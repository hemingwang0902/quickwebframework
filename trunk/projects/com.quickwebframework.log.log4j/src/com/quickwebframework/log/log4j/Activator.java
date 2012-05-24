package com.quickwebframework.log.log4j;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.service.LogService;

public class Activator implements BundleActivator {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		String log4jConfigFilePath = null;

		ServiceReference[] serviceReferences = context
				.getServiceReferences(
						String.class.getName(),
						"(quickwebframework.pluginConfigFile=com.quickwebframework.log.log4j.properties)");
		if (serviceReferences != null && serviceReferences.length > 0) {
			log4jConfigFilePath = (String) context
					.getService(serviceReferences[0]);
		}

		if (log4jConfigFilePath == null || log4jConfigFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found property 'quickwebframework.pluginConfigFile.com.quickwebframework.log.log4j.properties'！");
		} else {
			PropertyConfigurator.configure(log4jConfigFilePath);
		}

		// 注册为服务
		LogService logService = new LogServiceImpl();
		context.registerService(LogService.class.getName(), logService, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		Properties prop = new Properties();
		prop.setProperty("log4j.rootLogger", "INFO,C");
		prop.setProperty("log4j.appender.C", "org.apache.log4j.ConsoleAppender");
		prop.setProperty("log4j.appender.C.layout",
				"org.apache.log4j.PatternLayout");
		prop.setProperty("log4j.appender.C.layout.ConversionPattern",
				"%-d{yyyy-MM-dd HH:mm:ss,SSS} [%c]-[%p] %m%n");

		PropertyConfigurator.configure(prop);
	}
}
