package com.quickwebframework.log.log4j;

import org.apache.log4j.Logger;

import com.quickwebframework.entity.Log;

public class LogImpl implements Log {

	private Logger logger;

	public LogImpl() {
		logger = Logger.getRootLogger();
	}

	@Override
	public void debug(Object message) {
		logger.debug(message);
	}

	@Override
	public void debug(Object message, Throwable exception) {
		logger.debug(message, exception);
	}

	@Override
	public void error(Object message) {
		logger.error(message);
	}

	@Override
	public void error(Object message, Throwable exception) {
		logger.error(message, exception);
	}

	@Override
	public void fatal(Object message) {
		logger.fatal(message);
	}

	@Override
	public void fatal(Object message, Throwable exception) {
		logger.fatal(message, exception);
	}

	@Override
	public void info(Object message) {
		logger.info(message);
	}

	@Override
	public void info(Object message, Throwable exception) {
		logger.info(message, exception);
	}

	@Override
	public void trace(Object message) {
		logger.trace(message);
	}

	@Override
	public void trace(Object message, Throwable exception) {
		logger.trace(message, exception);
	}

	@Override
	public void warn(Object message) {
		logger.warn(message);
	}

	@Override
	public void warn(Object message, Throwable exception) {
		logger.warn(message, exception);
	}
}
