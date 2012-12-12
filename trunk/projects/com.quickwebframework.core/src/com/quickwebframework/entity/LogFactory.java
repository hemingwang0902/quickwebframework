package com.quickwebframework.entity;

import com.quickwebframework.framework.LogContext;

public class LogFactory {
	public static Log getLog(Class<?> clazz) {
		return LogContext.getInstance().getLog(clazz);
	}

	public static Log getLog(String name) {
		return LogContext.getInstance().getLog(name);
	}
}
