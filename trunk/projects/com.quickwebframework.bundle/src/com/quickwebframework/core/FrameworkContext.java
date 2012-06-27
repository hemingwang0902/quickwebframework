package com.quickwebframework.core;

import javax.servlet.http.HttpServlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.entity.HandlerExceptionResolver;
import com.quickwebframework.service.WebAppService;

public class FrameworkContext {

	private static HttpServlet rootUrlHandleServlet;
	private static HttpServlet urlNotFoundHandleServlet;
	private static HandlerExceptionResolver handlerExceptionResolver;

	/**
	 * 注册WEB应用
	 * 
	 * @param bundleContext
	 */
	public static void registerWebApp(BundleActivator bundleActivator,
			BundleContext bundleContext) {
		// 注册服务
		final Bundle currentBundle = bundleContext.getBundle();
		final ClassLoader currentClassLoader = bundleActivator.getClass()
				.getClassLoader();

		bundleContext.registerService(WebAppService.class.getName(),
				new WebAppService() {
					@Override
					public Bundle getBundle() {
						return currentBundle;
					}

					@Override
					public ClassLoader getClassLoader() {
						return currentClassLoader;
					}
				}, null);
	}

	/**
	 * 得到根跳转URL
	 * 
	 * @return
	 */
	public static HttpServlet getRootUrlHandleServlet() {
		return rootUrlHandleServlet;
	}

	/**
	 * 得到URL未找到处理Servlet
	 * 
	 * @return
	 */
	public static HttpServlet getUrlNotFoundHandleServlet() {
		return urlNotFoundHandleServlet;
	}

	/**
	 * 得到处理器异常解决器
	 * 
	 * @return
	 */
	public static HandlerExceptionResolver getHandlerExceptionResolver() {
		return handlerExceptionResolver;
	}

	public static void setRootUrlHandleServlet(HttpServlet rootUrlHandleServlet) {
		FrameworkContext.rootUrlHandleServlet = rootUrlHandleServlet;
	}

	public static void setUrlNotFoundHandleServlet(
			HttpServlet urlNotFoundHandleServlet) {
		FrameworkContext.urlNotFoundHandleServlet = urlNotFoundHandleServlet;
	}

	public static void setHandlerExceptionResolver(
			HandlerExceptionResolver handlerExceptionResolver) {
		FrameworkContext.handlerExceptionResolver = handlerExceptionResolver;
	}
}
