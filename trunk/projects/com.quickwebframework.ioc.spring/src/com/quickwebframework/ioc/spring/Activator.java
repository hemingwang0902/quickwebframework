package com.quickwebframework.ioc.spring;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

import com.quickwebframework.ioc.spring.service.impl.SpringIocFrameworkService;
import com.quickwebframework.service.IocFrameworkService;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		final SpringIocFrameworkService springIocFrameworkService = new SpringIocFrameworkService();

		// 注册为IocFrameworkService服务
		bundleContext.registerService(IocFrameworkService.class,
				springIocFrameworkService, null);

		bundleContext.addBundleListener(new SynchronousBundleListener() {

			@Override
			public void bundleChanged(BundleEvent arg0) {
				Bundle startingBundle = arg0.getBundle();
				int bundleEventType = arg0.getType();
				if (BundleEvent.STARTING == bundleEventType) {
					springIocFrameworkService.addBundle(startingBundle);
				} else if (BundleEvent.STOPPING == bundleEventType) {
					springIocFrameworkService.removeBundle(startingBundle);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
