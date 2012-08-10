package com.quickwebframework.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.entity.HandlerExceptionResolver;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.entity.MvcModelAndView;
import com.quickwebframework.proxy.PluginHttpServletRequest;
import com.quickwebframework.proxy.PluginHttpServletResponse;
import com.quickwebframework.service.MvcFrameworkService;
import com.quickwebframework.service.WebAppService;
import com.quickwebframework.service.ViewRenderService;

public class DispatcherServlet {
	private static Log log = LogFactory.getLog(DispatcherServlet.class);
	// Bundle上下文
	private BundleContext bundleContext;

	// 刷新渲染服务
	private void refreshViewRenderService() {
		ServiceReference<?> viewRenderServiceReference = bundleContext
				.getServiceReference(ViewRenderService.class.getName());
		if (viewRenderServiceReference == null) {
			FrameworkContext.viewRenderService = null;
		} else {
			FrameworkContext.viewRenderService = (ViewRenderService) bundleContext
					.getService(viewRenderServiceReference);
		}
	}

	// 刷新MVC框架服务
	private void refreshMvcFrameworkService() {
		ServiceReference<?> serviceReference = bundleContext
				.getServiceReference(MvcFrameworkService.class.getName());
		if (serviceReference == null) {
			FrameworkContext.mvcFrameworkService = null;
			return;
		}
		FrameworkContext.mvcFrameworkService = (MvcFrameworkService) bundleContext
				.getService(serviceReference);
	}

	/**
	 * 渲染视图
	 * 
	 * @return
	 */
	public void renderView(MvcModelAndView mav, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (FrameworkContext.viewRenderService != null) {
				// 渲染视图
				FrameworkContext.viewRenderService.renderView(mav
						.getWebAppService().getBundle().getSymbolicName(),
						mav.getViewName(), request, response);
			} else {
				response.sendError(500,
						"[com.quickwebframework.core.DispatcherServlet] cannot found ViewRender!");
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public DispatcherServlet(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		final Bundle currentBundle = bundleContext.getBundle();

		// 刷新视图渲染器
		refreshViewRenderService();
		// 刷新MVC框架服务
		refreshMvcFrameworkService();

		bundleContext.addBundleListener(new BundleListener() {
			@Override
			public void bundleChanged(BundleEvent arg0) {
				Bundle bundle = arg0.getBundle();
				String bundleName = bundle.getSymbolicName();
				int bundleEventType = arg0.getType();
				// 如果是已经停止
				if (bundleEventType == BundleEvent.STOPPED) {
					if (FrameworkContext.mvcFrameworkService == null)
						return;
					WebAppService webAppService = FrameworkContext.mvcFrameworkService
							.getWebAppService(bundleName);
					if (webAppService != null) {
						FrameworkContext.mvcFrameworkService
								.removeWebApp(webAppService);
					}
				}
			}
		});

		bundleContext.addServiceListener(new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent arg0) {
				String serviceReferenceName = arg0.getServiceReference()
						.toString();
				// 如果视图渲染器服务改变，刷新视图渲染器
				if (serviceReferenceName.contains(ViewRenderService.class
						.getName())) {
					refreshViewRenderService();
				} else if (serviceReferenceName
						.contains(MvcFrameworkService.class.getName())) {
					refreshMvcFrameworkService();
				}

				int serviceEventType = arg0.getType();
				if (serviceEventType == ServiceEvent.REGISTERED) {
					log.info(String.format("[%s]插件的[%s]服务已注册", arg0
							.getServiceReference().getBundle()
							.getSymbolicName(), arg0.getServiceReference()));
				} else if (serviceEventType == ServiceEvent.UNREGISTERING) {
					log.info(String.format("[%s]插件的[%s]服务正在取消注册", arg0
							.getServiceReference().getBundle()
							.getSymbolicName(), arg0.getServiceReference()));

					ServiceReference<?> serviceReference = arg0
							.getServiceReference();

					// 如果卸载的插件就是自己
					if (currentBundle.equals(serviceReference.getBundle()))
						return;
				}
			}
		});
	}

	private void handleUrlNotFound(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		if (FrameworkContext.getUrlNotFoundHandleServlet() == null)
			response.sendError(404, "URL " + request.getRequestURI()
					+ " not found!");
		else
			FrameworkContext.getUrlNotFoundHandleServlet().service(request,
					response);
	}

	private void processHttp(Object request, Object response,
			String bundleName, String methodName) {
		try {
			HttpServletRequest req = new PluginHttpServletRequest(request);
			HttpServletResponse rep = new PluginHttpServletResponse(response);

			// 如果插件名称为null或空字符串
			if (bundleName == null || bundleName.isEmpty()) {
				handleUrlNotFound(req, rep);
				return;
			}

			try {
				MvcModelAndView mav = FrameworkContext.mvcFrameworkService
						.handle(req, rep, bundleName, methodName);
				if (mav == null) {
					return;
				}
				String viewName = mav.getViewName();

				// 如果视图不为空
				if (viewName != null) {
					renderView(mav, req, rep);
				}
			} catch (Exception ex) {
				HandlerExceptionResolver resolver = FrameworkContext
						.getHandlerExceptionResolver();

				if (resolver == null)
					throw ex;
				// 解决处理器异常
				resolver.resolveException(req, rep, ex);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// 处理HTTP请求
	public void service(Object request, Object response, String bundleName,
			String methodName) {
		processHttp(request, response, bundleName, methodName);
	}

	// 处理根URL："/"请求
	public void serviceRootUrl(Object request, Object response)
			throws IOException, ServletException {
		HttpServletResponse rep = new PluginHttpServletResponse(response);
		if (FrameworkContext.getRootUrlHandleServlet() == null) {
			rep.setContentType("text/html;charset=utf-8");
			StringBuilder sb = new StringBuilder();
			sb.append("<html><head><title>Powered by QuickWebFramework</title></head><body>Welcome to use <a href=\"http://quickwebframework.com\">QuickWebFramework</a>!You can manage bundles in the <a href=\"qwf/index\">Bundle Manage Page</a>!");
			if (FrameworkContext.mvcFrameworkService != null) {
				Map<String, List<String>> map = FrameworkContext.mvcFrameworkService
						.getBundleNameUrlListMap();
				sb.append("<table>");
				for (String bundleName : map.keySet()) {
					sb.append("<tr><td><b>" + bundleName + "</b></td></tr>");
					for (String url : map.get(bundleName)) {
						sb.append("<tr><td><a style=\"margin-left:20px\" href=\"" + url + "\">" + url
								+ "</a></td></tr>");
					}
				}
				sb.append("</table>");
			}
			sb.append("</body></html>");
			rep.getWriter().write(sb.toString());
			return;
		}
		HttpServletRequest req = new PluginHttpServletRequest(response);
		FrameworkContext.getRootUrlHandleServlet().service(req, rep);
	}

	// 得到资源
	public InputStream doGetResource(Object request, Object response,
			String bundleName, String resourcePath) throws IOException {
		if (FrameworkContext.mvcFrameworkService == null)
			return null;

		WebAppService webAppService = FrameworkContext.mvcFrameworkService
				.getWebAppService(bundleName);
		if (webAppService == null)
			return null;
		URL resourceUrl = webAppService.getBundle().getResource(resourcePath);
		if (resourceUrl == null)
			return null;
		return resourceUrl.openStream();
	}

	// 处理过滤器,返回值是是否继续处理其他的过滤器
	public boolean doFilter(Object request, Object response, Object chain)
			throws IOException, ServletException {
		HttpServletRequest req = new PluginHttpServletRequest(request);
		HttpServletResponse rep = new PluginHttpServletResponse(response);
		BooleanFilterChain booleanFilterChain = new BooleanFilterChain();

		for (Filter filter : FrameworkContext.getFilterList()) {
			filter.doFilter(req, rep, booleanFilterChain);
			if (!booleanFilterChain.isContinueFilter)
				return false;
		}
		return true;
	}

	public class BooleanFilterChain implements FilterChain {
		// 是否继续过滤
		private boolean isContinueFilter = false;

		public boolean isContinueFilter() {
			return isContinueFilter;
		}

		public void setContinueFilter(boolean isContinueFilter) {
			this.isContinueFilter = isContinueFilter;
		}

		@Override
		public void doFilter(ServletRequest arg0, ServletResponse arg1)
				throws IOException, ServletException {
			isContinueFilter = true;
		}

	}
}
