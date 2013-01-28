package com.quickwebframework.framework;

import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceRegistration;

import com.quickwebframework.bridge.LogBridge;
import com.quickwebframework.core.Activator;

public class LogContext extends FrameworkContext {
	private static LogContext instance;

	protected static LogContext getInstance() {
		if (instance == null)
			instance = new LogContext();
		return instance;
	}

	// ======变量部分开始
	private ServiceRegistration<?> logBridgeServiceRegistration;

	// ======变量部分结束

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getContext();
	}

	@Override
	protected void init(int arg) {
		BundleContext bundleContext = getBundleContext();

		// 加载log4j的配置
		String log4jConfigFilePath = WebContext
				.getQwfConfig("qwf-log-log4j.properties");
		// 如果配置文件为空
		if (log4jConfigFilePath == null || log4jConfigFilePath.isEmpty()) {

		} else {
			log4jConfigFilePath = WebContext.getServletContext().getRealPath(
					log4jConfigFilePath);
			// 让log4j重新加载配置文件
			PropertyConfigurator.configure(log4jConfigFilePath);
		}

		// 注册日志桥接对象
		logBridgeServiceRegistration = bundleContext.registerService(
				LogBridge.class.getName(), new LogBridge(), null);
	}

	@Override
	protected void destory(int arg) {
		// 取消注册日志桥接对象
		logBridgeServiceRegistration.unregister();
	}

	@Override
	protected void bundleChanged(BundleEvent event) {
	}

	@Override
	protected void serviceChanged(ServiceEvent event) {

	}
}
