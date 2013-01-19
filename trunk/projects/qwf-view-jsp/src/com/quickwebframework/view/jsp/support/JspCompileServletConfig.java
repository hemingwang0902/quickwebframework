package com.quickwebframework.view.jsp.support;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class JspCompileServletConfig implements ServletConfig {

	private ServletConfig srcServletConfig;
	private ServletContext srcServletContext;

	public JspCompileServletConfig(ServletConfig srcServletConfig,
			ServletContext srcServletContext) {
		this.srcServletConfig = srcServletConfig;
		this.srcServletContext = srcServletContext;
	}

	@Override
	public String getInitParameter(String arg0) {
		return srcServletConfig.getInitParameter(arg0);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return srcServletConfig.getInitParameterNames();
	}

	@Override
	public ServletContext getServletContext() {
		return srcServletContext;
	}

	@Override
	public String getServletName() {
		return srcServletConfig.getServletName();
	}

}
