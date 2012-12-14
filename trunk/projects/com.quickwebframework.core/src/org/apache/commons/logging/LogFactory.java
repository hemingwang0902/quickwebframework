package org.apache.commons.logging;

import com.quickwebframework.framework.LogContext;

public abstract class LogFactory {
	public static Log getLog(Class<?> clazz) {
		return (Log) LogContext.getLog(clazz);
	}

	public static Log getLog(String name) {
		return (Log) LogContext.getLog(name);
	}
}
