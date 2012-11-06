package com.quickwebframework.framework;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;

public class FrameworkContext {

	private static Log log = LogFactory.getLog(FrameworkContext.class);
	// 核心Bundle
	static Bundle coreBundle;

	// 插件名称插件Map
	private static Map<String, Bundle> bundleNameBundleMap = new HashMap<String, Bundle>();

	// 根据插件名称得到Bundle
	public static Bundle getBundleByName(String bundleName) {
		if (bundleNameBundleMap.containsKey(bundleName)) {
			return bundleNameBundleMap.get(bundleName);
		}
		return null;
	}

	public static void init(Bundle coreBundle) {
		FrameworkContext.coreBundle = coreBundle;

		BundleContext bundleContext = coreBundle.getBundleContext();
		// 添加OSGi插件监听器
		bundleContext.addBundleListener(new BundleListener() {

			@Override
			public void bundleChanged(BundleEvent arg0) {
				int eventType = arg0.getType();
				Bundle bundle = arg0.getBundle();
				String bundleName = bundle.getSymbolicName();

				// 如果插件已经启动
				if (eventType == BundleEvent.STARTED) {
					bundleNameBundleMap.put(bundleName, bundle);
				}
				// 如果插件的状态是正在停止或已经停止
				else if (eventType == BundleEvent.STOPPED
						|| eventType == BundleEvent.STOPPING) {
					if (bundleNameBundleMap.containsKey(bundleName)) {
						bundleNameBundleMap.remove(bundleName);
					}
				}
			}
		});

		// 服务注册和取消时提示
		bundleContext.addServiceListener(new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent arg0) {
				int serviceEventType = arg0.getType();
				if (serviceEventType == ServiceEvent.REGISTERED) {
					log.info(String.format("[%s]插件的[%s]服务已注册", arg0
							.getServiceReference().getBundle()
							.getSymbolicName(), arg0.getServiceReference()));
				} else if (serviceEventType == ServiceEvent.UNREGISTERING) {
					log.info(String.format("[%s]插件的[%s]服务正在取消注册", arg0
							.getServiceReference().getBundle()
							.getSymbolicName(), arg0.getServiceReference()));
				}
			}
		});

		ListenerContext.init();
		FilterContext.init();
		WebContext.init();
		ThreadContext.init();
	}
}
