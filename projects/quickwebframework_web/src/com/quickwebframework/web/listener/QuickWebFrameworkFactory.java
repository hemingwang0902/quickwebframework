package com.quickwebframework.web.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import com.quickwebframework.web.servlet.PluginManageServlet;
import com.quickwebframework.web.servlet.PluginResourceDispatcherServlet;
import com.quickwebframework.web.servlet.PluginViewDispatcherServlet;
import com.quickwebframework.web.servlet.QwfServlet;
import com.quickwebframework.web.thread.BundleAutoManageThread;

public abstract class QuickWebFrameworkFactory {
	static {
		loggerMap = new HashMap<String, Logger>();
	}

	public static Logger logger = QuickWebFrameworkFactory
			.getLogger(QuickWebFrameworkFactory.class.getName());

	private static Map<String, Logger> loggerMap;

	/**
	 * 得到日志器
	 * 
	 * @param loggerName
	 * @return
	 */
	public static Logger getLogger(String loggerName) {
		if (loggerMap.containsKey(loggerName)) {
			return loggerMap.get(loggerName);
		}

		Logger tmpLogger = Logger.getLogger(loggerName);
		tmpLogger.setLevel(Level.FINEST);

		Handler[] handlers = tmpLogger.getHandlers();
		for (Handler handler : handlers) {
			tmpLogger.removeHandler(handler);
		}

		if (logBridgeObject == null)
			tmpLogger.addHandler(new java.util.logging.ConsoleHandler());
		else
			tmpLogger.addHandler(logBridgeObject);

		loggerMap.put(loggerName, tmpLogger);
		return tmpLogger;
	}

	public final static String CONST_FRAMEWORK_CORE_BUNDLE_NAME = "com.quickwebframework.core";
	// HttpServlet桥接对象类名
	public final static String CONST_HTTP_SERVLET_BRIDGE_CLASS_NAME = "com.quickwebframework.bridge.HttpServletBridge";
	// 日志桥接对象类名
	public final static String CONST_LOG_BRIDGE_CLASS_NAME = "com.quickwebframework.bridge.LogBridge";
	// 过滤器桥接对象类名
	public final static String CONST_SERVLET_FILTER_BRIDGE_CLASS_NAME = "com.quickwebframework.bridge.ServletFilterBridge";
	// 监听器桥接对象类名
	public final static String CONST_SERVLET_LISTENER_BRIDGE_CLASS_NAME = "com.quickwebframework.bridge.ServletListenerBridge";

	// 插件配置
	public final static String PLUGIN_CONFIG_PROPERTY_KEY = "quickwebframework.config";

	// 配置文件路径参数名称
	public final static String CONFIG_LOCATION_PARAMETER_NAME = "quickwebframeworkConfigLocation";

	// QuickWebFramework的配置
	private static Properties quickWebFrameworkProperties;

	private static Framework framework;

	/**
	 * 得到Framework对象
	 * 
	 * @return
	 */
	public static Framework getFramework() {
		return framework;
	}

	/**
	 * 得到BundleContext对象
	 * 
	 * @param servletContext
	 * @return
	 */
	public static BundleContext getBundleContext() {
		return framework.getBundleContext();
	}

	// HttpServlet桥接对象
	private static HttpServlet httpServletBridgeObject;
	// 日志桥接对象
	private static Handler logBridgeObject;
	// Servlet过滤器桥接对象
	private static Filter servletFilterBridgeObject;
	// Servlet监听器桥接对象
	private static EventListener servletListenerBridgeObject;

	public static HttpServlet getHttpServletBridgeObject() {
		return httpServletBridgeObject;
	}

	public static Handler getLogBridgeObject() {
		return logBridgeObject;
	}

	public static Filter getServletFilterBridgeObject() {
		return servletFilterBridgeObject;
	}

	public static EventListener getServletListenerBridgeObject() {
		return servletListenerBridgeObject;
	}

	// 刷新框架各桥接对象
	private static void refreshFrameworkBridgeObject() {
		httpServletBridgeObject = (HttpServlet) getOsgiServiceObject(CONST_HTTP_SERVLET_BRIDGE_CLASS_NAME);
		logBridgeObject = (Handler) getOsgiServiceObject(CONST_LOG_BRIDGE_CLASS_NAME);
		servletFilterBridgeObject = (Filter) getOsgiServiceObject(CONST_SERVLET_FILTER_BRIDGE_CLASS_NAME);
		servletListenerBridgeObject = (EventListener) getOsgiServiceObject(CONST_SERVLET_LISTENER_BRIDGE_CLASS_NAME);

		// 刷新各日志的处理器
		if (logBridgeObject != null) {
			synchronized (loggerMap) {
				for (String tmpLoggerName : loggerMap.keySet()) {
					Logger tmpLogger = loggerMap.get(tmpLoggerName);

					// 先移除所有的处理器
					Handler[] tmpLoggerHandlers = tmpLogger.getHandlers();
					for (Handler tmpLoggerHandler : tmpLoggerHandlers) {
						tmpLogger.removeHandler(tmpLoggerHandler);
					}
					// 添加框架中的日志处理器
					tmpLogger.addHandler(logBridgeObject);
				}
			}
		}
	}

	// 得到OSGi服务对象
	private static Object getOsgiServiceObject(String serviceName) {
		BundleContext bundleContext = getBundleContext();
		ServiceReference<?> serviceReference = bundleContext
				.getServiceReference(serviceName);
		if (serviceReference == null)
			return null;
		return bundleContext.getService(serviceReference);
	}

	// 相应的Servlet
	public static List<QwfServlet> qwfServletList = new ArrayList<QwfServlet>();

	// 启动OSGi Freamwork
	public void startOSGiFreamwork(ServletContext servletContext) {
		String propertiesFileName = servletContext
				.getInitParameter(CONFIG_LOCATION_PARAMETER_NAME);

		if (propertiesFileName == null) {
			throw new RuntimeException(String.format(
					"Servlet参数[%s]未找到，QuickWebFramework启动失败！",
					CONFIG_LOCATION_PARAMETER_NAME));
		}

		String quickWebFrameworkPropertiesFilePath = servletContext
				.getRealPath(propertiesFileName);

		File quickWebFrameworkPropertiesFile = new File(
				quickWebFrameworkPropertiesFilePath);
		if (!quickWebFrameworkPropertiesFile.exists()
				|| !quickWebFrameworkPropertiesFile.isFile()) {
			throw new RuntimeException(String.format(
					"QuickWebFramework配置文件[%s]未找到！",
					quickWebFrameworkPropertiesFilePath));
		}

		// QuickWebFramework的配置
		quickWebFrameworkProperties = new Properties();
		try {
			InputStream quickWebFrameworkPropertiesInputStream = new FileInputStream(
					quickWebFrameworkPropertiesFilePath);
			Reader quickWebFrameworkPropertiesReader = new InputStreamReader(
					quickWebFrameworkPropertiesInputStream, "utf-8");
			quickWebFrameworkProperties.load(quickWebFrameworkPropertiesReader);
			quickWebFrameworkPropertiesReader.close();
			quickWebFrameworkPropertiesInputStream.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// ====================
		// 初始化OSGi框架
		// ====================

		// 配置Map
		Map<String, String> osgiFrameworkConfigMap = new HashMap<String, String>();

		// 配置缓存保存路径
		String osgiFrameworkStorage = quickWebFrameworkProperties
				.getProperty("quickwebframework.osgiFrameworkStorage");
		osgiFrameworkStorage = servletContext.getRealPath(osgiFrameworkStorage);
		if (osgiFrameworkStorage != null) {
			osgiFrameworkConfigMap.put("org.osgi.framework.storage",
					osgiFrameworkStorage);
		}

		// 读取固定配置
		String osgiFrameworkFactoryConfig = quickWebFrameworkProperties
				.getProperty("quickwebframework.osgiFrameworkFactoryConfig");
		if (osgiFrameworkFactoryConfig != null) {
			String[] configLines = osgiFrameworkFactoryConfig.split(";");
			for (String configLine : configLines) {
				String[] tmpArray = configLine.split("=");
				if (tmpArray.length >= 2) {
					String key = tmpArray[0].trim();
					String value = tmpArray[1].trim();
					osgiFrameworkConfigMap.put(key, value);
				}
			}
		}

		// 加载OSGi框架类或OSGi框架工厂类
		String osgiFrameworkClass = quickWebFrameworkProperties
				.getProperty("quickwebframework.osgiFrameworkClass");
		if (osgiFrameworkClass != null) {
			Class<?> osgiFrameworkClazz;
			try {
				osgiFrameworkClazz = Class.forName(osgiFrameworkClass);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("查找osgiFrameworkClass类失败！-->"
						+ osgiFrameworkClass, e);
			}
			// 如果osgiFrameworkClazz不是Framework的派生类
			if (!Framework.class.isAssignableFrom(osgiFrameworkClazz)) {
				throw new RuntimeException(
						"指定的osgiFrameworkClass不是org.osgi.framework.launch.Framework的派生类！-->"
								+ osgiFrameworkClass);
			}
			// Framework构造函数
			Constructor<?> frameworkConstructor;
			try {
				frameworkConstructor = osgiFrameworkClazz
						.getConstructor(Map.class);
			} catch (SecurityException e) {
				throw new RuntimeException("获取osgiFrameworkClass的构造函数时出现安全异常。",
						e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(
						"未找到osgiFrameworkClass的Class(Map map)构造函数。", e);
			}
			try {
				framework = (Framework) frameworkConstructor
						.newInstance(osgiFrameworkConfigMap);
			} catch (Exception e) {
				throw new RuntimeException("初始化osgiFrameworkClass的实例时出现异常。", e);
			}
			logger.info("正在启动OSGi框架，OSGi框架类: " + osgiFrameworkClass);
		} else {
			String osgiFrameworkFactoryClass = quickWebFrameworkProperties
					.getProperty("quickwebframework.osgiFrameworkFactoryClass");
			Class<?> osgiFrameworkFactoryClazz;
			try {
				osgiFrameworkFactoryClazz = Class
						.forName(osgiFrameworkFactoryClass);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("查找osgiFrameworkFactoryClass类失败！-->"
						+ osgiFrameworkFactoryClass, e);
			}
			// 如果osgiFrameworkFactoryClazz不是FrameworkFactory的派生类
			if (!FrameworkFactory.class
					.isAssignableFrom(osgiFrameworkFactoryClazz)) {
				throw new RuntimeException(
						"指定的osgiFrameworkFactoryClass不是org.osgi.framework.launch.FrameworkFactory的派生类！-->"
								+ osgiFrameworkFactoryClass);
			}

			FrameworkFactory factory;
			try {
				factory = (FrameworkFactory) osgiFrameworkFactoryClazz
						.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("初始化osgiFrameworkFactoryClass失败！", e);
			}
			framework = factory.newFramework(osgiFrameworkConfigMap);

			logger.info("正在启动OSGi框架，OSGi框架工厂类: " + osgiFrameworkFactoryClass);
		}

		long osgiFrameworkStartTime = System.currentTimeMillis();

		try {
			// Framework初始化
			framework.init();

			QwfServlet tmpServlet = null;
			// 初始化插件视图Servlet
			tmpServlet = PluginViewDispatcherServlet.initServlet(
					servletContext, quickWebFrameworkProperties);
			if (tmpServlet != null) {
				qwfServletList.add(tmpServlet);
			}
			// 初始化插件资源Servlet
			tmpServlet = PluginResourceDispatcherServlet.initServlet(
					servletContext, quickWebFrameworkProperties);
			if (tmpServlet != null) {
				qwfServletList.add(tmpServlet);
			}
			// 初始化插件管理Servlet
			tmpServlet = PluginManageServlet.initServlet(servletContext,
					quickWebFrameworkProperties);
			if (tmpServlet != null) {
				qwfServletList.add(tmpServlet);
			}

			// 将ServletContext注册为服务
			getBundleContext().registerService(ServletContext.class.getName(),
					servletContext, null);

			// 设置插件要用到的配置文件
			Enumeration<?> quickWebFrameworkPropertieNameEnumeration = quickWebFrameworkProperties
					.propertyNames();
			while (quickWebFrameworkPropertieNameEnumeration.hasMoreElements()) {
				String propertieName = (String) quickWebFrameworkPropertieNameEnumeration
						.nextElement();
				if (propertieName.startsWith(PLUGIN_CONFIG_PROPERTY_KEY + ".")) {
					String propName = propertieName
							.substring((PLUGIN_CONFIG_PROPERTY_KEY + ".")
									.length());
					String propValue = quickWebFrameworkProperties
							.getProperty(propertieName);
					Dictionary<String, String> dict = new Hashtable<String, String>();
					dict.put(PLUGIN_CONFIG_PROPERTY_KEY, propName);
					getBundleContext().registerService(String.class.getName(),
							propValue, dict);
				}
			}
			// 设置WEB根目录到系统配置中
			System.setProperty("web.root.dir", servletContext.getRealPath("/"));

			// Bundle监听器
			getBundleContext().addBundleListener(new BundleListener() {
				public void bundleChanged(BundleEvent event) {
					if (event.getType() == BundleEvent.STARTED) {
						if (event.getBundle().getSymbolicName()
								.equals(CONST_FRAMEWORK_CORE_BUNDLE_NAME)) {
							refreshFrameworkBridgeObject();
						}
					}
				}
			});

			framework.start();

			// 启动OSGi框架所用时间
			long startFrameworkUsedTime = System.currentTimeMillis()
					- osgiFrameworkStartTime;
			logger.info(String.format("启动OSGi框架完成，用时 %,d ms",
					startFrameworkUsedTime));

			// 扫描插件目录，看是否有插件需要自动安装
			Thread trdBundleAutoManage = new BundleAutoManageThread(
					osgiFrameworkStorage);
			trdBundleAutoManage.start();
		} catch (BundleException e) {
			throw new RuntimeException("启动OSGi Framework失败！", e);
		}
	}

	/**
	 * 停止OSGi框架
	 */
	public void stopOSGiFramework() {
		try {
			if (framework != null) {
				framework.stop();
				framework.waitForStop(0);
			}
			logger.info("停止OSGi Framework成功！");
		} catch (Exception e) {
			throw new RuntimeException("停止OSGi Framework失败！", e);
		}
	}
}