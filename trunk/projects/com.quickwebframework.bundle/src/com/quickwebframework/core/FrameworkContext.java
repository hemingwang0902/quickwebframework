package com.quickwebframework.core;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.service.WebAppService;

public class FrameworkContext {
	/**
	 * 注册WEB应用
	 * 
	 * @param bundleContext
	 */
	public static void registerWebApp(BundleActivator bundleActivator,
			BundleContext bundleContext) {
		// 注册服务
		final Bundle currentBundle = bundleContext.getBundle();
		final ClassLoader currentClassLoader = bundleActivator.getClass().getClassLoader();

		bundleContext.registerService(WebAppService.class.getName(),
				new WebAppService() {
					@Override
					public Bundle getBundle() {
						return currentBundle;
					}

					@Override
					public ClassLoader getClassLoader() {
						return currentClassLoader;
					}
				}, null);
	}
}
