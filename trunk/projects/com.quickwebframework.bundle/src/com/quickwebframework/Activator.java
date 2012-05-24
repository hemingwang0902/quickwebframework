package com.quickwebframework;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.core.DispatcherServlet;
import com.quickwebframework.entity.Log;
import com.quickwebframework.service.LogFactory;

public class Activator implements BundleActivator {

	private static Log log;

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
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
		DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
		// 注册DispatcherServlet对象为Service
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
		Activator.context = null;
		log.info("Stoped [com.quickwebframework.bundle].");
	}

}
