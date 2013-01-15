package com.quickwebframework.mvc;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.SynchronousBundleListener;

import com.quickwebframework.framework.FrameworkContext;
import com.quickwebframework.mvc.impl.Activator;

public class MvcContext extends FrameworkContext {

	private static MvcContext instance;

	public static MvcContext getInstance() {
		if (instance == null)
			instance = new MvcContext();
		return instance;
	}

	private static BundleListener bundleListener;
	// MVC框架服务
	private static MvcFrameworkService mvcFrameworkService;

	public static MvcFrameworkService getMvcFrameworkService() {
		return mvcFrameworkService;
	}

	public MvcContext() {
		bundleListener = new SynchronousBundleListener() {
			@Override
			public void bundleChanged(BundleEvent arg0) {
				Bundle bundle = arg0.getBundle();
				int bundleEventType = arg0.getType();

				BundleContext bundleContext = Activator.getContext();
				if (bundleContext == null)
					return;
				Bundle coreBundle = bundleContext.getBundle();
				if (bundleEventType == BundleEvent.STOPPING) {
					// 移除插件的控制器
					if (mvcFrameworkService == null)
						return;
					mvcFrameworkService.removeBundle(bundle);
				}
			}
		};
	}

	@Override
	public void init() {
		super.addSimpleServiceStaticFieldLink(
				MvcFrameworkService.class.getName(), "mvcFrameworkService");
		// 添加插件监听器
		Activator.getContext().addBundleListener(bundleListener);
	}

	@Override
	public void destory() {
		Activator.getContext().removeBundleListener(bundleListener);
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
