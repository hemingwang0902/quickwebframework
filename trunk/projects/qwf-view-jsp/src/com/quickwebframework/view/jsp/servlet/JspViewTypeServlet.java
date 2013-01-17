package com.quickwebframework.view.jsp.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;

public class JspViewTypeServlet extends ViewTypeServlet {

	private static final long serialVersionUID = 3719762515648054933L;
	public static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.viewTypeName";

	public JspViewTypeServlet(String viewTypeName) {
		super(viewTypeName);
	}

	@Override
	public String[] getUrls() {
		return null;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String pluginName = request.getAttribute(WebContext.CONST_PLUGIN_NAME)
				.toString();
		String pathName = request.getAttribute(WebContext.CONST_PATH_NAME)
				.toString();
	}

}
