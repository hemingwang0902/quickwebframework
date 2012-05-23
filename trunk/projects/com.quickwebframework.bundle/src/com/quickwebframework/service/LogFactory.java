package com.quickwebframework.service;

import org.osgi.framework.BundleContext;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.impl.DefaultLogImpl;
import com.quickwebframework.entity.impl.JavaLoggerImpl;

public class LogFactory {

	public static void setBundleContext(BundleContext bundleContext) {
		currentLog = new DefaultLogImpl(bundleContext);
	}

	// 当前日志器
	private static Log currentLog = new JavaLoggerImpl();

	public static Log getLog() {
		return getLog(LogFactory.class);
	}

	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	public static Log getLog(String clazzName) {
		return currentLog;
	}
}
