package com.quickwebframework.developertool;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private BundleAutoManageThread bundleAutoManageThread;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// 得到web根目录
		String webRootDir = System.getProperty("web.root.dir");
		if (webRootDir == null) {
			throw new RuntimeException(
					"Can't found property[web.root.dir] in system properties!");
		}
		// 初始化管理线程
		bundleAutoManageThread = new BundleAutoManageThread(webRootDir
				+ "/WEB-INF/plugins");
		bundleAutoManageThread.start();
		System.out.println("插件自动管理线程已启动，监听插件目录："
				+ bundleAutoManageThread.bundleFolderPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		bundleAutoManageThread.interrupt();
	}
}
