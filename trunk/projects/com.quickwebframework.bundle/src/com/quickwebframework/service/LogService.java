package com.quickwebframework.service;

import com.quickwebframework.entity.Log;

/**
 * 日志服务
 * 
 * @author aaa
 * 
 */
public interface LogService {
	/**
	 * 
	 * 得到日志对象
	 * 
	 * @param name
	 *            日志器名称
	 * @return
	 */
	public Log getLog(String name);

	/**
	 * 得到日志对象
	 * 
	 * @param clazz
	 *            类名
	 * @return
	 */
	public Log getLog(Class clazz);
}
