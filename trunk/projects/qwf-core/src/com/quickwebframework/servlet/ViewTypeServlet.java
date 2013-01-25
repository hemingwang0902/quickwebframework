package com.quickwebframework.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ViewTypeServlet extends HttpServlet {

	private static final long serialVersionUID = 1764897069126545760L;
	private String viewTypeName;

	/**
	 * 得到视图类型名称
	 * 
	 * @return
	 */
	public String getViewTypeName() {
		return viewTypeName;
	}

	public ViewTypeServlet(String viewTypeName) {
		this.viewTypeName = viewTypeName;
	}

	/**
	 * 得到此视图类型Servlet下面的所有URL
	 * 
	 * @return
	 */
	public abstract String[] getUrls();

	/**
	 * HTTP服务
	 */
	@Override
	public abstract void service(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException;
}
