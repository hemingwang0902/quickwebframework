package com.quickwebframework.mvc.spring;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;

import com.quickwebframework.framework.FrameworkContext;
import com.quickwebframework.mvc.spring.service.impl.SpringMvcFrameworkService;
import com.quickwebframework.mvc.spring.support.Activator;
import com.quickwebframework.mvc.spring.support.BundleControllerHandler;
import com.quickwebframework.mvc.spring.support.BundleFilterHandler;
import com.quickwebframework.mvc.spring.support.BundleListenerHandler;
import com.quickwebframework.mvc.spring.support.BundleThreadHandler;

public class SpringMvcContext extends FrameworkContext {
	private static SpringMvcContext instance;

	public static SpringMvcContext getInstance() {
		if (instance == null)
			instance = new SpringMvcContext();
		return instance;
	}

	private static SpringMvcFrameworkService mvcFrameworkService;

	/**
	 * 得到Spring MVC框架服务
	 * 
	 * @return
	 */
	public static SpringMvcFrameworkService getSpringMvcFrameworkService() {
		return mvcFrameworkService;
	}

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getContext();
	}

	@Override
	protected void init(int arg) {
		// 注册MVC框架服务
		mvcFrameworkService = new SpringMvcFrameworkService();

		// 注册Spring MVC框架的BundleHandler
		mvcFrameworkService
				.registerBundleHandler(new BundleControllerHandler());
		mvcFrameworkService.registerBundleHandler(new BundleFilterHandler());
		mvcFrameworkService.registerBundleHandler(new BundleListenerHandler());
		mvcFrameworkService.registerBundleHandler(new BundleThreadHandler());

		// 添加到MVC框架中
		for (Bundle bundle : getBundleContext().getBundles()) {
			// 如果是系统Bundle，则不处理
			if (bundle.getBundleId() == 0) {
				continue;
			}
			// 如果状态是已激活
			if (bundle.getState() == Bundle.ACTIVE) {
				addBundleToMvcFramework(bundle);
			}
		}
	}

	@Override
	protected void destory(int arg) {
		// 从MVC框架中移除
		for (Bundle bundle : mvcFrameworkService.bundleApplicationContextMap
				.keySet().toArray(new Bundle[0])) {
			mvcFrameworkService.removeBundle(bundle);
		}
	}

	@Override
	protected void bundleChanged(BundleEvent event) {
		int bundleEventType = event.getType();
		Bundle bundle = event.getBundle();
		if (BundleEvent.STARTED == bundleEventType) {
			// 添加插件的控制器
			addBundleToMvcFramework(bundle);
		} else if (BundleEvent.STOPPING == bundleEventType) {
			// 移除插件的控制器
			mvcFrameworkService.removeBundle(bundle);
		}
	}

	@Override
	protected void serviceChanged(ServiceEvent event) {

	}

	private void addBundleToMvcFramework(Bundle bundle) {
		if (!mvcFrameworkService.containsBundle(bundle)) {
			mvcFrameworkService.addBundle(bundle);
		}
	}
}
