package com.quickwebframework.viewrender.freemarker;

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

import com.quickwebframework.service.ViewRenderService;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {

		String freemarkerPropertyFilePath = null;

		ServiceReference[] serviceReferences = context
				.getServiceReferences(
						String.class.getName(),
						"(quickwebframework.pluginConfigFile=com.quickwebframework.viewrender.freemarker.peoperties)");

		if (serviceReferences != null && serviceReferences.length > 0) {
			freemarkerPropertyFilePath = (String) context
					.getService(serviceReferences[0]);
		}

		if (freemarkerPropertyFilePath == null
				|| freemarkerPropertyFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found property 'quickwebframework.pluginConfigFile.com.quickwebframework.db.jdbc.properties'！");
		}

		File freemarkerPropertyFile = new File(freemarkerPropertyFilePath);
		if (!freemarkerPropertyFile.exists()
				|| !freemarkerPropertyFile.isFile()) {
			String message = String.format("Config file [%s] not exist!",
					freemarkerPropertyFilePath);
			throw new IOException(message);
		}

		InputStream inputStream = new FileInputStream(freemarkerPropertyFile);
		Reader reader = new InputStreamReader(inputStream, "utf-8");
		Properties prop = new Properties();
		prop.load(reader);
		reader.close();
		inputStream.close();

		ViewRenderService viewRenderService = new ViewRenderServiceImpl(
				context, prop);
		context.registerService(ViewRenderService.class.getName(),
				viewRenderService, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}
}
