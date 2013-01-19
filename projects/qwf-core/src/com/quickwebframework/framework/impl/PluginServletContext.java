package com.quickwebframework.framework.impl;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;

import com.quickwebframework.core.Activator;
import com.quickwebframework.framework.FrameworkContext;
import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;

public class PluginServletContext extends FrameworkContext {
	private static PluginServletContext instance;

	public static PluginServletContext getInstance() {
		if (instance == null)
			instance = new PluginServletContext();
		return instance;
	}

	private static Map<String, ViewTypeServlet> viewTypeNameServletMap;

	public PluginServletContext() {
		viewTypeNameServletMap = new HashMap<String, ViewTypeServlet>();
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
	public static void registerViewTypeServlet(final ViewTypeServlet servlet) {
		try {
			final Dictionary<String, String> servletInitParameterDict = new Hashtable<String, String>();

			servlet.init(new ServletConfig() {

				@Override
				public String getInitParameter(String arg0) {
					return servletInitParameterDict.get(arg0);
				}

				@Override
				public Enumeration<String> getInitParameterNames() {
					return servletInitParameterDict.keys();
				}

				@Override
				public ServletContext getServletContext() {
					return WebContext.getServletContext();
				}

				@Override
				public String getServletName() {
					return "viewType_" + servlet.getViewTypeName();
				}
			});
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		String viewTypeName = servlet.getViewTypeName();
		if (StringUtils.isEmpty(viewTypeName)) {
			throw new RuntimeException(String.format(
					"视图类型Servlet[%s]的视图类型名称为null或空字符串！", servlet));
		}
		if (viewTypeNameServletMap.containsKey(viewTypeName)) {
			throw new RuntimeException(String.format(
					"视图类型Servlet[%s]注册时失败，原因：视图类型名称[%s]已经被注册到了上下文中！", servlet,
					servlet.getViewTypeName()));
		}
		viewTypeNameServletMap.put(viewTypeName, servlet);
	}

	/**
	 * 取消注册视图类型的Servlet
	 * 
	 * @param typeName
	 */
	public static void unregisterViewTypeServlet(String typeName) {
		viewTypeNameServletMap.remove(typeName);
	}

	/**
	 * 得到指定的视图类型Servlet
	 * 
	 * @param typeName
	 * @return
	 */
	public static ViewTypeServlet getViewTypeServlet(String typeName) {
		return viewTypeNameServletMap.get(typeName);
	}

	/**
	 * 得到所有的视图类型Servlet
	 * 
	 * @return
	 */
	public static ViewTypeServlet[] getViewTypeServlets() {
		return viewTypeNameServletMap.values().toArray(
				new ViewTypeServlet[viewTypeNameServletMap.size()]);
	}
}
