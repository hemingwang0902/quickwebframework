package org.slf4j;

import java.util.HashMap;
import java.util.Map;

import com.quickwebframework.framework.LogContext;
import com.quickwebframework.log.impl.slf4j.LoggerImpl;

public class LoggerFactory {

	private static Map<String, Logger> logMap;

	public static Logger getLogger(String name) {
		if (logMap == null)
			logMap = new HashMap<String, Logger>();
		if (logMap.containsKey(name))
			return logMap.get(name);
		Logger newLogger = new LoggerImpl(name, LogContext.getLog(name));
		logMap.put(name, newLogger);
		return newLogger;
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}
}
