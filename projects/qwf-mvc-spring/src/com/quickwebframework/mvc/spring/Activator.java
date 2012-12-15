package com.quickwebframework.mvc.spring;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;

import com.quickwebframework.mvc.spring.service.impl.SpringMvcFrameworkService;
import com.quickwebframework.service.MvcFrameworkService;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private MvcFrameworkService mvcFrameworkService;
	private ServiceRegistration<?> mvcFrameworkServiceRegistration;
	private BundleListener bundleListener;

	public static BundleContext getContext() {
		return context;
	}

	public Activator() {
		bundleListener = new SynchronousBundleListener() {

			@Override
			public void bundleChanged(BundleEvent arg0) {
				int bundleEventType = arg0.getType();
				if (BundleEvent.STARTING == bundleEventType) {
					Bundle startingBundle = arg0.getBundle();
					// 添加到MVC框架中
					mvcFrameworkService.addBundle(startingBundle);
				}
			}
		};
	}

	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		// 注册MVC框架服务
		mvcFrameworkService = new SpringMvcFrameworkService();
		mvcFrameworkServiceRegistration = bundleContext.registerService(
				MvcFrameworkService.class.getName(), mvcFrameworkService, null);
		// 添加插件监听器
		bundleContext.addBundleListener(bundleListener);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		// 取消注册MVC框架服务
		mvcFrameworkServiceRegistration.unregister();
		// 移除插件监听器
		bundleContext.removeBundleListener(bundleListener);
		Activator.context = null;
	}
}
