package com.quickwebframework.db.orm.spring.jdbc;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.db.orm.spring.jdbc.service.DatabaseService;
import com.quickwebframework.db.orm.spring.jdbc.service.impl.DatabaseServiceImpl;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		DatabaseService databaseService = new DatabaseServiceImpl(bundleContext);
		bundleContext.registerService(DatabaseService.class.getName(),
				databaseService, null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
}
