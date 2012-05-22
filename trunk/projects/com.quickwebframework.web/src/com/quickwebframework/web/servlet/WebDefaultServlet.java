package com.quickwebframework.web.servlet;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.web.util.IoUtil;

/**
 * WEB资源Servlet
 * 
 * @author aaa
 * 
 */
public class WebDefaultServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5801185453706962742L;

	// 处理"/"的Servlet
	private HttpServlet rootHttpServlet;

	public WebDefaultServlet() {
	}

	public WebDefaultServlet(HttpServlet rootHttpServlet) {
		this.rootHttpServlet = rootHttpServlet;
	}

	// 初始化Web资源Servlet
	public static HttpServlet initServlet(ServletContext servletContext,
			HttpServlet rootServlet) {
		WebDefaultServlet webDefaultServlet = new WebDefaultServlet(rootServlet);
		// 添加Servlet映射
		ServletRegistration.Dynamic viewDynamic = servletContext.addServlet(
				WebDefaultServlet.class.getName(), webDefaultServlet);
		viewDynamic.addMapping("/");
		return webDefaultServlet;
	}

	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			String requestURI = request.getRequestURI();

			// 如果是"/"，并且处理/的Servlet不为null，则由此Servlet处理
			if (requestURI.equals("/") && rootHttpServlet != null) {
				rootHttpServlet.service(request, response);
				return;
			}
			// 否则处一般资源处理
			if (requestURI.startsWith("/WEB-INF")) {
				response.sendError(403);
				return;
			}

			InputStream inputStream = request.getServletContext()
					.getResourceAsStream(requestURI);
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
