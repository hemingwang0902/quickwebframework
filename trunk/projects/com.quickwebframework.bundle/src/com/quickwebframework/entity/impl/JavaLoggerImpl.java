package com.quickwebframework.entity.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.quickwebframework.entity.Log;

public class JavaLoggerImpl implements Log {
	// Java默认全局日志对象
	private Logger logger;

	public JavaLoggerImpl(String name) {
		logger = Logger.getLogger(name);
	}

	@Override
	public void debug(Object message) {
		logger.log(Level.FINER, message.toString());
	}

	@Override
	public void debug(Object message, Throwable exception) {
		logger.log(Level.FINER, message.toString(), exception);
	}

	@Override
	public void error(Object message) {
		logger.log(Level.SEVERE, message.toString());
	}

	@Override
	public void error(Object message, Throwable exception) {
		logger.log(Level.SEVERE, message.toString(), exception);
	}

	@Override
	public void fatal(Object message) {
		logger.log(Level.SEVERE, message.toString());
	}

	@Override
	public void fatal(Object message, Throwable exception) {
		logger.log(Level.SEVERE, message.toString(), exception);
	}

	@Override
	public void info(Object message) {
		logger.log(Level.INFO, message.toString());
	}

	@Override
	public void info(Object message, Throwable exception) {
		logger.log(Level.INFO, message.toString(), exception);
	}

	@Override
	public void trace(Object message) {
		logger.log(Level.FINEST, message.toString());
	}

	@Override
	public void trace(Object message, Throwable exception) {
		logger.log(Level.FINEST, message.toString(), exception);
	}

	@Override
	public void warn(Object message) {
		logger.log(Level.WARNING, message.toString());
	}

	@Override
	public void warn(Object message, Throwable exception) {
		logger.log(Level.WARNING, message.toString(), exception);
	}
}
