package com.quickwebframework.service;

import org.osgi.framework.Bundle;

/**
 * 控制器服务，由其他插件注册
 * 
 * @author aaa
 * 
 */
public interface PluginService {
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
