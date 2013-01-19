package com.quickwebframework.view.jsp.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.compiler.TldLocationsCache;
import org.apache.tomcat.InstanceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.quickwebframework.framework.OsgiContext;
import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;
import com.quickwebframework.view.jsp.support.Activator;

public class JspViewTypeServlet extends ViewTypeServlet {
	private static final long serialVersionUID = 3719762515648054933L;
	public static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.viewTypeName";
	public static final String JSP_PATH_PREFIX_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.jspPathPrefix";
	public static final String JSP_PATH_SUFFIX_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.jspPathSuffix";

	private Map<String, PluginJspDispatchServlet> pluginNameServletMap = new HashMap<String, PluginJspDispatchServlet>();

	// JSP路径前缀
	private String jspPathPrefix;
	// JSP路径后缀
	private String jspPathSuffix;
	private ServletConfig config;

	private BundleListener bundleListener;

	public String getJspPathPrefix() {
		return jspPathPrefix;
	}

	public String getJspPathSuffix() {
		return jspPathSuffix;
	}

	public JspViewTypeServlet(String viewTypeName) {
		super(viewTypeName);
		bundleListener = new BundleListener() {
			@Override
			public void bundleChanged(BundleEvent event) {
				Bundle bundle = event.getBundle();
				String pluginName = bundle.getSymbolicName();
				if (BundleEvent.STARTED == event.getType()) {
					PluginJspDispatchServlet pluginJspDispatchServlet = createNewPluginJspDispatchServlet(bundle);
					pluginNameServletMap.put(pluginName,
							pluginJspDispatchServlet);
				} else if (BundleEvent.STOPPING == event.getType()) {
					pluginNameServletMap.remove(pluginName);
				}
			}
		};
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.config = config;

		jspPathPrefix = WebContext.getQwfConfig(JSP_PATH_PREFIX_PROPERTY_KEY);
		jspPathSuffix = WebContext.getQwfConfig(JSP_PATH_SUFFIX_PROPERTY_KEY);
		if (jspPathSuffix == null || jspPathSuffix.isEmpty()) {
			jspPathSuffix = ".jsp";
		}
		Activator.getContext().addBundleListener(bundleListener);
	}

	@Override
	public void destroy() {
		super.destroy();
		Activator.getContext().removeBundleListener(bundleListener);
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
		Bundle bundle = OsgiContext.getBundleByName(pluginName);
		PluginJspDispatchServlet pluginJspDispatchServlet = pluginNameServletMap
				.get(pluginName);
		if (pluginJspDispatchServlet == null) {
			pluginJspDispatchServlet = createNewPluginJspDispatchServlet(bundle);
			pluginNameServletMap.put(pluginName, pluginJspDispatchServlet);
		}
		pluginJspDispatchServlet.service(request, response);
	}

	private PluginJspDispatchServlet createNewPluginJspDispatchServlet(
			Bundle bundle) {
		PluginJspDispatchServlet servlet = new PluginJspDispatchServlet(this,
				bundle);
		try {
			servlet.init(config);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
		return servlet;
	}
}
