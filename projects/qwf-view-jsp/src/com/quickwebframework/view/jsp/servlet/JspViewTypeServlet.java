package com.quickwebframework.view.jsp.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;
import com.quickwebframework.view.jsp.support.Activator;
import com.quickwebframework.viewrender.ViewRenderContext;
import com.quickwebframework.viewrender.ViewRenderService;

public class JspViewTypeServlet extends ViewTypeServlet {
	private static final long serialVersionUID = 3719762515648054933L;
	public static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.viewTypeName";
	public static final String JSP_PATH_PREFIX_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.jspPathPrefix";
	public static final String JSP_PATH_SUFFIX_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.jspPathSuffix";

	// JSP路径前缀
	private String jspPathPrefix;
	// JSP路径后缀
	private String jspPathSuffix;

	public String getJspPathPrefix() {
		return jspPathPrefix;
	}

	public String getJspPathSuffix() {
		return jspPathSuffix;
	}

	public JspViewTypeServlet(String viewTypeName) {
		super(viewTypeName);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		jspPathPrefix = WebContext.getQwfConfig(JSP_PATH_PREFIX_PROPERTY_KEY);
		jspPathSuffix = WebContext.getQwfConfig(JSP_PATH_SUFFIX_PROPERTY_KEY);
		if (jspPathSuffix == null || jspPathSuffix.isEmpty()) {
			jspPathSuffix = ".jsp";
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public String[] getUrls() {
		List<String> rtnUrlList = new ArrayList<String>();
		for (Bundle bundle : Activator.getContext().getBundles()) {
			// OSGi框架插件不扫描
			if (bundle.getBundleId() == 0) {
				continue;
			}
			String bundleName = bundle.getSymbolicName();
			try {
				Enumeration<URL> resources = bundle.findEntries(
						this.jspPathPrefix, "*" + this.jspPathSuffix, true);
				if (resources == null) {
					continue;
				}
				while (resources.hasMoreElements()) {
					String entryPath = resources.nextElement().getPath();
					String methodName = entryPath.substring(this.jspPathPrefix
							.length());
					methodName = methodName.substring(0, methodName.length()
							- this.jspPathSuffix.length());
					String url = "/" + bundleName + "/"
							+ this.getViewTypeName() + "/" + methodName;
					while (url.contains("//")) {
						url = url.replace("//", "/");
					}
					rtnUrlList.add(url);
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return rtnUrlList.toArray(new String[rtnUrlList.size()]);
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String pluginName = request.getAttribute(WebContext.CONST_PLUGIN_NAME)
				.toString();
		String pathName = request.getAttribute(WebContext.CONST_PATH_NAME)
				.toString();
		if (this.getJspPathPrefix() != null) {
			pathName = this.getJspPathPrefix() + pathName;
		}
		if (this.getJspPathSuffix() != null) {
			pathName = pathName + this.getJspPathSuffix();
		}

		ViewRenderService viewRenderService = ViewRenderContext
				.getViewRenderService();
		// 得到视图名称：例 qwf.test.core:/jsp/test.jsp
		String viewName = pluginName
				+ viewRenderService.getPluginNameAndPathSplitString()
				+ pathName;
		viewRenderService.renderView(request, response, viewName, null);
	}
}
