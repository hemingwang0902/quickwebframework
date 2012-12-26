package com.quickwebframework.entity.impl;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.quickwebframework.entity.Log;

@SuppressWarnings("deprecation")
public class JavaLoggerImpl implements Log {

	/**
	 * java日志器处理级别
	 */
	public static Level javaLoggerLevel = Level.INFO;

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
		LogRecord logRecord = new LogRecord(level, message.toString());
		logRecord.setLoggerName(sourceClassName);
		logRecord.setSourceClassName(sourceClassName);
		logRecord.setSourceMethodName(sourceMethodName);
		return logRecord;
	}

	private void recordLog(LogRecord logRecord) {
		if (!consoleHandler.getLevel().equals(javaLoggerLevel))
			consoleHandler.setLevel(javaLoggerLevel);
		consoleHandler.publish(logRecord);
	}

	@Override
	public void debug(Object message) {
		recordLog(getLogRecord(Level.CONFIG, message));
	}

	@Override
	public void debug(Object message, Throwable exception) {
		recordLog(getLogRecord(Level.CONFIG, message));
	}

	@Override
	public void error(Object message) {
		recordLog(getLogRecord(Level.SEVERE, message));
	}

	@Override
	public void error(Object message, Throwable exception) {
		recordLog(getLogRecord(Level.SEVERE, message));
	}

	@Override
	public void fatal(Object message) {
		recordLog(getLogRecord(Level.SEVERE, message));
	}

	@Override
	public void fatal(Object message, Throwable exception) {
		recordLog(getLogRecord(Level.SEVERE, message));
	}

	@Override
	public void info(Object message) {
		recordLog(getLogRecord(Level.INFO, message));
	}

	@Override
	public void info(Object message, Throwable exception) {
		recordLog(getLogRecord(Level.INFO, message));
	}

	@Override
	public void trace(Object message) {
		recordLog(getLogRecord(Level.FINEST, message));
	}

	@Override
	public void trace(Object message, Throwable exception) {
		recordLog(getLogRecord(Level.FINEST, message));
	}

	@Override
	public void warn(Object message) {
		recordLog(getLogRecord(Level.WARNING, message));
	}

	@Override
	public void warn(Object message, Throwable exception) {
		recordLog(getLogRecord(Level.WARNING, message));
	}
}
