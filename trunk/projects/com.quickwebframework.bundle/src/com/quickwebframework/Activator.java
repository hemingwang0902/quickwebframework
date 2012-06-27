package com.quickwebframework;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.core.DispatcherServlet;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.proxy.PluginServletContext;
import com.quickwebframework.service.core.PluginService;
import com.quickwebframework.service.core.impl.PluginServiceImpl;
import com.quickwebframework.util.BundleUtil;

public class Activator implements BundleActivator {
	public static final String BUNDLE_METHOD_URL_TEMPLATE = "com.quickwebframework.util.BUNDLE_METHOD_URL_TEMPLATE";
	private static Log log;

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	private PluginService pluginService;

	private ServletContext getServletContext() {
		ServiceReference servletContextServiceReference = null;
		try {
			ServiceReference[] allServiceReferences = context
					.getAllServiceReferences(null, null);
			for (ServiceReference sr : allServiceReferences) {
				if (sr.toString().contains(ServletContext.class.getName())) {
					servletContextServiceReference = sr;
					break;
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		if (servletContextServiceReference == null) {
			throw new RuntimeException(
					"在OSGi服务中未找到javax.servlet.ServletContext服务！");
		}
		Object obj = context.getService(servletContextServiceReference);
		return new PluginServletContext(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		LogFactory.setBundleContext(context);
		log = LogFactory.getLog(Activator.class);
		log.info("Starting [com.quickwebframework.bundle]...");

		// 设置插件方法URL模板
		ServletContext servletContext = getServletContext();
		Object tmpObj = servletContext.getAttribute(BUNDLE_METHOD_URL_TEMPLATE);
		if (tmpObj != null) {
			BundleUtil.bundleMethodUrlTemplate = tmpObj.toString();
		}

		// 注册PluginService
		pluginService = new PluginServiceImpl(context);
		context.registerService(PluginService.class.getName(), pluginService,
				null);
		// 注册DispatcherServlet对象为Service
		DispatcherServlet dispatcherServlet = new DispatcherServlet(context,
				pluginService);
		context.registerService(DispatcherServlet.class.getName(),
				dispatcherServlet, null);

		log.info("Started [com.quickwebframework.bundle].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		log.info("Stoping [com.quickwebframework.bundle]...");
		pluginService.whenBundleStoped(context.getBundle());
		Activator.context = null;
		log.info("Stoped [com.quickwebframework.bundle].");
	}

}
