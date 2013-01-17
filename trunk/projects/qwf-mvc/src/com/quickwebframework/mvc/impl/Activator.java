package com.quickwebframework.mvc.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.mvc.MvcContext;
import com.quickwebframework.mvc.servlet.MvcViewTypeServlet;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private MvcViewTypeServlet mvcDispatchServlet;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		String viewTypeName = WebContext
				.getQwfConfig(MvcViewTypeServlet.VIEW_TYPE_NAME_PROPERTY_KEY);
		// 注册视图类型处理Servlet
		if (viewTypeName == null) {
			throw new RuntimeException(
					"未找到配置[qwf.config.qwf-mvc.MvcDispatchServlet.viewTypeName]!");
		}
		mvcDispatchServlet = new MvcViewTypeServlet(viewTypeName);
		WebContext.registerViewTypeServlet(mvcDispatchServlet);
		MvcContext.getInstance().init();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		MvcContext.getInstance().destory();
		// 取消注册视图类型处理Servlet
		WebContext.unregisterViewTypeServlet(mvcDispatchServlet);
		Activator.context = null;
	}
}
