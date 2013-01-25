package com.quickwebframework.view.struts2.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;
import org.osgi.framework.Bundle;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.view.struts2.support.Struts2FilterConfig;
import com.quickwebframework.view.struts2.support.Struts2ServletContext;

public class PluginStruts2DispatchServlet extends HttpServlet {

	private static final long serialVersionUID = 7768803244477900595L;
	private Log log = LogFactory.getLog(PluginStruts2DispatchServlet.class);
	private transient ServletContext context;
	private ServletConfig servletConfig;
	private FilterConfig filterConfig;
	private Struts2ViewTypeServlet struts2ViewTypeServlet;
	private Bundle bundle;
	private StrutsPrepareAndExecuteFilter struts2Filter = null;
	private static Bundle currentBundle;
	private static ServletContext currentServletContext;

	// 得到当前Bundle
	public static Bundle getCurrentBundle() {
		return currentBundle;
	}

	// 得到当前ServletContext
	public static ServletContext getCurrentServletContext() {
		return currentServletContext;
	}

	public PluginStruts2DispatchServlet(
			Struts2ViewTypeServlet struts2ViewTypeServlet, Bundle bundle) {
		this.struts2ViewTypeServlet = struts2ViewTypeServlet;
		this.bundle = bundle;
	}

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		this.servletConfig = config;
		this.context = new Struts2ServletContext(config.getServletContext());
		this.filterConfig = new Struts2FilterConfig(this.servletConfig,
				this.context, bundle);
		synchronized (PluginStruts2DispatchServlet.class) {
			PluginStruts2DispatchServlet.currentBundle = this.bundle;
			PluginStruts2DispatchServlet.currentServletContext = this.context;
			struts2Filter = new StrutsPrepareAndExecuteFilter();
			struts2Filter.init(this.filterConfig);
			PluginStruts2DispatchServlet.currentBundle = null;
			PluginStruts2DispatchServlet.currentServletContext = null;
		}
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String pathName = request.getAttribute(WebContext.CONST_PATH_NAME)
				.toString();
		struts2Filter.doFilter(request, response, null);
	}
}