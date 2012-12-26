package com.quickwebframework.service;

import com.quickwebframework.entity.Log;

@SuppressWarnings("deprecation")
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
}
