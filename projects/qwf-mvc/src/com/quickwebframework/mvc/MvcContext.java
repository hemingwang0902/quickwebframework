package com.quickwebframework.mvc;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;

import com.quickwebframework.framework.FrameworkContext;
import com.quickwebframework.mvc.impl.Activator;

public class MvcContext extends FrameworkContext {

	private static MvcContext instance;

	public static MvcContext getInstance() {
		if (instance == null)
			instance = new MvcContext();
		return instance;
	}

	// MVC框架服务
	private static MvcFrameworkService mvcFrameworkService;

	public static MvcFrameworkService getMvcFrameworkService() {
		return mvcFrameworkService;
	}

	public MvcContext() {
	}

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getContext();
	}

	@Override
	public void init() {
		super.addSimpleServiceStaticFieldLink(
				MvcFrameworkService.class.getName(), "mvcFrameworkService");
	}

	@Override
	protected void bundleChanged(BundleEvent event) {
		Bundle bundle = event.getBundle();
		int bundleEventType = event.getType();
		if (bundleEventType == BundleEvent.STOPPING) {
			// 移除插件的控制器
			if (mvcFrameworkService == null)
				return;
			mvcFrameworkService.removeBundle(bundle);
		}
	}

	@Override
	protected void serviceChanged(ServiceEvent event) {

	}

	@Override
	public void destory() {

	}

	/**
	 * 注册WEB应用
	 * 
	 * @param bundleContext
	 */
	public static void addBundle(Bundle bundle) {
		if (mvcFrameworkService == null) {
			String message = String.format("将插件[%s]添加到MVC框架时，未发现有注册的MVC框架服务！",
					bundle.getSymbolicName());
			throw new RuntimeException(message);
		}
		// 注册服务
		mvcFrameworkService.addBundle(bundle);
	}
}
