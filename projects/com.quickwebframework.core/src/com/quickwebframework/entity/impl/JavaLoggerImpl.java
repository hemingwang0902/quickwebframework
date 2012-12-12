package com.quickwebframework.entity.impl;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.quickwebframework.entity.Log;

public class JavaLoggerImpl implements Log {

	// Java默认控制台日志输出处理程序
	private ConsoleHandler consoleHandler;

	private String sourceClassName;
	private String sourceMethodName;

	public JavaLoggerImpl(String name) {
		consoleHandler = new ConsoleHandler();
		sourceClassName = name;
		sourceMethodName = "";
	}

	private LogRecord getLogRecord(Level level, Object message) {
		LogRecord logRecord = new LogRecord(Level.FINER, message.toString());
		logRecord.setLoggerName(sourceClassName);
		logRecord.setSourceClassName(sourceClassName);
		logRecord.setSourceMethodName(sourceMethodName);
		return logRecord;
	}

	@Override
	public void debug(Object message) {
		consoleHandler.publish(getLogRecord(Level.FINER, message));
	}

	@Override
	public void debug(Object message, Throwable exception) {
		consoleHandler.publish(getLogRecord(Level.FINER, message));
	}

	@Override
	public void error(Object message) {
		consoleHandler.publish(getLogRecord(Level.SEVERE, message));
	}

	@Override
	public void error(Object message, Throwable exception) {
		consoleHandler.publish(getLogRecord(Level.SEVERE, message));
	}

	@Override
	public void fatal(Object message) {
		consoleHandler.publish(getLogRecord(Level.SEVERE, message));
	}

	@Override
	public void fatal(Object message, Throwable exception) {
		consoleHandler.publish(getLogRecord(Level.SEVERE, message));
	}

	@Override
	public void info(Object message) {
		consoleHandler.publish(getLogRecord(Level.INFO, message));
	}

	@Override
	public void info(Object message, Throwable exception) {
		consoleHandler.publish(getLogRecord(Level.INFO, message));
	}

	@Override
	public void trace(Object message) {
		consoleHandler.publish(getLogRecord(Level.FINEST, message));
	}

	@Override
	public void trace(Object message, Throwable exception) {
		consoleHandler.publish(getLogRecord(Level.FINEST, message));
	}

	@Override
	public void warn(Object message) {
		consoleHandler.publish(getLogRecord(Level.WARNING, message));
	}

	@Override
	public void warn(Object message, Throwable exception) {
		consoleHandler.publish(getLogRecord(Level.WARNING, message));
	}
}
