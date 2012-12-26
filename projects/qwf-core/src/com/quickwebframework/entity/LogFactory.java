package com.quickwebframework.entity;

import com.quickwebframework.framework.LogContext;

@SuppressWarnings("deprecation")
/*
 * @author aaa
 * 
 * @deprecated 请使用Apache
 * Commons的LogFactory类，即：org.apache.commons.logging.LogFactory
 */
public class LogFactory {

	public static Log getLog(Class<?> clazz) {
		return LogContext.getLog(clazz);
	}

	public static Log getLog(String name) {
		return LogContext.getLog(name);
	}
}
