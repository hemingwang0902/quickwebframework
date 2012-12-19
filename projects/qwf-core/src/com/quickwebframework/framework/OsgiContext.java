package com.quickwebframework.framework;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.launch.Framework;

import com.quickwebframework.core.Activator;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;

public class OsgiContext extends FrameworkContext {

	private static OsgiContext instance;

	protected static OsgiContext getInstance() {
		if (instance == null)
			instance = new OsgiContext();
		return instance;
	}

	// ======变量开始部分
	private static Log log = LogFactory.getLog(OsgiContext.class);
	// 插件名称插件Map
	private static Map<String, Bundle> bundleNameBundleMap;
	// 插件监听器
	private BundleListener bundleListener;
	// 服务监听器
	private ServiceListener serviceListener;
	// OSGi的Framework
	private static Framework framework;

	// ======变量开始结束

	public OsgiContext() {
		bundleNameBundleMap = new HashMap<String, Bundle>();
		bundleListener = new SynchronousBundleListener() {
			@Override
			public void bundleChanged(BundleEvent arg0) {
				int eventType = arg0.getType();
				Bundle bundle = arg0.getBundle();
				String bundleName = bundle.getSymbolicName();

				if (eventType == BundleEvent.STARTING) {
					log.debug(String.format("正在启动插件[%s]...", bundleName));
				} else if (eventType == BundleEvent.STARTED) {
					log.debug(String.format("插件[%s]已启动", bundleName));
					bundleNameBundleMap.put(bundleName, bundle);
				} else if (eventType == BundleEvent.STOPPING) {
					log.debug(String.format("正在停止插件[%s]...", bundleName));
				} else if (eventType == BundleEvent.STOPPED) {
					log.debug(String.format("插件[%s]已停止", bundleName));
					if (bundleNameBundleMap.containsKey(bundleName)) {
						bundleNameBundleMap.remove(bundleName);
					}
				}
			}
		};

		serviceListener = new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent event) {
				int serviceEventType = event.getType();
				if (serviceEventType == ServiceEvent.REGISTERED) {
					log.debug(String.format("插件[%s]的服务[%s]已经注册", event
							.getServiceReference().getBundle()
							.getSymbolicName(), event.getServiceReference()));
				} else if (serviceEventType == ServiceEvent.MODIFIED) {
					log.debug(String.format("插件[%s]的服务[%s]已变更", event
							.getServiceReference().getBundle()
							.getSymbolicName(), event.getServiceReference()));
				} else if (serviceEventType == ServiceEvent.UNREGISTERING) {
					log.debug(String.format("插件[%s]的服务[%s]正在取消注册...", event
							.getServiceReference().getBundle()
							.getSymbolicName(), event.getServiceReference()));
				}
			}
		};
	}

	@Override
	protected void init() {
		BundleContext bundleContext = Activator.getContext();
		// 添加OSGi插件监听器
		bundleContext.addBundleListener(bundleListener);
		// 添加OSGi服务监听器
		bundleContext.addServiceListener(serviceListener);

		this.addSimpleServiceStaticFieldLink(Framework.class.getName(),
				"framework");
	}

	@Override
	protected void destory() {
		BundleContext bundleContext = Activator.getContext();
		// 移除OSGi插件监听器
		bundleContext.removeBundleListener(bundleListener);
		// 移除OSGi服务监听器
		bundleContext.removeServiceListener(serviceListener);
		log.debug(String.format("插件[%s]已停止", bundleContext.getBundle()
				.getSymbolicName()));
	}

	// 根据插件名称得到Bundle
	public static Bundle getBundleByName(String bundleName) {
		if (bundleNameBundleMap.containsKey(bundleName)) {
			return bundleNameBundleMap.get(bundleName);
		}
		return null;
	}

	// 得到核心的Bundle上下文
	public static BundleContext getFrameworkBundleContext() {
		return framework.getBundleContext();
	}
}
