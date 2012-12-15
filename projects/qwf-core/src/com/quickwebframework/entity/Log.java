package com.quickwebframework.entity;

/**
 * 日志对象接口
 * 
 * @author aaa
 * 
 */
public interface Log {
	public void debug(Object message);

	public void debug(Object message, Throwable exception);

	public void error(Object message);

	public void error(Object message, Throwable exception);

	public void fatal(Object message);

	public void fatal(Object message, Throwable exception);

	public void info(Object message);

	public void info(Object message, Throwable exception);

	public void trace(Object message);

	public void trace(Object message, Throwable exception);

	public void warn(Object message);

	public void warn(Object message, Throwable exception);
}
