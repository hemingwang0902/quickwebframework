package com.quickwebframework.core;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.bridge.FrameworkBridge;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.framework.FrameworkContext;
import com.quickwebframework.framework.WebContext;

public class Activator implements BundleActivator {
	public static final String BUNDLE_METHOD_URL_TEMPLATE = "com.quickwebframework.util.BUNDLE_METHOD_URL_TEMPLATE";
	private static Log log;

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	private ServletContext getServletContext() {
		ServiceReference<?> servletContextServiceReference = null;
		try {
			ServiceReference<?>[] allServiceReferences = context
					.getAllServiceReferences(null, null);
			for (ServiceReference<?> sr : allServiceReferences) {
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
		return (ServletContext) obj;
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
		log.info("Starting [com.quickwebframework.core]...");

		// 设置插件方法URL模板
		ServletContext servletContext = getServletContext();
		Object tmpObj = servletContext.getAttribute(BUNDLE_METHOD_URL_TEMPLATE);
		if (tmpObj != null) {
			WebContext.bundleMethodUrlTemplate = tmpObj.toString();
		}

		// 初始化FrameworkContext
		FrameworkContext.init(context.getBundle());

		// 注册FrameworkBridge对象为服务
		FrameworkBridge frameworkBridge = new FrameworkBridge();
		context.registerService(FrameworkBridge.class.getName(),
				frameworkBridge, null);

		log.info("Started [com.quickwebframework.bundle].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		log.info("Stoping [com.quickwebframework.core]...");
		Activator.context = null;
		log.info("Stoped [com.quickwebframework.core].");
	}

}
