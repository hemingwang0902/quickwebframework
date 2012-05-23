package com.quickwebframework.entity.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.entity.Log;
import com.quickwebframework.service.LogService;

public class DefaultLogImpl implements Log {

	private BundleContext bundleContext;

	// 默认的日志器
	private Log defaultLog = null;
	// 当前日志器
	private Log currentLog = null;

	public DefaultLogImpl(BundleContext bundleContext, final String name) {
		this.bundleContext = bundleContext;
		defaultLog = new JavaLoggerImpl(name);
		refreshCurrentLog(name);

		bundleContext.addServiceListener(new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent arg0) {
				if (arg0.getServiceReference().toString()
						.contains(LogService.class.getName())) {
					refreshCurrentLog(name);
				}
			}
		});
	}

	private void refreshCurrentLog(String name) {
		Log tmpLog = getServiceLog(name);
		if (tmpLog == null)
			currentLog = defaultLog;
		else
			currentLog = tmpLog;
	}

	// 得到服务中的Log
	private Log getServiceLog(String name) {
		if (bundleContext == null)
			return null;
		ServiceReference serviceReference = bundleContext
				.getServiceReference(LogService.class.getName());
		if (serviceReference == null)
			return null;
		LogService logService = (LogService) bundleContext
				.getService(serviceReference);
		return logService.getLog(name);
	}

	@Override
	public void debug(Object message) {
		currentLog.debug(message);
	}

	@Override
	public void debug(Object message, Throwable exception) {
		currentLog.debug(message, exception);
	}

	@Override
	public void error(Object message) {
		currentLog.error(message);
	}

	@Override
	public void error(Object message, Throwable exception) {
		currentLog.error(message, exception);
	}

	@Override
	public void fatal(Object message) {
		currentLog.fatal(message);
	}

	@Override
	public void fatal(Object message, Throwable exception) {
		currentLog.fatal(message, exception);
	}

	@Override
	public void info(Object message) {
		currentLog.info(message);
	}

	@Override
	public void info(Object message, Throwable exception) {
		currentLog.info(message, exception);
	}

	@Override
	public void trace(Object message) {
		currentLog.trace(message);
	}

	@Override
	public void trace(Object message, Throwable exception) {
		currentLog.trace(message, exception);
	}

	@Override
	public void warn(Object message) {
		currentLog.warn(message);
	}

	@Override
	public void warn(Object message, Throwable exception) {
		currentLog.warn(message, exception);
	}
}
