package com.quickwebframework.mvc.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.mvc.MvcContext;
import com.quickwebframework.mvc.servlet.MvcDispatchServlet;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// 注册视图类型处理Servlet
		WebContext.registerViewTypeServlet("mvc", new MvcDispatchServlet());

		MvcContext.getInstance().init();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		MvcContext.getInstance().destory();
		// 取消注册视图类型处理Servlet
		WebContext.unregisterViewTypeServlet("mvc");
		Activator.context = null;
	}
}
