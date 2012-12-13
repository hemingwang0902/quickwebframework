package com.quickwebframework.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.framework.FrameworkContext;

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

		log = LogFactory.getLog(Activator.class);
		log.info("Starting [com.quickwebframework.core]...");
		FrameworkContext.initAllContext();
		log.info("Started [com.quickwebframework.core].");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		log.info("Stoping [com.quickwebframework.core]...");
		FrameworkContext.destoryAllContext();
		Activator.context = null;
		log.info("Stoped [com.quickwebframework.core].");
	}
}
