package com.quickwebframework;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.core.DispatcherServlet;
import com.quickwebframework.core.PluginServletContext;
import com.quickwebframework.entity.Log;
import com.quickwebframework.service.LogService;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public static Log getLog() {
		ServiceReference serviceReference = context
				.getServiceReference(LogService.class.getName());
		if (serviceReference == null)
			return null;
		LogService logService = (LogService) context
				.getService(serviceReference);
		return logService.getLog();
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
		System.out.println("Starting QuickWebFramework...");

		ServiceReference servletContextServiceReference = null;
		ServiceReference[] allServiceReferences = context
				.getAllServiceReferences(null, null);
		for (ServiceReference sr : allServiceReferences) {
			if (sr.toString().contains(ServletContext.class.getName())) {
				servletContextServiceReference = sr;
				break;
			}
		}
		if (servletContextServiceReference == null) {
			throw new RuntimeException(
					"在OSGi服务中未找到javax.servlet.ServletContext服务！");
		}

		Object obj = context.getService(servletContextServiceReference);
		ServletContext servletContext = new PluginServletContext(obj);

		DispatcherServlet dispatcherServlet = new DispatcherServlet(
				servletContext, context);
		// 注册DispatcherServlet对象为Service
		context.registerService(DispatcherServlet.class.getName(),
				dispatcherServlet, null);

		System.out.println("Started QuickWebFramework.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stoping QuickWebFramework...");
		Activator.context = null;
		System.out.println("Stoped QuickWebFramework.");
	}

}
