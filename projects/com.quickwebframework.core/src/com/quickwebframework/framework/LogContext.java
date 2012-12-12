package com.quickwebframework.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.core.Activator;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.entity.impl.DefaultLogImpl;
import com.quickwebframework.entity.impl.JavaLoggerImpl;

public class LogContext {
	private static Log log = LogFactory.getLog(LogContext.class.getName());

	public static void init() {
		BundleContext bundleContext = Activator.getContext();
		// 设置默认的Java日志记录器配置
		try {
			ServiceReference<?>[] serviceReferences = bundleContext
					.getServiceReferences(String.class.getName(),
							"(quickwebframework.config=com.quickwebframework.core.javalogger.level)");
			if (serviceReferences != null && serviceReferences.length > 0) {
				String javaLoggerLevelStr = (String) bundleContext
						.getService(serviceReferences[0]);
				JavaLoggerImpl.javaLoggerLevel = Level
						.parse(javaLoggerLevelStr);
			}
		} catch (Exception ex) {
			log.error("设置默认的Java日志记录器配置时出错。");
		}
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
				log = new DefaultLogImpl(Activator.getContext(), name);
				logMap.put(name, log);
			}
		}
		return log;
	}
}
