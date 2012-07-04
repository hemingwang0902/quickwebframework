package com.quickwebframework.db.jdbc.mysql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.service.DatabaseService;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		String jdbcPropertyFilePath = null;

		ServiceReference<?>[] serviceReferences = context
				.getServiceReferences(String.class.getName(),
						"(quickwebframework.pluginConfigFile=com.quickwebframework.db.jdbc.properties)");

		if (serviceReferences != null && serviceReferences.length > 0) {
			jdbcPropertyFilePath = (String) context
					.getService(serviceReferences[0]);
		}

		if (jdbcPropertyFilePath == null || jdbcPropertyFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found property 'quickwebframework.pluginConfigFile.com.quickwebframework.db.jdbc.properties'！");
		}

		File jdbcPropertyFile = new File(jdbcPropertyFilePath);
		if (!jdbcPropertyFile.exists() || !jdbcPropertyFile.isFile()) {
			String message = String.format("Config file [%s] not exist!",
					jdbcPropertyFilePath);
			throw new IOException(message);
		}

		InputStream inputStream = new FileInputStream(jdbcPropertyFile);
		Reader reader = new InputStreamReader(inputStream, "utf-8");
		Properties prop = new Properties();
		prop.load(reader);
		reader.close();
		inputStream.close();

		DatabaseService databaseService = new DatabaseServiceImpl(prop);
		context.registerService(DatabaseService.class.getName(),
				databaseService, null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
}
