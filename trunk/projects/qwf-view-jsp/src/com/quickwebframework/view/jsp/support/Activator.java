package com.quickwebframework.view.jsp.support;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;
import com.quickwebframework.view.jsp.servlet.JspViewTypeServlet;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private ViewTypeServlet viewTypeServlet;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		String viewTypeName = WebContext
				.getQwfConfig(JspViewTypeServlet.VIEW_TYPE_NAME_PROPERTY_KEY);
		if (viewTypeName == null || viewTypeName.isEmpty()) {
			throw new RuntimeException(String.format(
					"未找到配置[qwf.config.%s]，JSP框架启动失败！",
					JspViewTypeServlet.VIEW_TYPE_NAME_PROPERTY_KEY));
		}
		viewTypeServlet = new JspViewTypeServlet(viewTypeName);
		WebContext.registerViewTypeServlet(viewTypeServlet);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		WebContext.unregisterViewTypeServlet(viewTypeServlet);
		Activator.context = null;
	}

}
