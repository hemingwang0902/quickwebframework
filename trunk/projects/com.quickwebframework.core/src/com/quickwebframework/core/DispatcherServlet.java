package com.quickwebframework.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
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
import com.quickwebframework.service.MvcFrameworkService;
import com.quickwebframework.service.WebAppService;
import com.quickwebframework.service.ViewRenderService;

public class DispatcherServlet extends HttpServlet implements
		javax.servlet.Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6484809258029142503L;

	private static Log log = LogFactory.getLog(DispatcherServlet.class);
	public static final String ARG_BUNDLE_NAME = "com.quickwebframework.util.ARG_BUNDLE_NAME";
	public static final String ARG_METHOD_NAME = "com.quickwebframework.util.ARG_METHOD_NAME";
	public static final String ARG_RESOURCE_PATH = "com.quickwebframework.util.ARG_RESOURCE_PATH";
	public static final String ARG_RESOURCE_INPUTSTREAM = "com.quickwebframework.web.servlet.PluginResourceDispatcherServlet.ARG_RESOURCE_INPUTSTREAM";

	// Bundle上下文
	private BundleContext bundleContext;

	// 刷新渲染服务
	private void refreshViewRenderService() {
		try {
			ServiceReference<?> viewRenderServiceReference = bundleContext
					.getServiceReference(ViewRenderService.class.getName());
			if (viewRenderServiceReference == null) {
				FrameworkContext.viewRenderService = null;
			} else {
				FrameworkContext.viewRenderService = (ViewRenderService) bundleContext
						.getService(viewRenderServiceReference);
			}
		} catch (Exception ex) {
		}
	}

	// 刷新MVC框架服务
	private void refreshMvcFrameworkService() {
		try {
			ServiceReference<?> serviceReference = bundleContext
					.getServiceReference(MvcFrameworkService.class.getName());
			if (serviceReference == null) {
				FrameworkContext.mvcFrameworkService = null;
				return;
			}
			FrameworkContext.mvcFrameworkService = (MvcFrameworkService) bundleContext
					.getService(serviceReference);
		} catch (Exception ex) {
			return;
		}
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

	private void processHttp(HttpServletRequest request,
			HttpServletResponse response, String bundleName, String methodName) {
		try {
			// 如果插件名称为null或空字符串
			if (bundleName == null || bundleName.isEmpty()) {
				handleUrlNotFound(request, response);
				return;
			}

			try {
				MvcModelAndView mav = FrameworkContext.mvcFrameworkService
						.handle(request, response, bundleName, methodName);
				if (mav == null) {
					return;
				}
				String viewName = mav.getViewName();

				// 如果视图不为空
				if (viewName != null) {
					renderView(mav, request, response);
				}
			} catch (Exception ex) {
				HandlerExceptionResolver resolver = FrameworkContext
						.getHandlerExceptionResolver();

				if (resolver == null)
					throw ex;
				// 解决处理器异常
				resolver.resolveException(request, response, ex);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// 处理HTTP请求
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String requestURIWithoutContextPath = request.getRequestURI()
				.substring(request.getContextPath().length());
		if (requestURIWithoutContextPath.isEmpty())
			requestURIWithoutContextPath = "/";

		// 如果是根路径
		if ("/".equals(requestURIWithoutContextPath)) {
			serviceRootUrl(request, response);
			return;
		}

		String bundleName = request.getAttribute(ARG_BUNDLE_NAME).toString();
		Object methodNameObject = request.getAttribute(ARG_METHOD_NAME);
		Object resourcePathObject = request.getAttribute(ARG_RESOURCE_PATH);
		// 如果是视图
		if (methodNameObject != null) {
			String methodName = methodNameObject.toString();
			processHttp(request, response, bundleName, methodName);
		}
		// 如果是资源
		else if (resourcePathObject != null) {
			String resourcePath = resourcePathObject.toString();
			request.setAttribute(ARG_RESOURCE_INPUTSTREAM,
					getBundleResource(bundleName, resourcePath));
		}
	}

	// 处理根URL："/"请求
	private void serviceRootUrl(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		if (FrameworkContext.getRootUrlHandleServlet() == null) {
			response.setContentType("text/html;charset=utf-8");
			StringBuilder sb = new StringBuilder();
			sb.append("<html><head><title>Powered by QuickWebFramework</title></head><body>Welcome to use <a href=\"http://quickwebframework.com\">QuickWebFramework</a>!You can manage bundles in the <a href=\"qwf/index\">Bundle Manage Page</a>!");
			if (FrameworkContext.mvcFrameworkService != null) {
				Map<String, List<String>> map = FrameworkContext.mvcFrameworkService
						.getBundleNameUrlListMap();
				sb.append("<table>");
				for (String bundleName : map.keySet()) {
					sb.append("<tr><td><b>" + bundleName + "</b></td></tr>");
					for (String url : map.get(bundleName)) {
						sb.append("<tr><td><a style=\"margin-left:20px\" href=\""
								+ url + "\">" + url + "</a></td></tr>");
					}
				}
				sb.append("</table>");
			}
			sb.append("</body></html>");
			response.getWriter().write(sb.toString());
			return;
		}
		FrameworkContext.getRootUrlHandleServlet().service(request, response);
	}

	// 得到Bundle资源
	private InputStream getBundleResource(String bundleName, String resourcePath)
			throws IOException {
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

	public class ArrayFilterChain implements FilterChain {
		private Filter[] filters;
		private int filterIndex = -1;
		private int filterCount = 0;

		public Filter lastFilter;

		public boolean isContinueFilterChain() {
			return filterIndex >= filterCount;
		}

		public ArrayFilterChain(Filter[] filters) {
			if (filters == null)
				return;
			this.filters = filters;
			filterCount = filters.length;
		}

		@Override
		public void doFilter(ServletRequest arg0, ServletResponse arg1)
				throws IOException, ServletException {
			if (filters == null)
				return;

			filterIndex++;

			// 如果过滤器已使用完
			if (filterIndex >= filterCount)
				return;

			lastFilter = filters[filterIndex];
			lastFilter.doFilter(arg0, arg1, this);
		}
	}

	// 执行过滤
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		ArrayFilterChain arrayFilterChain = new ArrayFilterChain(
				FrameworkContext.getFilterList().toArray(new Filter[0]));
		arrayFilterChain.doFilter(request, response);
		if (arrayFilterChain.isContinueFilterChain())
			filterChain.doFilter(request, response);
		else
			log.info("过滤器链未全部执行完成，在执行完过滤器[" + arrayFilterChain.lastFilter
					+ "]后断开。");
	}

	// 过滤器初始化
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
