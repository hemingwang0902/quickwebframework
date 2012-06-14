package com.quickwebframework.service;

import org.osgi.framework.Bundle;

/**
 * WEB应用服务，开发的插件如果有WEB功能，则应注册此服务
 * 
 * @author aaa
 * 
 */
public interface WebAppService {
	/**
	 * 得到Bundle对象
	 * 
	 * @return
	 */
	public Bundle getBundle();

	/**
	 * 得到Bundle内部的ClassLoader
	 * 
	 * @return
	 */
	public ClassLoader getClassLoader();
}
