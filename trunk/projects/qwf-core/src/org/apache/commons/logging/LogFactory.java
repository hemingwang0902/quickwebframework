package org.apache.commons.logging;

import java.util.HashMap;
import java.util.Map;

import com.quickwebframework.framework.LogContext;
import com.quickwebframework.log.impl.commonslogging.LogImpl;

public abstract class LogFactory {
	private static Map<String, Log> logMap;

	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	public static Log getLog(String name) {
		if (logMap == null)
			logMap = new HashMap<String, Log>();
		if (logMap.containsKey(name))
			return logMap.get(name);
		Log log = new LogImpl(LogContext.getLog(name));
		logMap.put(name, log);
		return log;
	}
}
