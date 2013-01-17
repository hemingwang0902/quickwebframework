package com.quickwebframework.mvc.spring.support;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.mvc.spring.SpringMvcContext;
import com.quickwebframework.mvc.spring.servlet.SpringMvcViewTypeServlet;
import com.quickwebframework.servlet.ViewTypeServlet;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static ViewTypeServlet servlet;

	public static BundleContext getContext() {
		return context;
	}

	public static ViewTypeServlet getViewTypeServlet() {
		return servlet;
	}

	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		SpringMvcContext.getInstance().init();

		String viewTypeName = WebContext
				.getQwfConfig(SpringMvcViewTypeServlet.VIEW_TYPE_NAME_PROPERTY_KEY);
		if (viewTypeName == null || viewTypeName.isEmpty()) {
			throw new RuntimeException(String.format(
					"未找到配置[qwf.config.%s]，Spring MVC框架启动失败！",
					SpringMvcViewTypeServlet.VIEW_TYPE_NAME_PROPERTY_KEY));
		}
		servlet = new SpringMvcViewTypeServlet(viewTypeName);
		WebContext.registerViewTypeServlet(servlet);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		WebContext.unregisterViewTypeServlet(servlet);
		SpringMvcContext.getInstance().destory();
		Activator.context = null;
	}
}
