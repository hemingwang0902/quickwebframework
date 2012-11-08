package com.quickwebframework.framework;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.service.IocFrameworkService;

public class IocContext {
	private static Log log = LogFactory.getLog(IocContext.class);

	private static IocFrameworkService iocFrameworkService;

	public static void init() {
		final BundleContext bundleContext = FrameworkContext.coreBundle
				.getBundleContext();
		// 刷新IoC框架服务
		refreshIocFrameworkService(bundleContext);

		bundleContext.addBundleListener(new BundleListener() {
			@Override
			public void bundleChanged(BundleEvent arg0) {
				Bundle bundle = arg0.getBundle();
				int bundleEventType = arg0.getType();
				// 如果是已经停止
				if (bundleEventType == BundleEvent.STOPPED) {
					if (iocFrameworkService == null)
						return;
					removeBundle(bundle);
				}
			}
		});

		bundleContext.addServiceListener(new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent arg0) {
				String serviceReferenceName = arg0.getServiceReference()
						.toString();
				// 如果IoC框架服务改变，刷新IoC框架
				if (serviceReferenceName.contains(IocFrameworkService.class
						.getName())) {
					refreshIocFrameworkService(bundleContext);
				}
			}
		});
	}

	// 刷新IoC框架服务
	private static void refreshIocFrameworkService(BundleContext bundleContext) {
		try {
			ServiceReference<?> iocFrameworkServiceReference = bundleContext
					.getServiceReference(IocFrameworkService.class.getName());
			if (iocFrameworkServiceReference == null) {
				iocFrameworkService = null;
			} else {
				iocFrameworkService = (IocFrameworkService) bundleContext
						.getService(iocFrameworkServiceReference);
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * 添加一个Bundle到IoC框架中
	 * 
	 * @param bundle
	 */
	public static void addBundle(Bundle bundle) {
		checkIocFrameworkExist();
		iocFrameworkService.addBundle(bundle);
	}

	/**
	 * 移除一个Bundle到IoC框架中
	 * 
	 * @param bundle
	 */
	public static void removeBundle(Bundle bundle) {
		checkIocFrameworkExist();
		iocFrameworkService.removeBundle(bundle);
	}

	/**
	 * 从IoC框架中得到此Bundle对应的应用程序上下文
	 * 
	 * @param bundle
	 * @return
	 */
	public static Object getBundleApplicationContext(Bundle bundle) {
		checkIocFrameworkExist();
		return iocFrameworkService.getBundleApplicationContext(bundle);
	}

	private static void checkIocFrameworkExist() {
		if (iocFrameworkService == null) {
			log.error("未发现有注册的IocFrameworkService服务！");
			throw new RuntimeException("未发现有注册的IocFrameworkService服务！");
		}
	}
}
