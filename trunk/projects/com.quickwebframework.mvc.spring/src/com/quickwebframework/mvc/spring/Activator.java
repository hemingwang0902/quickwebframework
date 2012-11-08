package com.quickwebframework.mvc.spring;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.mvc.spring.service.impl.SpringMvcFrameworkService;
import com.quickwebframework.service.MvcFrameworkService;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		// 注册为服务
		MvcFrameworkService mvcFrameworkService = new SpringMvcFrameworkService();
		bundleContext.registerService(MvcFrameworkService.class.getName(),
				mvcFrameworkService, null);

		bundleContext.addBundleListener(new SynchronousBundleListener() {

			@Override
			public void bundleChanged(BundleEvent arg0) {
				int bundleEventType = arg0.getType();
				if (BundleEvent.STARTING == bundleEventType) {
					Bundle startingBundle = arg0.getBundle();
					// 添加到MVC框架中
					WebContext.addBundle(startingBundle);
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
