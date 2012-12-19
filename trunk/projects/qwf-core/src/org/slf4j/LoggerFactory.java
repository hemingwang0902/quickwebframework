package org.slf4j;

import com.quickwebframework.framework.LogContext;

public class LoggerFactory {
	public static Logger getLogger(String name) {
		LogContext.getLog(name);
		return null;
	}

	public static Logger getLogger(Class clazz) {
		return getLogger(clazz.getName());
	}
}
