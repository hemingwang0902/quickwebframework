package com.quickwebframework.mvc.spring;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.SynchronousBundleListener;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.framework.WebContext;
import com.quickwebframework.mvc.spring.service.impl.SpringMvcFrameworkService;
import com.quickwebframework.mvc.spring.service.impl.TransactionDatabaseService;
import com.quickwebframework.service.DatabaseService;
import com.quickwebframework.service.MvcFrameworkService;
import com.quickwebframework.util.BundleUtil;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static Log log = LogFactory.getLog(SpringMvcFrameworkService.class);

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
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		// 注册为服务
		MvcFrameworkService mvcFrameworkService = new SpringMvcFrameworkService();
		bundleContext.registerService(MvcFrameworkService.class.getName(),
				mvcFrameworkService, null);

		// 注册为服务
		TransactionDatabaseService transactionDatabaseService = new TransactionDatabaseService(
				bundleContext);
		bundleContext.registerService(DatabaseService.class.getName(),
				transactionDatabaseService, null);

		bundleContext.addBundleListener(new SynchronousBundleListener() {

			@Override
			public void bundleChanged(BundleEvent arg0) {
				int bundleEventType = arg0.getType();
				if (BundleEvent.STARTING == bundleEventType) {
					Bundle startingBundle = arg0.getBundle();
					WebContext.registerWebApp(startingBundle);
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
