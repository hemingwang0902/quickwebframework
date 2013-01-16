package com.quickwebframework.mvc.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.mvc.MvcContext;
import com.quickwebframework.mvc.servlet.MvcDispatchServlet;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private MvcDispatchServlet mvcDispatchServlet;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// 注册视图类型处理Servlet
		mvcDispatchServlet = new MvcDispatchServlet();
		String mvcViewTypeName = mvcDispatchServlet.getViewTypeName();
		if (mvcViewTypeName == null) {
			throw new RuntimeException(
					"未找到配置[qwf.config.qwf-mvc.MvcDispatchServlet.viewTypeName]!");
		}
		WebContext.registerViewTypeServlet(mvcViewTypeName, mvcDispatchServlet);
		MvcContext.getInstance().init();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		MvcContext.getInstance().destory();
		// 取消注册视图类型处理Servlet
		WebContext.unregisterViewTypeServlet(mvcDispatchServlet
				.getViewTypeName());
		Activator.context = null;
	}
}
