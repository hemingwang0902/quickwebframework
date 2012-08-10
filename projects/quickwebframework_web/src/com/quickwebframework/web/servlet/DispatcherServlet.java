package com.quickwebframework.web.servlet;

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

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			String requestURI = request.getRequestURI();
			requestURI = requestURI
					.substring(request.getContextPath().length());

			// 保护WEB-INF中的文件
			if (requestURI.startsWith("/WEB-INF")) {
				response.sendError(403);
				return;
			}

			for (QwfServlet tmpServlet : QuickWebFrameworkLoaderListener.qwfServletList) {
				if (tmpServlet.isUrlMatch(requestURI)) {
					tmpServlet.service(request, response);
					return;
				}
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
