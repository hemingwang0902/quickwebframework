package com.quickwebframework.db.orm.spring.jdbc;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.quickwebframework.db.orm.spring.jdbc.service.DatabaseService;
import com.quickwebframework.db.orm.spring.jdbc.service.impl.DatabaseServiceImpl;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private ServiceRegistration<?> databaseServiceRegistration;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		// 注册数据库服务
		DatabaseService databaseService = new DatabaseServiceImpl(context);
		databaseServiceRegistration = context.registerService(
				DatabaseService.class.getName(), databaseService, null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		// 取消注册数据库服务
		databaseServiceRegistration.unregister();
		Activator.context = null;
	}
}
