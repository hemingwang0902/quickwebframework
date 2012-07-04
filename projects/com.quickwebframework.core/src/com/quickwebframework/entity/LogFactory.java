package com.quickwebframework.entity;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;

import com.quickwebframework.entity.impl.DefaultLogImpl;

public class LogFactory {

	private static BundleContext bundleContext;

	public static void setBundleContext(BundleContext bundleContext) {
		LogFactory.bundleContext = bundleContext;
	}

	// 日志器Map
	private static Map<String, Log> logMap = new HashMap<String, Log>();

	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	public static Log getLog(String name) {
		Log log = null;
		synchronized (logMap) {
			if (logMap.containsKey(name)) {
				log = logMap.get(name);
			} else {
				log = new DefaultLogImpl(bundleContext, name);
				logMap.put(name, log);
			}
		}
		return log;
	}
}
