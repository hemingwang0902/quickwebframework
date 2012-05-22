package com.quickwebframework.web.servlet;

import java.lang.reflect.Method;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.web.listener.QuickWebFrameworkLoaderListener;

public class PluginViewDispatcherServlet extends javax.servlet.http.HttpServlet {

	private static final long serialVersionUID = -148988758320673145L;

	public static final String MAPPING_PROPERTY_KEY = "quickwebframework.pluginViewServlet.mapping";
	public static final String BUNDLE_NAME_PARAMETER_NAME_PROPERTY_KEY = "quickwebframework.pluginViewServlet.bundleNameParameterName";
	public static final String METHOD_NAME_PARAMETER_NAME_PROPERTY_KEY = "quickwebframework.pluginViewServlet.methodNameParameterName";

	public static final String ARG_BUNDLE_NAME = "com.quickwebframework.util.ARG_BUNDLE_NAME";
	public static final String ARG_METHOD_NAME = "com.quickwebframework.util.ARG_METHOD_NAME";

	private String mapping;
	private String bundleNameParameterName;
	private String methodNameParameterName;

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public void setBundleNameParameterName(String bundleNameParameterName) {
		this.bundleNameParameterName = bundleNameParameterName;
	}

	public void setMethodNameParameterName(String methodNameParameterName) {
		this.methodNameParameterName = methodNameParameterName;
	}
	
	// 初始化插件视图Servlet
	public static HttpServlet initServlet(
			ServletContext servletContext,
			Properties quickWebFrameworkProperties) {
		// 添加插件视图Servlet
		PluginViewDispatcherServlet pluginViewDispatcherServlet = new PluginViewDispatcherServlet();
		String pluginViewDispatcherServletMapping = quickWebFrameworkProperties
				.getProperty(PluginViewDispatcherServlet.MAPPING_PROPERTY_KEY);
		// 设置映射的URL
		pluginViewDispatcherServlet
				.setMapping(pluginViewDispatcherServletMapping);
		// 设置请求参数中的插件名称参数
		pluginViewDispatcherServlet
				.setBundleNameParameterName(quickWebFrameworkProperties
						.getProperty(PluginViewDispatcherServlet.BUNDLE_NAME_PARAMETER_NAME_PROPERTY_KEY));
		// 设置请求参数中的方法名称参数
		pluginViewDispatcherServlet
				.setMethodNameParameterName(quickWebFrameworkProperties
						.getProperty(PluginViewDispatcherServlet.METHOD_NAME_PARAMETER_NAME_PROPERTY_KEY));

		// 添加Servlet映射
		ServletRegistration.Dynamic viewDynamic = servletContext.addServlet(
				PluginViewDispatcherServlet.class.getName(),
				pluginViewDispatcherServlet);
		viewDynamic.addMapping(pluginViewDispatcherServletMapping);
		return pluginViewDispatcherServlet;
	}

	// 处理HTTP方法
	private void processHttpMethod(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Object dispatcherServletObject = QuickWebFrameworkLoaderListener
					.getDispatcherServletObject();
			if (dispatcherServletObject == null) {
				response.sendError(404, "DispatcherServlet object not found!");
				return;
			}
			// dispatcherServlet的类
			Class<?> dispatcherServletClazz = dispatcherServletObject
					.getClass();

			String contextPath = request.getContextPath();
			String requestURI = request.getRequestURI();

			Method httpMethod = null;

			// 先得到对应的Java方法
			try {
				// 如果当前是根URL："/"
				if ("/".equals(requestURI)) {
					httpMethod = dispatcherServletClazz.getMethod(
							"serviceRootUrl", Object.class, Object.class);
				} else {
					// 找到对应的处理方法
					httpMethod = dispatcherServletClazz.getMethod("service",
							Object.class, Object.class, String.class,
							String.class);
				}
			} catch (Exception ex) {
				response.sendError(404, ex.toString());
				return;
			}

			// 准备参数然后调用上面得到的Java方法
			if ("/".equals(requestURI)) {
				// 调用HTTP方法
				httpMethod.invoke(dispatcherServletObject, request, response);
			} else {
				String bundleName = null;
				String methodName = null;

				// 得到bundleName和methodName

				// 如果映射的URL是类似于 /view/*
				if (mapping.endsWith("*")) {
					if (!requestURI.endsWith("/"))
						requestURI = requestURI + "/";
					String otherString = requestURI.substring(contextPath
							.length() + mapping.length() - 1);
					String[] tmpArray = otherString.split("/");
					if (tmpArray.length < 2) {
						response.sendError(404);
						return;
					}
					bundleName = tmpArray[0];
					methodName = tmpArray[1];
				}
				// 否则应该是类似于/view，只需要直接取出参数
				else {
					bundleName = request.getParameter(bundleNameParameterName);
					methodName = request.getParameter(methodNameParameterName);
				}
				// 将插件名称与方法名称设置到请求属性中
				request.setAttribute(ARG_BUNDLE_NAME, bundleName);
				request.setAttribute(ARG_METHOD_NAME, methodName);

				// 调用HTTP方法
				httpMethod.invoke(dispatcherServletObject, request, response,
						bundleName, methodName);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public void service(HttpServletRequest request, HttpServletResponse response) {
		processHttpMethod(request, response);
	}
}
