package com.quickwebframework.log.impl.commonslogging;

import org.apache.commons.logging.Log;

public class LogImpl implements Log {

	private com.quickwebframework.entity.Log qwfLog;

	public LogImpl(com.quickwebframework.entity.Log qwfLog) {
		this.qwfLog = qwfLog;
	}

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
		// TODO Auto-generated method stub

	}

	@Override
	public void warn(Object message, Throwable exception) {
		// TODO Auto-generated method stub

	}

}
