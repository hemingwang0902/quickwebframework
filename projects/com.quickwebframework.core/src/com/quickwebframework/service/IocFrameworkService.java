package com.quickwebframework.service;

import org.osgi.framework.Bundle;

/**
 * IoC框架服务接口
 * 
 * @author aaa
 * 
 */
public interface IocFrameworkService {

	/**
	 * 添加一个Bundle到IoC框架中
	 * 
	 * @param bundle
	 */
	public void addBundle(Bundle bundle);

	/**
	 * 移除一个Bundle到IoC框架中
	 * 
	 * @param bundle
	 */
	public void removeBundle(Bundle bundle);

	/**
	 * 从IoC框架中得到此Bundle对应的应用程序上下文
	 * 
	 * @param bundle
	 * @return
	 */
	public Object getBundleApplicationContext(Bundle bundle);
}
