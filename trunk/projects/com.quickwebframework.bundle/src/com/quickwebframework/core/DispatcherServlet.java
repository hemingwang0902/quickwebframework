package com.quickwebframework.core;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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
import com.quickwebframework.service.WebSettingService;
import com.quickwebframework.service.core.PluginService;

public class DispatcherServlet {
	private static Log log = LogFactory.getLog(DispatcherServlet.class);
	// Bundle上下文
	private BundleContext bundleContext;
	// 注册过滤器，线程的服务
	private PluginService pluginService;

	// 视图渲染服务
	private ViewRenderService viewRenderService;

	// 刷新渲染服务
	private void refreshViewRenderService() {
		ServiceReference viewRenderServiceReference = bundleContext
				.getServiceReference(ViewRenderService.class.getName());
		if (viewRenderServiceReference == null) {
			viewRenderService = null;
		} else {
			viewRenderService = (ViewRenderService) bundleContext
					.getService(viewRenderServiceReference);
		}
	}

	// WEB设置服务
	private WebSettingService webSettingService;

	// 刷新WEB设置服务
	private void refreshWebSettingService() {
		ServiceReference serviceReference = bundleContext
				.getServiceReference(WebSettingService.class.getName());
		if (serviceReference == null) {
			webSettingService = null;
			return;
		}
		webSettingService = (WebSettingService) bundleContext
				.getService(serviceReference);
	}

	// MVC框架服务
	private MvcFrameworkService mvcFrameworkService;

	private void refreshMvcFrameworkService() {
		ServiceReference serviceReference = bundleContext
				.getServiceReference(MvcFrameworkService.class.getName());
		if (serviceReference == null) {
			mvcFrameworkService = null;
			return;
		}
		mvcFrameworkService = (MvcFrameworkService) bundleContext
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
			if (viewRenderService != null) {
				// 渲染视图
				viewRenderService.renderView(mav.getWebAppService().getBundle()
						.getSymbolicName(), mav.getViewName(), request,
						response);
			} else {
				response.sendError(500,
						"[com.quickwebframework.core.DispatcherServlet] cannot found ViewRender!");
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public DispatcherServlet(final BundleContext bundleContext,
			PluginService pluginService) {
		this.bundleContext = bundleContext;
		this.pluginService = pluginService;

		final Bundle currentBundle = bundleContext.getBundle();

		// 刷新视图渲染器
		refreshViewRenderService();
		// 刷新WEB设置服务
		refreshWebSettingService();
		// 刷新MVC框架服务
		refreshMvcFrameworkService();

		try {
			ServiceReference[] serviceReferences = bundleContext
					.getServiceReferences(WebAppService.class.getName(), null);
			if (serviceReferences != null) {
				for (ServiceReference serviceReference : serviceReferences) {
					WebAppService webAppService = (WebAppService) bundleContext
							.getService(serviceReference);
					mvcFrameworkService.addWebApp(webAppService);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

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
						.contains(WebSettingService.class.getName())) {
					refreshWebSettingService();
				} else if (serviceReferenceName
						.contains(MvcFrameworkService.class.getName())) {
					refreshMvcFrameworkService();
				}

				int serviceEventType = arg0.getType();
				if (serviceEventType == ServiceEvent.REGISTERED) {
					log.info(String.format("[%s]插件的[%s]服务已注册", arg0
							.getServiceReference().getBundle()
							.getSymbolicName(), arg0.getServiceReference()));

					ServiceReference serviceReference = arg0
							.getServiceReference();
					Object obj = bundleContext.getService(serviceReference);
					// 如果上服务不是WebAppService
					if (!WebAppService.class.isInstance(obj))
						return;
					mvcFrameworkService.addWebApp((WebAppService) obj);
				} else if (serviceEventType == ServiceEvent.UNREGISTERING) {
					log.info(String.format("[%s]插件的[%s]服务正在取消注册", arg0
							.getServiceReference().getBundle()
							.getSymbolicName(), arg0.getServiceReference()));

					ServiceReference serviceReference = arg0
							.getServiceReference();

					// 如果卸载的插件就是自己
					if (currentBundle.equals(serviceReference.getBundle()))
						return;

					Object obj = bundleContext.getService(serviceReference);
					// 如果上服务不是ControllerService
					if (!WebAppService.class.isInstance(obj))
						return;
					mvcFrameworkService.removeWebApp((WebAppService) obj);
				}
			}
		});
	}

	private void handleUrlNotFound(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		if (webSettingService == null
				|| webSettingService.getUrlNotFoundHandleServlet() == null)
			response.sendError(404, "URL " + request.getRequestURI()
					+ " not found!");
		else
			webSettingService.getUrlNotFoundHandleServlet().service(request,
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
				MvcModelAndView mav = mvcFrameworkService.handle(req, rep,
						bundleName, methodName);
				if (mav == null) {
					return;
				}
				String viewName = mav.getViewName();

				// 如果视图不为空
				if (viewName != null) {
					renderView(mav, req, rep);
				}
			} catch (Exception ex) {
				if (webSettingService == null)
					throw ex;

				HandlerExceptionResolver resolver = webSettingService
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
		if (webSettingService == null
				|| webSettingService.getRootUrlHandleServlet() == null) {
			rep.getWriter()
					.write("<html><head><title>Powered by QuickWebFramework</title></head><body>Welcome to use <a href=\"http://quickwebframework.com\">QuickWebFramework</a>!</body></html>");
			return;
		}
		HttpServletRequest req = new PluginHttpServletRequest(response);
		webSettingService.getRootUrlHandleServlet().service(req, rep);
	}

	// 得到资源
	public InputStream doGetResource(Object request, Object response,
			String bundleName, String resourcePath) throws IOException {
		WebAppService webAppService = mvcFrameworkService
				.getWebAppService(bundleName);
		if (webAppService == null)
			return null;
		return webAppService.getBundle().getResource(resourcePath).openStream();
	}

	// 处理过滤器,返回值是是否继续处理其他的过滤器
	public boolean doFilter(Object request, Object response, Object chain)
			throws IOException, ServletException {
		HttpServletRequest req = new PluginHttpServletRequest(request);
		HttpServletResponse rep = new PluginHttpServletResponse(response);
		BooleanFilterChain booleanFilterChain = new BooleanFilterChain();

		for (Filter filter : pluginService.getFilterList()) {
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
