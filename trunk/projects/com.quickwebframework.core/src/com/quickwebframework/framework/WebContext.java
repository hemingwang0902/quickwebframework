package com.quickwebframework.framework;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

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
import com.quickwebframework.service.MvcFrameworkService;
import com.quickwebframework.service.ViewRenderService;

public class WebContext {
	private static Log log = LogFactory.getLog(WebContext.class);

	// MVC框架服务
	private static MvcFrameworkService mvcFrameworkService;

	public static MvcFrameworkService getMvcFrameworkService() {
		return mvcFrameworkService;
	}

	private static ServletContext servletContext;

	/**
	 * 得到ServletContext
	 * 
	 * @return
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}

	// 视图渲染服务
	private static ViewRenderService viewRenderService;

	public static ViewRenderService getViewRenderService() {
		return viewRenderService;
	}

	// 根URL处理Servlet
	private static HttpServlet rootUrlHandleServlet;

	public static HttpServlet getRootUrlHandleServlet() {
		return rootUrlHandleServlet;
	}

	public static void setRootUrlHandleServlet(HttpServlet rootUrlHandleServlet) {
		WebContext.rootUrlHandleServlet = rootUrlHandleServlet;
	}

	// URL未找到处理Servlet
	private static HttpServlet urlNotFoundHandleServlet;

	public static HttpServlet getUrlNotFoundHandleServlet() {
		return urlNotFoundHandleServlet;
	}

	public static void setUrlNotFoundHandleServlet(
			HttpServlet urlNotFoundHandleServlet) {
		WebContext.urlNotFoundHandleServlet = urlNotFoundHandleServlet;
	}

	// 得到处理器异常解决器
	private static HandlerExceptionResolver handlerExceptionResolver;

	public static HandlerExceptionResolver getHandlerExceptionResolver() {
		return handlerExceptionResolver;
	}

	public static void setHandlerExceptionResolver(
			HandlerExceptionResolver handlerExceptionResolver) {
		WebContext.handlerExceptionResolver = handlerExceptionResolver;
	}

	/**
	 * 注册WEB应用
	 * 
	 * @param bundleContext
	 */
	public static void addBundle(Bundle bundle) {
		if (WebContext.mvcFrameworkService == null) {
			log.error("注册WebApp时，未发现有注册的MvcFrameworkService服务！");
			throw new RuntimeException(
					"注册WebApp时，未发现有注册的MvcFrameworkService服务！");
		}
		// 注册服务
		WebContext.mvcFrameworkService.addBundle(bundle);
	}

	// 刷新渲染服务
	private static void refreshViewRenderService(BundleContext bundleContext) {
		try {
			ServiceReference<?> viewRenderServiceReference = bundleContext
					.getServiceReference(ViewRenderService.class.getName());
			if (viewRenderServiceReference == null) {
				WebContext.viewRenderService = null;
			} else {
				WebContext.viewRenderService = (ViewRenderService) bundleContext
						.getService(viewRenderServiceReference);
			}
		} catch (Exception ex) {
		}
	}

	// 刷新MVC框架服务
	private static void refreshMvcFrameworkService(BundleContext bundleContext) {
		try {
			ServiceReference<?> serviceReference = bundleContext
					.getServiceReference(MvcFrameworkService.class.getName());
			if (serviceReference == null) {
				WebContext.mvcFrameworkService = null;
				return;
			}
			WebContext.mvcFrameworkService = (MvcFrameworkService) bundleContext
					.getService(serviceReference);
		} catch (Exception ex) {
			return;
		}
	}

	// 刷新ServletContext
	private static void refreshServletContextService(BundleContext bundleContext) {
		try {
			ServiceReference<?> serviceReference = bundleContext
					.getServiceReference(ServletContext.class.getName());
			if (serviceReference == null) {
				WebContext.servletContext = null;
				return;
			}
			WebContext.servletContext = (ServletContext) bundleContext
					.getService(serviceReference);
		} catch (Exception ex) {
			return;
		}
	}

	public static void init() {
		final BundleContext bundleContext = FrameworkContext.coreBundle
				.getBundleContext();
		// 刷新视图渲染器
		refreshViewRenderService(bundleContext);
		// 刷新MVC框架服务
		refreshMvcFrameworkService(bundleContext);
		// 刷新ServletContext
		refreshServletContextService(bundleContext);

		bundleContext.addBundleListener(new BundleListener() {
			@Override
			public void bundleChanged(BundleEvent arg0) {
				Bundle bundle = arg0.getBundle();
				int bundleEventType = arg0.getType();
				// 如果是已经停止
				if (bundleEventType == BundleEvent.STOPPED) {
					if (WebContext.mvcFrameworkService == null)
						return;
					WebContext.mvcFrameworkService.removeBundle(bundle);
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
					refreshViewRenderService(bundleContext);
				} else if (serviceReferenceName
						.contains(MvcFrameworkService.class.getName())) {
					refreshMvcFrameworkService(bundleContext);
				} else if (serviceReferenceName.contains(ServletContext.class
						.getName())) {
					refreshServletContextService(bundleContext);
				}
			}
		});
	}

}