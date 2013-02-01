package com.quickwebframework.db.orm.quickorm.support;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.db.orm.quickorm.QuickormContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		QuickormContext.getInstance().init();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		QuickormContext.getInstance().destory();
		Activator.context = null;
	}
}
