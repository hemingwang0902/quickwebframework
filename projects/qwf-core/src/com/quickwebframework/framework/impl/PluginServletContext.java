package com.quickwebframework.framework.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;

import com.quickwebframework.core.Activator;
import com.quickwebframework.framework.FrameworkContext;

public class PluginServletContext extends FrameworkContext {
	private static PluginServletContext instance;

	public static PluginServletContext getInstance() {
		if (instance == null)
			instance = new PluginServletContext();
		return instance;
	}

	private static Map<String, Servlet> typeNameServletMap;

	public PluginServletContext() {
		typeNameServletMap = new HashMap<String, Servlet>();
	}

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getContext();
	}

	@Override
	protected void init(int arg) {

	}

	@Override
	protected void destory(int arg) {

	}

	@Override
	protected void bundleChanged(BundleEvent event) {

	}

	@Override
	protected void serviceChanged(ServiceEvent event) {

	}

	/**
	 * 注册视图类型的Servlet
	 * 
	 * @param typeName
	 * @param servlet
	 */
	public static void registerViewTypeServlet(String typeName, Servlet servlet) {
		if (typeNameServletMap.containsKey(typeName)) {
			throw new RuntimeException("视图类型为[%s]的Servlet已经被注册到了上下文中！");
		}
		typeNameServletMap.put(typeName, servlet);
	}

	/**
	 * 取消注册视图类型的Servlet
	 * 
	 * @param typeName
	 */
	public static void unregisterViewTypeServlet(String typeName) {
		typeNameServletMap.remove(typeName);
	}

	/**
	 * 得到指定视图类型的Servlet
	 * 
	 * @param typeName
	 * @return
	 */
	public static Servlet getViewTypeServlet(String typeName) {
		return typeNameServletMap.get(typeName);
	}
}
