package org.slf4j;

import java.util.HashMap;
import java.util.Map;

import com.quickwebframework.entity.Log;
import com.quickwebframework.framework.LogContext;

public class LoggerFactory {

	private static Map<String, Logger> logMap;

	public static Logger getLogger(String name) {
		if (logMap == null)
			logMap = new HashMap<String, Logger>();
		if (logMap.containsKey(name))
			return logMap.get(name);

		final String loggerName = name;
		final Log qwfLog = LogContext.getLog(name);

		Logger newLogger = new Logger() {
			@Override
			public String getName() {
				return loggerName;
			}

			private String getFinalString(String format, Object... arguments) {
				if (arguments == null || arguments.length == 0)
					return format;
				String spString = "{}";
				StringBuilder sb = new StringBuilder(format);
				for (int i = 0; i < arguments.length; i++) {
					Object currentObj = arguments[arguments.length - i - 1];
					int currentIndex = format.lastIndexOf(spString);
					sb.delete(currentIndex, currentIndex + spString.length());
					sb.insert(currentIndex, currentObj);
				}
				return sb.toString();
			}

			@Override
			public boolean isTraceEnabled() {
				return true;
			}

			@Override
			public void trace(String msg) {
				qwfLog.trace(msg);
			}

			@Override
			public void trace(String format, Object arg) {
				trace(format, new Object[] { arg });
			}

			@Override
			public void trace(String format, Object arg1, Object arg2) {
				trace(format, new Object[] { arg1, arg2 });
			}

			@Override
			public void trace(String format, Object... arguments) {
				qwfLog.trace(getFinalString(format, arguments));
			}

			@Override
			public void trace(String msg, Throwable t) {
				qwfLog.trace(msg, t);
			}

			@Override
			public boolean isTraceEnabled(Marker marker) {
				return true;
			}

			@Override
			public void trace(Marker marker, String msg) {
				trace(msg);
			}

			@Override
			public void trace(Marker marker, String format, Object arg) {
				trace(format, arg);
			}

			@Override
			public void trace(Marker marker, String format, Object arg1,
					Object arg2) {
				trace(format, arg1, arg2);
			}

			@Override
			public void trace(Marker marker, String format, Object... argArray) {
				trace(format, argArray);
			}

			@Override
			public void trace(Marker marker, String msg, Throwable t) {
				trace(msg, t);
			}

			@Override
			public boolean isDebugEnabled() {
				return true;
			}

			@Override
			public void debug(String msg) {
				qwfLog.debug(msg);
			}

			@Override
			public void debug(String format, Object arg) {
				debug(format, new Object[] { arg });
			}

			@Override
			public void debug(String format, Object arg1, Object arg2) {
				debug(format, new Object[] { arg1, arg2 });
			}

			@Override
			public void debug(String format, Object... arguments) {
				debug(getFinalString(format, arguments));
			}

			@Override
			public void debug(String msg, Throwable t) {
				qwfLog.debug(msg, t);
			}

			@Override
			public boolean isDebugEnabled(Marker marker) {
				return true;
			}

			@Override
			public void debug(Marker marker, String msg) {
				debug(msg);
			}

			@Override
			public void debug(Marker marker, String format, Object arg) {
				debug(format, arg);
			}

			@Override
			public void debug(Marker marker, String format, Object arg1,
					Object arg2) {
				debug(format, arg1, arg2);
			}

			@Override
			public void debug(Marker marker, String format, Object... arguments) {
				debug(format, arguments);
			}

			@Override
			public void debug(Marker marker, String msg, Throwable t) {
				debug(msg, t);
			}

			@Override
			public boolean isInfoEnabled() {
				return true;
			}

			@Override
			public void info(String msg) {
				qwfLog.info(msg);
			}

			@Override
			public void info(String format, Object arg) {
				info(format, new Object[] { arg });
			}

			@Override
			public void info(String format, Object arg1, Object arg2) {
				info(format, new Object[] { arg1, arg2 });
			}

			@Override
			public void info(String format, Object... arguments) {
				qwfLog.info(getFinalString(format, arguments));
			}

			@Override
			public void info(String msg, Throwable t) {
				qwfLog.info(msg, t);
			}

			@Override
			public boolean isInfoEnabled(Marker marker) {
				return true;
			}

			@Override
			public void info(Marker marker, String msg) {
				info(msg);
			}

			@Override
			public void info(Marker marker, String format, Object arg) {
				info(format, arg);
			}

			@Override
			public void info(Marker marker, String format, Object arg1,
					Object arg2) {
				info(format, arg1, arg2);
			}

			@Override
			public void info(Marker marker, String format, Object... arguments) {
				info(format, arguments);
			}

			@Override
			public void info(Marker marker, String msg, Throwable t) {
				qwfLog.info(msg, t);
			}

			@Override
			public boolean isWarnEnabled() {
				return true;
			}

			@Override
			public void warn(String msg) {
				qwfLog.warn(msg);
			}

			@Override
			public void warn(String format, Object arg) {
				warn(format, new Object[] { arg });
			}

			@Override
			public void warn(String format, Object... arguments) {
				qwfLog.warn(getFinalString(format, arguments));
			}

			@Override
			public void warn(String format, Object arg1, Object arg2) {
				warn(format, new Object[] { arg1, arg2 });
			}

			@Override
			public void warn(String msg, Throwable t) {
				qwfLog.warn(msg, t);
			}

			@Override
			public boolean isWarnEnabled(Marker marker) {
				return true;
			}

			@Override
			public void warn(Marker marker, String msg) {
				warn(msg);
			}

			@Override
			public void warn(Marker marker, String format, Object arg) {
				warn(format, arg);
			}

			@Override
			public void warn(Marker marker, String format, Object arg1,
					Object arg2) {
				warn(format, arg1, arg2);
			}

			@Override
			public void warn(Marker marker, String format, Object... arguments) {
				warn(format, arguments);
			}

			@Override
			public void warn(Marker marker, String msg, Throwable t) {
				warn(msg, t);
			}

			@Override
			public boolean isErrorEnabled() {
				return true;
			}

			@Override
			public void error(String msg) {
				qwfLog.error(msg);
			}

			@Override
			public void error(String format, Object arg) {
				error(format, new Object[] { arg });
			}

			@Override
			public void error(String format, Object arg1, Object arg2) {
				error(format, new Object[] { arg1, arg2 });
			}

			@Override
			public void error(String format, Object... arguments) {
				qwfLog.error(getFinalString(format, arguments));
			}

			@Override
			public void error(String msg, Throwable t) {
				qwfLog.error(msg, t);
			}

			@Override
			public boolean isErrorEnabled(Marker marker) {
				return true;
			}

			@Override
			public void error(Marker marker, String msg) {
				error(msg);
			}

			@Override
			public void error(Marker marker, String format, Object arg) {
				error(format, arg);
			}

			@Override
			public void error(Marker marker, String format, Object arg1,
					Object arg2) {
				error(format, arg1, arg2);
			}

			@Override
			public void error(Marker marker, String format, Object... arguments) {
				error(format, arguments);
			}

			@Override
			public void error(Marker marker, String msg, Throwable t) {
				error(msg, t);
			}
		};
		logMap.put(name, newLogger);
		return newLogger;
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}
}
