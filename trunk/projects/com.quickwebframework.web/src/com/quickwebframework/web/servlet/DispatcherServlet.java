package com.quickwebframework.web.servlet;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.web.listener.QuickWebFrameworkLoaderListener;
import com.quickwebframework.web.util.IoUtil;

/**
 * WEB资源Servlet
 * 
 * @author aaa
 * 
 */
public class DispatcherServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5801185453706962742L;

	private HttpServlet pluginViewDispatcherServlet;
	private HttpServlet pluginResourceDispatcherServlet;
	private HttpServlet pluginManageServlet;

	@Override
	public void init() {
		ServletContext servletContext = getServletContext();
		Properties quickWebFrameworkProperties = QuickWebFrameworkLoaderListener
				.getQuickWebFrameworkProperties();

		// 初始化插件视图Servlet
		pluginViewDispatcherServlet = PluginViewDispatcherServlet.initServlet(
				servletContext, quickWebFrameworkProperties);
		// 初始化插件资源Servlet
		pluginResourceDispatcherServlet = PluginResourceDispatcherServlet
				.initServlet(servletContext, quickWebFrameworkProperties);
		// 初始化插件管理Servlet
		pluginManageServlet = PluginManageServlet.initServlet(servletContext,
				quickWebFrameworkProperties);
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			String requestURI = request.getRequestURI();

			// 保护WEB-INF中的文件
			if (requestURI.startsWith("/WEB-INF")) {
				response.sendError(403);
				return;
			}

			// 如果是视图
			if (requestURI.equals("/") || requestURI.startsWith("/view/")) {
				pluginViewDispatcherServlet.service(request, response);
				return;
			}

			// 如果是资源
			if (requestURI.startsWith("/resource/")) {
				pluginResourceDispatcherServlet.service(request, response);
				return;
			}

			// 如果是插件管理页面
			if (requestURI.equals("/qwf/index")) {
				pluginManageServlet.service(request, response);
				return;
			}

			InputStream inputStream = getServletContext().getResourceAsStream(
					requestURI);
			if (inputStream == null) {
				response.sendError(404);
				return;
			}
			// 输出
			OutputStream outputStream = response.getOutputStream();
			IoUtil.copyStream(inputStream, outputStream);
			inputStream.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
