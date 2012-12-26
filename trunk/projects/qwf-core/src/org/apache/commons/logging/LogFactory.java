package org.apache.commons.logging;

import java.util.HashMap;
import java.util.Map;

import com.quickwebframework.framework.LogContext;

@SuppressWarnings("deprecation")
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
		final com.quickwebframework.entity.Log qwfLog = LogContext.getLog(name);
		Log log = new Log() {
			@Override
			public boolean isDebugEnabled() {
				return true;
			}

			@Override
			public boolean isErrorEnabled() {
				return true;
			}

			@Override
			public boolean isFatalEnabled() {
				return true;
			}

			@Override
			public boolean isInfoEnabled() {
				return true;
			}

			@Override
			public boolean isTraceEnabled() {
				return true;
			}

			@Override
			public boolean isWarnEnabled() {
				return true;
			}

			@Override
			public void debug(Object message) {
				qwfLog.debug(message);
			}

			@Override
			public void debug(Object message, Throwable exception) {
				qwfLog.debug(message, exception);
			}

			@Override
			public void error(Object message) {
				qwfLog.error(message);
			}

			@Override
			public void error(Object message, Throwable exception) {
				qwfLog.error(message, exception);
			}

			@Override
			public void fatal(Object message) {
				qwfLog.fatal(message);
			}

			@Override
			public void fatal(Object message, Throwable exception) {
				qwfLog.fatal(message, exception);
			}

			@Override
			public void info(Object message) {
				qwfLog.info(message);
			}

			@Override
			public void info(Object message, Throwable exception) {
				qwfLog.info(message, exception);
			}

			@Override
			public void trace(Object message) {
				qwfLog.trace(message);
			}

			@Override
			public void trace(Object message, Throwable exception) {
				qwfLog.trace(message, exception);
			}

			@Override
			public void warn(Object message) {
				qwfLog.warn(message);
			}

			@Override
			public void warn(Object message, Throwable exception) {
				qwfLog.warn(message, exception);
			}
		};
		logMap.put(name, log);
		return log;
	}
}
