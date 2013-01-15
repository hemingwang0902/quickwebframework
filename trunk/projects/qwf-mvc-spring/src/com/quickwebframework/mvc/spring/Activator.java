package com.quickwebframework.mvc.spring;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;

import com.quickwebframework.mvc.MvcFrameworkService;
import com.quickwebframework.mvc.spring.core.support.BundleControllerHandler;
import com.quickwebframework.mvc.spring.core.support.BundleFilterHandler;
import com.quickwebframework.mvc.spring.core.support.BundleListenerHandler;
import com.quickwebframework.mvc.spring.core.support.BundleThreadHandler;
import com.quickwebframework.mvc.spring.service.impl.SpringMvcFrameworkService;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static SpringMvcFrameworkService mvcFrameworkService;
	private ServiceRegistration<?> mvcFrameworkServiceRegistration;
	private BundleListener bundleListener;

	public static BundleContext getContext() {
		return context;
	}

	public static SpringMvcFrameworkService getSpringMvcFrameworkService() {
		return mvcFrameworkService;
	}

	public Activator() {
		bundleListener = new SynchronousBundleListener() {

			@Override
			public void bundleChanged(BundleEvent arg0) {
				int bundleEventType = arg0.getType();
				if (BundleEvent.STARTED == bundleEventType) {
					addBundleToMvcFramework(arg0.getBundle());
				}
			}
		};
	}

	// 将Bundle添加到框架中
	private void addBundleToMvcFramework(Bundle bundle) {
		// 如果是系统Bundle
		if (bundle.getBundleId() == 0)
			return;

		// 如果还没有添加到MVC框架中，则添加到MVC框架中
		if (!mvcFrameworkService.containsBundle(bundle))
			mvcFrameworkService.addBundle(bundle);
	}

	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		// 注册MVC框架服务
		mvcFrameworkService = new SpringMvcFrameworkService();

		// 注册Spring MVC框架的BundleHandler
		mvcFrameworkService
				.registerBundleHandler(new BundleControllerHandler());
		mvcFrameworkService.registerBundleHandler(new BundleFilterHandler());
		mvcFrameworkService.registerBundleHandler(new BundleListenerHandler());
		mvcFrameworkService.registerBundleHandler(new BundleThreadHandler());

		mvcFrameworkServiceRegistration = bundleContext.registerService(
				MvcFrameworkService.class.getName(), mvcFrameworkService, null);
		// 添加插件监听器
		bundleContext.addBundleListener(bundleListener);
		// 添加到MVC框架中
		for (Bundle bundle : bundleContext.getBundles()) {
			// 如果状态是已激活
			if (bundle.getState() == Bundle.ACTIVE) {
				addBundleToMvcFramework(bundle);
			}
		}
	}

	public void stop(BundleContext bundleContext) throws Exception {
		// 从MVC框架中移除
		for (Bundle bundle : mvcFrameworkService.bundleApplicationContextMap
				.keySet().toArray(new Bundle[0])) {
			mvcFrameworkService.removeBundle(bundle);
		}
		// 取消注册MVC框架服务
		mvcFrameworkServiceRegistration.unregister();
		// 移除插件监听器
		bundleContext.removeBundleListener(bundleListener);
		Activator.context = null;
	}
}
