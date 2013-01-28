package com.quickwebframework.viewrender.jsp.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jasper.Constants;
import org.apache.jasper.EmbeddedServletOptions;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.security.SecurityUtil;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.tomcat.InstanceManager;
import org.osgi.framework.Bundle;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.viewrender.jsp.support.DefaultInstanceManager;
import com.quickwebframework.viewrender.jsp.support.JspCompileServletConfig;
import com.quickwebframework.viewrender.jsp.support.JspCompileServletContext;

public class PluginJspDispatchServlet extends HttpServlet {
	private static final long serialVersionUID = -8223091470395652222L;

	private Log log = LogFactory.getLog(PluginJspDispatchServlet.class);

	private transient ServletContext context;
	private ServletConfig config;
	private transient Options options;
	private transient JspRuntimeContext rctxt;
	private Bundle bundle;

	public PluginJspDispatchServlet(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		this.context = new JspCompileServletContext(config.getServletContext());
		this.config = new JspCompileServletConfig(config, this.context);

		options = new EmbeddedServletOptions(this.config, this.context);
		rctxt = new JspRuntimeContext(this.context, options);
		this.context.setAttribute(InstanceManager.class.getName(),
				new DefaultInstanceManager(bundle));
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String pathName = request.getAttribute(WebContext.CONST_PATH_NAME)
				.toString();
		URL url = bundle.getResource(pathName);
		if (url == null) {
			response.sendError(404, "在插件[" + bundle.getSymbolicName()
					+ "]中未找到[" + pathName + "]资源！");
			return;
		}

		String jspUri = "/" + url.toString();
		try {
			boolean precompile = preCompile(request);
			serviceJspFile(request, response, jspUri, precompile);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	boolean preCompile(HttpServletRequest request) throws ServletException {
		String queryString = request.getQueryString();
		if (queryString == null) {
			return (false);
		}
		int start = queryString.indexOf(Constants.PRECOMPILE);
		if (start < 0) {
			return (false);
		}
		queryString = queryString.substring(start
				+ Constants.PRECOMPILE.length());
		if (queryString.length() == 0) {
			return (true);
		}
		if (queryString.startsWith("&")) {
			return (true);
		}
		if (!queryString.startsWith("=")) {
			return (false);
		}
		int limit = queryString.length();
		int ampersand = queryString.indexOf("&");
		if (ampersand > 0) {
			limit = ampersand;
		}
		String value = queryString.substring(1, limit);
		if (value.equals("true")) {
			return (true);
		} else if (value.equals("false")) {
			return (true);
		} else {
			throw new ServletException("Cannot have request parameter "
					+ Constants.PRECOMPILE + " set to " + value);
		}

	}

	private void serviceJspFile(HttpServletRequest request,
			HttpServletResponse response, String jspUri, boolean precompile)
			throws ServletException, IOException {
		JspServletWrapper wrapper = rctxt.getWrapper(jspUri);
		if (wrapper == null) {
			synchronized (this) {
				wrapper = rctxt.getWrapper(jspUri);
				if (wrapper == null) {
					wrapper = new JspServletWrapper(config, options, jspUri,
							rctxt);
					rctxt.addWrapper(jspUri, wrapper);
				}
				wrapper.getJspEngineContext().setClassLoader(
						this.getClass().getClassLoader());
			}
		}

		try {
			wrapper.service(request, response, precompile);
		} catch (FileNotFoundException fnfe) {
			handleMissingResource(request, response, jspUri);
		}

	}

	private void handleMissingResource(HttpServletRequest request,
			HttpServletResponse response, String jspUri)
			throws ServletException, IOException {

		String includeRequestUri = (String) request
				.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI);

		if (includeRequestUri != null) {
			String msg = Localizer.getMessage("jsp.error.file.not.found",
					jspUri);
			throw new ServletException(SecurityUtil.filter(msg));
		} else {
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND,
						request.getRequestURI());
			} catch (IllegalStateException ise) {
				log.error(Localizer.getMessage("jsp.error.file.not.found",
						jspUri));
			}
		}
		return;
	}
}
