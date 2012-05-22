package com.quickwebframework.db.orm.quickorm;

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

import com.quickwebframework.db.orm.quickorm.service.DatabaseService;
import com.quickwebframework.db.orm.quickorm.service.impl.DatabaseServiceImpl;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		String quickormPropertyFilePath = null;

		ServiceReference[] serviceReferences = context
				.getServiceReferences(
						String.class.getName(),
						"(quickwebframework.pluginConfigFile=com.quickwebframework.db.quickorm.properties)");

		if (serviceReferences != null && serviceReferences.length > 0) {
			quickormPropertyFilePath = (String) context
					.getService(serviceReferences[0]);
		}

		if (quickormPropertyFilePath == null
				|| quickormPropertyFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found property 'quickwebframework.pluginConfigFile.com.quickwebframework.db.quickorm.properties'！");
		}

		File quickormPropertyFile = new File(quickormPropertyFilePath);
		if (!quickormPropertyFile.exists() || !quickormPropertyFile.isFile()) {
			String message = String.format("Config file [%s] not exist!",
					quickormPropertyFilePath);
			throw new IOException(message);
		}

		InputStream inputStream = new FileInputStream(quickormPropertyFile);
		Reader reader = new InputStreamReader(inputStream, "utf-8");
		Properties prop = new Properties();
		prop.load(reader);
		reader.close();
		inputStream.close();

		// 注册为服务
		DatabaseService databaseService = new DatabaseServiceImpl(context, prop);
		context.registerService(DatabaseService.class.getName(),
				databaseService, null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
}
