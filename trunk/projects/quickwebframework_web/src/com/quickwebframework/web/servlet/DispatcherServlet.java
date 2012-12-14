package com.quickwebframework.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

	@Override
	public void init() {
	}

	private void handleUrlNotFound(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// 如果QuickWebFramework视图分发Servlet不为空
		PluginViewDispatcherServlet pluginViewDispatcherServlet = PluginViewDispatcherServlet
				.getInstance();
		if (pluginViewDispatcherServlet != null) {
			// 交给QuickWebFramework视图分发Servlet
			pluginViewDispatcherServlet.service(request, response);
		} else {
			// 发送错误码
			response.sendError(404);
		}
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			String requestURI = request.getRequestURI();
			requestURI = requestURI
					.substring(request.getContextPath().length());

			// 保护WEB-INF中的文件
			if (requestURI.startsWith("/WEB-INF")) {
				handleUrlNotFound(request, response);
				return;
			}

			for (QwfServlet tmpServlet : QuickWebFrameworkLoaderListener.qwfServletList) {
				if (tmpServlet.isUrlMatch(requestURI)) {
					tmpServlet.service(request, response);
					return;
				}
			}

			// 如果是WEB项目的资源
			InputStream inputStream = getServletContext().getResourceAsStream(
					requestURI);
			if (inputStream != null) {
				// 输出
				OutputStream outputStream = response.getOutputStream();
				IoUtil.copyStream(inputStream, outputStream);
				inputStream.close();
				return;
			}
			// 处理URL未找到
			handleUrlNotFound(request, response);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
