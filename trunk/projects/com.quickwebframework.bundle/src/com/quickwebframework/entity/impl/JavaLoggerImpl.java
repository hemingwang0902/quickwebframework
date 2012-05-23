package com.quickwebframework.entity.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.quickwebframework.entity.Log;

public class JavaLoggerImpl implements Log {
	// Java默认全局日志对象
	private Logger logger;

	private String sourceClassName;
	private String sourceMethodName;

	public JavaLoggerImpl(String name) {
		logger = Logger.getLogger(name);
		sourceClassName = name;
		sourceMethodName = "";
	}

	@Override
	public void debug(Object message) {
		logger.logp(Level.FINER, sourceClassName, sourceMethodName,
				message.toString());
	}

	@Override
	public void debug(Object message, Throwable exception) {
		logger.logp(Level.FINER, sourceClassName, sourceMethodName,
				message.toString(), exception);
	}

	@Override
	public void error(Object message) {
		logger.logp(Level.SEVERE, sourceClassName, sourceMethodName,
				message.toString());
	}

	@Override
	public void error(Object message, Throwable exception) {
		logger.logp(Level.SEVERE, sourceClassName, sourceMethodName,
				message.toString(), exception);
	}

	@Override
	public void fatal(Object message) {
		logger.logp(Level.SEVERE, sourceClassName, sourceMethodName,
				message.toString());
	}

	@Override
	public void fatal(Object message, Throwable exception) {
		logger.logp(Level.SEVERE, sourceClassName, sourceMethodName,
				message.toString(), exception);
	}

	@Override
	public void info(Object message) {
		logger.logp(Level.INFO, sourceClassName, sourceMethodName,
				message.toString());
	}

	@Override
	public void info(Object message, Throwable exception) {
		logger.logp(Level.INFO, sourceClassName, sourceMethodName,
				message.toString(), exception);
	}

	@Override
	public void trace(Object message) {
		logger.logp(Level.FINEST, sourceClassName, sourceMethodName,
				message.toString());
	}

	@Override
	public void trace(Object message, Throwable exception) {
		logger.logp(Level.FINEST, sourceClassName, sourceMethodName,
				message.toString(), exception);
	}

	@Override
	public void warn(Object message) {
		logger.logp(Level.WARNING, sourceClassName, sourceMethodName,
				message.toString());
	}

	@Override
	public void warn(Object message, Throwable exception) {
		logger.logp(Level.WARNING, sourceClassName, sourceMethodName,
				message.toString(), exception);
	}
}
