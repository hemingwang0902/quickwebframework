package com.quickwebframework.framework;

import java.util.Dictionary;
import java.util.EventListener;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.core.Activator;
import com.quickwebframework.entity.HandlerExceptionResolver;
import com.quickwebframework.framework.impl.PluginServletContext;
import com.quickwebframework.framework.impl.ServletFilterContext;
import com.quickwebframework.framework.impl.ServletListenerContext;
import com.quickwebframework.framework.impl.ServletServletContext;
import com.quickwebframework.servlet.DefaultResourceServlet;

public class WebContext extends FrameworkContext {
	private static WebContext instance;

	protected static WebContext getInstance() {
		if (instance == null)
			instance = new WebContext();
		return instance;
	}

	// 用于从HttpServletRequest对象中设置或获取对应的参数
	public static final String CONST_PLUGIN_NAME = "com.quickwebframework.framework.WebContext.CONST_PLUGIN_NAME";
	public static final String CONST_PATH_NAME = "com.quickwebframework.framework.WebContext.CONST_PATH_NAME";

	//
	// WEB项目的ServletContext
	private static ServletContext servletContext;
	// URL未找到处理Servlet
	private static HttpServlet urlNotFoundHandleServlet;
	// 得到处理器异常解决器
	private static HandlerExceptionResolver handlerExceptionResolver;

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static HttpServlet getUrlNotFoundHandleServlet() {
		return urlNotFoundHandleServlet;
	}

	public static void setUrlNotFoundHandleServlet(
			HttpServlet urlNotFoundHandleServlet) {
		WebContext.urlNotFoundHandleServlet = urlNotFoundHandleServlet;
	}

	public static HandlerExceptionResolver getHandlerExceptionResolver() {
		return handlerExceptionResolver;
	}

	public static void setHandlerExceptionResolver(
			HandlerExceptionResolver handlerExceptionResolver) {
		WebContext.handlerExceptionResolver = handlerExceptionResolver;
	}

	/**
	 * 得到实际路径
	 * 
	 * @param path
	 * @return
	 */
	public static String getRealPath(String path) {
		if (servletContext == null)
			return null;
		return servletContext.getRealPath(path);
	}

	/**
	 * 得到quickwebframework.properties文件中的quickwebframework.config开头的配置
	 * 
	 * @param configKey
	 * @return
	 */
	public static String getQwfConfig(String configKey) {
		BundleContext context = Activator.getContext();

		ServiceReference<?>[] serviceReferences;
		try {
			serviceReferences = context.getServiceReferences(
					String.class.getName(),
					String.format("(qwf.config=%s)", configKey));
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
		if (serviceReferences != null && serviceReferences.length > 0) {
			return (String) context.getService(serviceReferences[0]);
		}
		return null;
	}

	// ===== WEB相关变量部分结束

	public WebContext() {

	}

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getContext();
	}

	@Override
	protected void init(int arg) {
		super.addSimpleServiceStaticFieldLink(ServletContext.class.getName(),
				"servletContext");
		ServletServletContext.getInstance().init();
		ServletFilterContext.getInstance().init();
		ServletListenerContext.getInstance().init();
		PluginServletContext.getInstance().init();

		// 如果配置了默认的资源访问Servlet
		if ("true".equals(WebContext
				.getQwfConfig(DefaultResourceServlet.RESOURCE_SERVLET))) {
			DefaultResourceServlet resourceServlet = new DefaultResourceServlet();
			String resourceViewTypeName = resourceServlet.getViewTypeName();
			if (!StringUtils.isEmpty(resourceViewTypeName)) {
				WebContext.registerViewTypeServlet(resourceViewTypeName,
						resourceServlet);
			}
		}
	}

	@Override
	protected void destory(int arg) {
		PluginServletContext.getInstance().destory();
		ServletListenerContext.getInstance().destory();
		ServletFilterContext.getInstance().destory();
		ServletServletContext.getInstance().destory();
	}

	@Override
	protected void bundleChanged(BundleEvent event) {

	}

	@Override
	protected void serviceChanged(ServiceEvent event) {

	}

	/**
	 * 得到所有的过滤器
	 * 
	 * @return
	 */
	public static Filter[] getFilters() {
		return ServletFilterContext.getFilters();
	}

	/**
	 * 设置过滤器配置
	 * 
	 * @param filterConfig
	 */
	public static void setFilterConfig(FilterConfig filterConfig) {
		ServletFilterContext.setFilterConfig(filterConfig);
	}

	/**
	 * 注册过滤器
	 * 
	 * @param bundle
	 * @param filter
	 */
	public static void registerFilter(Bundle bundle, Filter filter) {
		ServletFilterContext.registerFilter(bundle, filter);
	}

	/**
	 * 取消注册过滤器
	 * 
	 * @param bundle
	 * @param filter
	 */
	public static void unregisterFilter(Bundle bundle, Filter filter) {
		ServletFilterContext.unregisterFilter(bundle, filter);
	}

	/**
	 * 取消注册所有的过滤器
	 */
	public static void unregisterAllFilter() {
		ServletFilterContext.unregisterAllFilter();
	}

	/**
	 * 注册监听器
	 * 
	 * @param bundle
	 * @param listener
	 */
	public static void registerListener(Bundle bundle, EventListener listener) {
		ServletListenerContext.registerListener(bundle, listener);
	}

	/**
	 * 取消注册监听器
	 * 
	 * @param bundle
	 * @param listener
	 */
	public static void unregisterListener(Bundle bundle, EventListener listener) {
		ServletListenerContext.unregisterListener(bundle, listener);
	}

	/**
	 * 取消注册所有的监听器
	 */
	public static void unregisterAllListener() {
		ServletListenerContext.unregisterAllListener();
	}

	/**
	 * 得到所有监听器
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T extends EventListener> T[] getListeners(Class<T> clazz) {
		return ServletListenerContext.getListeners(clazz);
	}

	/**
	 * 注册Servlet
	 * 
	 * @param path
	 * @param servlet
	 * @param initparams
	 */
	public static void registerServlet(String path, Servlet servlet,
			Dictionary<String, Object> initparams) {
		ServletServletContext.registerServlet(path, servlet, initparams);
	}

	/**
	 * 取消注册Servlet
	 * 
	 * @param string
	 */
	public static void unregisterServlet(String path) {
		ServletServletContext.unregisterServlet(path);
	}

	/**
	 * 获取所有Servlet所注册的路径数组
	 * 
	 * @return
	 */
	public static String[] getAllServletPaths() {
		return ServletServletContext.getAllServletPaths();
	}

	/**
	 * 根据请求路径得到对应的Servlet
	 * 
	 * @param path
	 * @return
	 */
	public static Servlet getServletByPath(String path) {
		return ServletServletContext.getServletByPath(path);
	}

	/**
	 * 注册视图类型的Servlet
	 * 
	 * @param typeName
	 * @param servlet
	 */
	public static void registerViewTypeServlet(String typeName, Servlet servlet) {
		PluginServletContext.registerViewTypeServlet(typeName, servlet);
	}

	/**
	 * 取消注册视图类型的Servlet
	 * 
	 * @param typeName
	 */
	public static void unregisterViewTypeServlet(String typeName) {
		PluginServletContext.unregisterViewTypeServlet(typeName);
	}

	/**
	 * 得到指定视图类型的Servlet
	 * 
	 * @param typeName
	 * @return
	 */
	public static Servlet getViewTypeServlet(String typeName) {
		return PluginServletContext.getViewTypeServlet(typeName);
	}
}
