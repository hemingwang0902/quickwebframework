package com.quickwebframework.service.core;

import java.util.List;

import javax.servlet.Filter;

import org.osgi.framework.Bundle;

/**
 * 框架插件提供给其他各插件的服务
 * 
 * @author aaa
 * 
 */
public interface PluginService {
	/**
	 * 添加一个过滤器
	 * 
	 * @param bundle
	 *            过滤器所属插件
	 * @param filter
	 *            过滤器
	 */
	public void addFilter(Bundle bundle, Filter filter);

	/**
	 * 添加一个线程
	 * 
	 * @param bundle
	 *            线程所属插件
	 * @param thread
	 *            线程
	 */
	public void addThread(Bundle bundle, Thread thread);

	/**
	 * 得到过滤器列表
	 * 
	 * @return
	 */
	public List<Filter> getFilterList();
}
