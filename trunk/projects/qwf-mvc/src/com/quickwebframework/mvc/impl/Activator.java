package com.quickwebframework.mvc.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.mvc.MvcContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// 注册view类型URL处理

		MvcContext.getInstance().init();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		MvcContext.getInstance().destory();

		Activator.context = null;
	}
}
