package com.quickwebframework.view.struts2.support;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;
import com.quickwebframework.view.struts2.servlet.Struts2ViewTypeServlet;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static ViewTypeServlet servlet;

	public static BundleContext getContext() {
		return context;
	}

	public static ServletContext getServletContext() {
		return WebContext.getServletContext();
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		String viewTypeName = WebContext
				.getQwfConfig(Struts2ViewTypeServlet.VIEW_TYPE_NAME_PROPERTY_KEY);
		if (viewTypeName == null || viewTypeName.isEmpty()) {
			throw new RuntimeException(String.format(
					"未找到配置[qwf.config.%s]，Spring MVC框架启动失败！",
					Struts2ViewTypeServlet.VIEW_TYPE_NAME_PROPERTY_KEY));
		}
		servlet = new Struts2ViewTypeServlet(viewTypeName);
		WebContext.registerViewTypeServlet(servlet);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		WebContext.unregisterViewTypeServlet(servlet);
		Activator.context = null;
	}
}
