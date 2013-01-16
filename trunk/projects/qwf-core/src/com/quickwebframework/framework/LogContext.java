package com.quickwebframework.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceRegistration;

import com.quickwebframework.bridge.LogBridge;
import com.quickwebframework.core.Activator;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.entity.impl.DefaultLogImpl;
import com.quickwebframework.entity.impl.JavaLoggerImpl;

@SuppressWarnings("deprecation")
public class LogContext extends FrameworkContext {
	private static LogContext instance;

	protected static LogContext getInstance() {
		if (instance == null)
			instance = new LogContext();
		return instance;
	}

	// ======变量部分开始
	// 日志器Map
	private static Map<String, Log> logMap;
	private ServiceRegistration<?> logBridgeServiceRegistration;

	// ======变量部分结束

	public LogContext() {
		if (logMap == null)
			logMap = new HashMap<String, Log>();
	}

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getContext();
	}

	@Override
	protected void init(int arg) {
		BundleContext bundleContext = getBundleContext();
		// 设置默认的Java日志记录器配置
		try {

			String javaLoggerLevelStr = WebContext
					.getQwfConfig("qwf-core.javalogger.level");
			if (javaLoggerLevelStr != null) {
				JavaLoggerImpl.javaLoggerLevel = Level
						.parse(javaLoggerLevelStr);
			}
		} catch (Exception ex) {
			LogFactory.getLog(LogContext.class.getName()).error(
					"设置默认的Java日志记录器配置时出错。");
		}

		// 注册日志桥接对象
		logBridgeServiceRegistration = bundleContext.registerService(
				LogBridge.class.getName(), new LogBridge(), null);
	}

	@Override
	protected void destory(int arg) {
		// 取消注册日志桥接对象
		logBridgeServiceRegistration.unregister();
	}

	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	public static Log getLog(String name) {
		Log log = null;
		if (logMap == null)
			logMap = new HashMap<String, Log>();
		synchronized (logMap) {
			if (logMap.containsKey(name)) {
				log = logMap.get(name);
			} else {
				log = new DefaultLogImpl(Activator.getContext(), name);
				logMap.put(name, log);
			}
		}
		return log;
	}

	@Override
	protected void bundleChanged(BundleEvent event) {
	}

	@Override
	protected void serviceChanged(ServiceEvent event) {

	}
}
