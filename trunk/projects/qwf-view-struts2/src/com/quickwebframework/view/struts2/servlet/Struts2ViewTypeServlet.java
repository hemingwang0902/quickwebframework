package com.quickwebframework.view.struts2.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.quickwebframework.framework.OsgiContext;
import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;
import com.quickwebframework.view.struts2.support.Activator;

public class Struts2ViewTypeServlet extends ViewTypeServlet {

	private static final long serialVersionUID = 830203994209104651L;
	public static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-view-struts2.viewTypeName";

	private Map<String, PluginStruts2DispatchServlet> pluginNameServletMap = new HashMap<String, PluginStruts2DispatchServlet>();
	private BundleListener bundleListener;
	private ServletConfig config;

	public Struts2ViewTypeServlet(String viewTypeName) {
		super(viewTypeName);

		bundleListener = new BundleListener() {
			@Override
			public void bundleChanged(BundleEvent event) {
				Bundle bundle = event.getBundle();
				String pluginName = bundle.getSymbolicName();
				if (BundleEvent.STOPPING == event.getType()) {
					if (pluginNameServletMap.containsKey(pluginName)) {
						pluginNameServletMap.remove(pluginName);
					}
				}
			}
		};
	}

	private PluginStruts2DispatchServlet createNewPluginStruts2DispatchServlet(
			Bundle bundle) {
		PluginStruts2DispatchServlet servlet = new PluginStruts2DispatchServlet(
				this, bundle);
		try {
			servlet.init(config);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
		return servlet;
	}

	@Override
	public String[] getUrls() {
		return null;
	}

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		this.config = config;
		Activator.getContext().addBundleListener(bundleListener);
	}

	@Override
	public void destroy() {
		super.destroy();
		Activator.getContext().removeBundleListener(bundleListener);
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String pluginName = request.getAttribute(WebContext.CONST_PLUGIN_NAME)
				.toString();
		PluginStruts2DispatchServlet pluginStruts2DispatchServlet = pluginNameServletMap
				.get(pluginName);
		if (pluginStruts2DispatchServlet == null) {
			Bundle bundle = OsgiContext.getBundleByName(pluginName);
			pluginStruts2DispatchServlet = createNewPluginStruts2DispatchServlet(bundle);
			pluginNameServletMap.put(pluginName, pluginStruts2DispatchServlet);
		}
		pluginStruts2DispatchServlet.service(request, response);
	}
}
