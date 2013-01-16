package com.quickwebframework.db.jdbc.mysql;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.db.jdbc.mysql.service.impl.DatabaseServiceImpl;
import com.quickwebframework.framework.WebContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	private static DatabaseServiceImpl databaseService;

	public static DatabaseServiceImpl getDatabaseService() {
		return databaseService;
	}

	public void start(BundleContext context) throws Exception {
		Activator.context = context;

		String jdbcPropertyFilePath = WebContext
				.getQwfConfig("qwf-db-jdbc.properties");
		if (jdbcPropertyFilePath == null || jdbcPropertyFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found qwf config: 'qwf-db-jdbc.properties'！");
		}
		jdbcPropertyFilePath = WebContext.getServletContext().getRealPath(
				jdbcPropertyFilePath);
		// 注册为数据库服务
		databaseService = new DatabaseServiceImpl(jdbcPropertyFilePath);
		databaseService.registerService();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		// 取消注册为数据库服务
		databaseService.unregisterService();
		Activator.context = null;
	}
}
