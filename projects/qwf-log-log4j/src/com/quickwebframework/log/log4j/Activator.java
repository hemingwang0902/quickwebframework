package com.quickwebframework.log.log4j;

import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.log.log4j.service.impl.LogServiceImpl;
import com.quickwebframework.service.LogService;

public class Activator implements BundleActivator {

	private ServiceRegistration<?> logServiceRegistration;

	public void start(BundleContext context) throws Exception {
		String log4jConfigFilePath = WebContext
				.getQwfConfig("qwf-log-log4j.properties");
		if (log4jConfigFilePath == null || log4jConfigFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found qwf config 'qwf-log-log4j.properties'！");
		}
		log4jConfigFilePath = WebContext.getServletContext().getRealPath(
				log4jConfigFilePath);
		// 让log4j重新加载配置文件
		PropertyConfigurator.configure(log4jConfigFilePath);

		// 注册日志服务
		LogService logService = new LogServiceImpl();
		logServiceRegistration = context.registerService(
				LogService.class.getName(), logService, null);
	}

	public void stop(BundleContext context) throws Exception {
		// 取消注册日志服务
		logServiceRegistration.unregister();
	}
}
