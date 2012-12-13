package com.quickwebframework.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.osgi.framework.BundleContext;

import com.quickwebframework.bridge.LogBridge;
import com.quickwebframework.core.Activator;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.entity.impl.DefaultLogImpl;
import com.quickwebframework.entity.impl.JavaLoggerImpl;
import com.quickwebframework.util.BundleContextUtil;

public class LogContext extends FrameworkContext {
	private static LogContext instance;

	public static LogContext getInstance() {
		if (instance == null)
			instance = new LogContext();
		return instance;
	}

	// ======变量部分开始
	private static Log log = LogFactory.getLog(LogContext.class.getName());
	// 日志器Map
	private Map<String, Log> logMap;

	// ======变量部分结束

	public LogContext() {
		logMap = new HashMap<String, Log>();
	}

	@Override
	public void init() {
		BundleContext bundleContext = Activator.getContext();
		// 设置默认的Java日志记录器配置
		try {
			Object obj = BundleContextUtil
					.getServiceObject(bundleContext, String.class.getName(),
							"(quickwebframework.config=com.quickwebframework.core.javalogger.level)");
			if (obj != null) {
				String javaLoggerLevelStr = (String) obj;
				JavaLoggerImpl.javaLoggerLevel = Level
						.parse(javaLoggerLevelStr);
			}
		} catch (Exception ex) {
			log.error("设置默认的Java日志记录器配置时出错。");
		}

		// 注册日志桥接对象
		bundleContext.registerService(LogBridge.class.getName(),
				new LogBridge(), null);
	}

	@Override
	public void destory() {

	}

	public Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	public Log getLog(String name) {
		Log log = null;
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
}
