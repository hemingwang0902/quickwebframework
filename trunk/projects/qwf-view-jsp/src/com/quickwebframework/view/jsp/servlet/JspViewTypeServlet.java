package com.quickwebframework.view.jsp.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jasper.Constants;
import org.apache.jasper.EmbeddedServletOptions;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.security.SecurityUtil;
import org.apache.jasper.servlet.JspServletWrapper;
import org.osgi.framework.Bundle;

import com.quickwebframework.framework.OsgiContext;
import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;

public class JspViewTypeServlet extends ViewTypeServlet {
	private Log log = LogFactory.getLog(JspViewTypeServlet.class);

	private static final long serialVersionUID = 3719762515648054933L;
	public static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.viewTypeName";
	public static final String JSP_PATH_PREFIX_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.jspPathPrefix";
	public static final String JSP_PATH_SUFFIX_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.jspPathSuffix";

	// JSP路径前缀
	private String jspPathPrefix;
	// JSP路径后缀
	private String jspPathSuffix;

	private transient ServletContext context;
	private ServletConfig config;
	private transient Options options;
	private transient JspRuntimeContext rctxt;
	private String jspFile;

	public JspViewTypeServlet(String viewTypeName) {
		super(viewTypeName);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		jspPathPrefix = WebContext.getQwfConfig(JSP_PATH_PREFIX_PROPERTY_KEY);
		jspPathSuffix = WebContext.getQwfConfig(JSP_PATH_SUFFIX_PROPERTY_KEY);
		if (jspPathSuffix == null || jspPathSuffix.isEmpty()) {
			jspPathSuffix = ".jsp";
		}

		this.config = config;
		this.context = config.getServletContext();

		rctxt = new JspRuntimeContext(context, options);

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
		if (jspPathPrefix != null) {
			pathName = jspPathPrefix + pathName;
		}
		if (jspPathSuffix != null) {
			pathName = pathName + jspPathSuffix;
		}
		Bundle bundle = OsgiContext.getBundleByName(pluginName);
		URL url = bundle.getResource(pathName);
		if (url == null) {
			response.sendError(404, "在插件[" + pluginName + "]中未找到[" + pathName
					+ "]资源！");
			return;
		}

		String jspUri = jspFile;
		if (jspUri == null) {
			jspUri = (String) request
					.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
			if (jspUri != null) {
				String pathInfo = (String) request
						.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);
				if (pathInfo != null) {
					jspUri += pathInfo;
				}
			} else {
				jspUri = request.getServletPath();
				String pathInfo = request.getPathInfo();
				if (pathInfo != null) {
					jspUri += pathInfo;
				}
			}
		}

		try {
			boolean precompile = preCompile(request);
			serviceJspFile(request, response, jspUri, precompile);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		if (true) {
			return;
		}
		
		
		// ==============
		// 文件方式
		// ==============
		String outputPath = WebContext.getRealPath("WEB-INF/jspoutput");

		String srcJspFolderPath = outputPath + "/src";
		File tmpFile = new File(srcJspFolderPath + "/" + pathName);

		// 创建目录
		tmpFile.getParentFile().mkdirs();
		// 将文件复制到临时文件
		FileUtils.copyURLToFile(url, tmpFile);

		try {
			org.apache.jasper.JspC jspC = new org.apache.jasper.JspC();

			jspC.setJavaEncoding("utf-8");
			jspC.setWebXmlEncoding("utf-8");
			jspC.setUriroot(srcJspFolderPath);
			jspC.setOutputDir(outputPath);
			jspC.setJspFiles(pathName);
			jspC.setCompile(true);
			jspC.execute();
		} catch (Exception ex) {
			log.error("编译JSP文件时出错！", ex);
			response.sendError(500, "编译JSP文件时出错！");
			return;
		} finally {
			// 删除临时文件
			// FileUtils.deleteDirectory(new File(srcJspFolderPath));
			// FileUtils.deleteDirectory(new File(outputPath));
		}
		response.getWriter().write("Found jsp file:" + pathName);
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
					if (null == context.getResource(jspUri)) {
						handleMissingResource(request, response, jspUri);
						return;
					}
					wrapper = new JspServletWrapper(config, options, jspUri,
							rctxt);
					rctxt.addWrapper(jspUri, wrapper);
				}
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
