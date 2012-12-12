package com.quickwebframework.core;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.bridge.HttpServletBridge;
import com.quickwebframework.bridge.LogBridge;
import com.quickwebframework.bridge.ServletFilterBridge;
import com.quickwebframework.bridge.ServletListenerBridge;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.framework.FilterContext;
import com.quickwebframework.framework.FrameworkContext;
import com.quickwebframework.framework.WebContext;

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

		// 初始化FrameworkContext
		FrameworkContext.init();

		// 注册各FrameworkBridge对象为服务
		context.registerService(HttpServletBridge.class.getName(),
				new HttpServletBridge(), null);
		context.registerService(LogBridge.class.getName(), new LogBridge(),
				null);
		context.registerService(ServletFilterBridge.class.getName(),
				new ServletFilterBridge(), null);
		context.registerService(ServletListenerBridge.class.getName(),
				new ServletListenerBridge(), null);

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
		FrameworkContext.destroy();
		Activator.context = null;
		log.info("Stoped [com.quickwebframework.core].");
	}
}
