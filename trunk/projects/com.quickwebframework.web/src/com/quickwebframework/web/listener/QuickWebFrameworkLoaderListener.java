package com.quickwebframework.web.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Dictionary;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.FilterRegistration;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import com.quickwebframework.web.filter.PluginFilter;
import com.quickwebframework.web.servlet.PluginManageServlet;
import com.quickwebframework.web.servlet.PluginResourceDispatcherServlet;
import com.quickwebframework.web.servlet.PluginViewDispatcherServlet;
import com.quickwebframework.web.servlet.WebDefaultServlet;

public class QuickWebFrameworkLoaderListener implements ServletContextListener {

	public final static String CONST_DISPATCHER_SERVLET_CLASS_NAME = "com.quickwebframework.core.DispatcherServlet";
	public final static String PLUGIN_CONFIG_FILES_PROPERTY_KEY = "quickwebframework.pluginConfigFiles.";

	// 配置文件路径参数名称
	public final static String CONFIG_LOCATION_PARAMETER_NAME = "quickwebframeworkConfigLocation";

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

	private static Object dispatcherServletObject;

	/**
	 * 得到DispatcherServlet对象
	 * 
	 * @return
	 */
	public static Object getDispatcherServletObject() {
		return dispatcherServletObject;
	}

	// 刷新分发Servlet对象
	private static void refreshDispatcherServletObject() {
		ServiceReference<?> serviceReference = getBundleContext()
				.getServiceReference(CONST_DISPATCHER_SERVLET_CLASS_NAME);
		if (serviceReference == null)
			return;
		dispatcherServletObject = getBundleContext().getService(
				serviceReference);
	}

	public void contextInitialized(ServletContextEvent arg0) {
		ServletContext servletContext = arg0.getServletContext();
		// 初始化OSGi框架
		initOSGiFreamwork(servletContext);
	}

	// 初始化OSGi Freamwork
	private void initOSGiFreamwork(ServletContext servletContext) {
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
		Properties quickWebFrameworkProperties = new Properties();
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
		String osgiFrameworkFactoryClass = quickWebFrameworkProperties
				.getProperty("quickwebframework.osgiFrameworkFactoryClass");
		Class<?> osgiFrameworkFactoryClazz;
		try {
			osgiFrameworkFactoryClazz = Class
					.forName(osgiFrameworkFactoryClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("查找osgiFrameworkFactoryClass类失败！", e);
		}
		// 如果osgiFrameworkFactoryClazz不是FrameworkFactory的派生类
		if (!FrameworkFactory.class.isAssignableFrom(osgiFrameworkFactoryClazz)) {
			throw new RuntimeException(
					"指定的osgiFrameworkFactoryClass不是org.osgi.framework.launch.FrameworkFactory的派生类！");
		}
		FrameworkFactory factory;
		try {
			factory = (FrameworkFactory) osgiFrameworkFactoryClazz
					.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("初始化osgiFrameworkFactoryClass失败！", e);
		}
		// 配置Map
		Map<String, String> osgiFrameworkConfigMap = new HashMap<String, String>();
		osgiFrameworkConfigMap.put("org.osgi.framework.storage",
				servletContext.getRealPath("WEB-INF/plugin"));
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

		framework = factory.newFramework(osgiFrameworkConfigMap);

		try {
			// Framework初始化
			framework.init();

			// 将ServletContext注册为服务
			getBundleContext().registerService(ServletContext.class.getName(),
					servletContext, null);

			// 设置插件要用到的配置文件
			Enumeration<?> quickWebFrameworkPropertieNameEnumeration = quickWebFrameworkProperties
					.propertyNames();
			while (quickWebFrameworkPropertieNameEnumeration.hasMoreElements()) {
				String propertieName = (String) quickWebFrameworkPropertieNameEnumeration
						.nextElement();
				if (propertieName.startsWith(PLUGIN_CONFIG_FILES_PROPERTY_KEY)) {
					String propName = propertieName
							.substring(PLUGIN_CONFIG_FILES_PROPERTY_KEY
									.length());
					String filePath = quickWebFrameworkProperties
							.getProperty(propertieName);
					Dictionary<String, String> dict = new Hashtable<String, String>();
					dict.put("quickwebframework.pluginConfigFile", propName);
					getBundleContext().registerService(String.class.getName(),
							servletContext.getRealPath(filePath), dict);
				}
			}

			// Bundle监听器
			getBundleContext().addBundleListener(new BundleListener() {
				public void bundleChanged(BundleEvent arg0) {
				}
			});
			// Service监听器
			getBundleContext().addServiceListener(new ServiceListener() {
				public void serviceChanged(ServiceEvent arg0) {
					// 如果是DispatcherServlet服务更改
					if (arg0.getServiceReference().toString()
							.contains(CONST_DISPATCHER_SERVLET_CLASS_NAME)) {
						refreshDispatcherServletObject();
					}
				}
			});

			framework.start();

		} catch (BundleException e) {
			throw new RuntimeException("启动OSGi Framework失败！", e);
		}

		// 初始化插件视图Servlet
		HttpServlet pluginViewDispatcherServlet = PluginViewDispatcherServlet
				.initServlet(servletContext, quickWebFrameworkProperties);
		// 初始化插件资源Servlet
		PluginResourceDispatcherServlet.initServlet(servletContext,
				quickWebFrameworkProperties);
		// 初始化插件管理Servlet
		PluginManageServlet.initServlet(servletContext,
				quickWebFrameworkProperties);
		// 初始化Web资源Servlet
		WebDefaultServlet.initServlet(servletContext,
				pluginViewDispatcherServlet);

		// 添加过滤器
		FilterRegistration.Dynamic filterDynamic = servletContext.addFilter(
				PluginFilter.class.getName(), PluginFilter.class);
		EnumSet<DispatcherType> dispatcherTypeSet = EnumSet
				.noneOf(DispatcherType.class);
		dispatcherTypeSet.add(DispatcherType.REQUEST);
		dispatcherTypeSet.add(DispatcherType.FORWARD);
		dispatcherTypeSet.add(DispatcherType.INCLUDE);
		dispatcherTypeSet.add(DispatcherType.ERROR);

		filterDynamic.addMappingForServletNames(dispatcherTypeSet, true,
				PluginViewDispatcherServlet.class.getName());
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			if (framework != null)
				framework.stop();
		} catch (BundleException e) {
			throw new RuntimeException("停止OSGi Framework失败！", e);
		}
	}
}
