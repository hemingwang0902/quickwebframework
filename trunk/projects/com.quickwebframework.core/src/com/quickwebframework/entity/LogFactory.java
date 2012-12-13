package com.quickwebframework.entity;

import com.quickwebframework.framework.LogContext;

public class LogFactory {
	public static Log getLog(Class<?> clazz) {
		return LogContext.getLog(clazz);
	}

	public static Log getLog(String name) {
		return LogContext.getLog(name);
	}
}
