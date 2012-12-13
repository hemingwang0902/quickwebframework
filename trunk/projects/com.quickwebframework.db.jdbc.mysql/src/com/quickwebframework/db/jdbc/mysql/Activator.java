package com.quickwebframework.db.jdbc.mysql;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

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

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		String jdbcPropertyFilePath = null;

		ServiceReference<?>[] serviceReferences = context
				.getServiceReferences(String.class.getName(),
						"(quickwebframework.config=com.quickwebframework.db.jdbc.properties)");

		if (serviceReferences != null && serviceReferences.length > 0) {
			jdbcPropertyFilePath = (String) context
					.getService(serviceReferences[0]);
		}

		if (jdbcPropertyFilePath == null || jdbcPropertyFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found property 'quickwebframework.config.com.quickwebframework.db.jdbc.properties'！");
		}
		jdbcPropertyFilePath = WebContext.getServletContext()
				.getRealPath(jdbcPropertyFilePath);
		databaseService = new DatabaseServiceImpl(jdbcPropertyFilePath);
		databaseService.registerService();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
}
