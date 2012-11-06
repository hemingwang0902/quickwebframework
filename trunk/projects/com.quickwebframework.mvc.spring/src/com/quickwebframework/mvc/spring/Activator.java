package com.quickwebframework.mvc.spring;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.mvc.spring.service.impl.SpringMvcFrameworkService;
import com.quickwebframework.mvc.spring.service.impl.TransactionDatabaseService;
import com.quickwebframework.service.DatabaseService;
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
