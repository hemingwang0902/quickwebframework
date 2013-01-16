package com.quickwebframework.framework;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;

import com.quickwebframework.bridge.ServletFilterBridge;
import com.quickwebframework.bridge.ServletListenerBridge;
import com.quickwebframework.core.Activator;
import com.quickwebframework.entity.HandlerExceptionResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.quickwebframework.stereotype.FilterSetting;
import com.quickwebframework.util.pattern.WildcardPattern;

public class WebContext extends FrameworkContext {
	private static WebContext instance;

	protected static WebContext getInstance() {
		if (instance == null)
			instance = new WebContext();
		return instance;
	}

	private static Log log = LogFactory.getLog(WebContext.class);

	// QuickwebFramework的过滤器配置状态
	// ===== 常量开始
	public static final String QUICKWEBFRAMEWORK_STATE_FILTERCONFIG = "com.quickwebframework.state.FILTERCONFIG";
	// ===== 常量结束

	// ===== WEB相关变量部分开始
	private static BundleListener bundleListener;

	// WEB项目的ServletContext
	private static ServletContext servletContext;
	// URL未找到处理Servlet
	private static HttpServlet urlNotFoundHandleServlet;
	// 得到处理器异常解决器
	private static HandlerExceptionResolver handlerExceptionResolver;
	// 路径与Servlet映射Map
	private static Map<String, Servlet> pathServletMap;
	// 路径与通配符模板对象映射Map
	private static Map<String, WildcardPattern> pathWildcardPatternMap;

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static HttpServlet getUrlNotFoundHandleServlet() {
		return urlNotFoundHandleServlet;
	}

	public static void setUrlNotFoundHandleServlet(
			HttpServlet urlNotFoundHandleServlet) {
		WebContext.urlNotFoundHandleServlet = urlNotFoundHandleServlet;
	}

	public static HandlerExceptionResolver getHandlerExceptionResolver() {
		return handlerExceptionResolver;
	}

	public static void setHandlerExceptionResolver(
			HandlerExceptionResolver handlerExceptionResolver) {
		WebContext.handlerExceptionResolver = handlerExceptionResolver;
	}

	/**
	 * 得到实际路径
	 * 
	 * @param path
	 * @return
	 */
	public static String getRealPath(String path) {
		if (servletContext == null)
			return null;
		return servletContext.getRealPath(path);
	}

	/**
	 * 得到quickwebframework.properties文件中的quickwebframework.config开头的配置
	 * 
	 * @param configKey
	 * @return
	 */
	public static String getQwfConfig(String configKey) {
		BundleContext context = Activator.getContext();

		ServiceReference<?>[] serviceReferences;
		try {
			serviceReferences = context.getServiceReferences(
					String.class.getName(),
					String.format("(qwf.config=%s)", configKey));
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
		if (serviceReferences != null && serviceReferences.length > 0) {
			return (String) context.getService(serviceReferences[0]);
		}
		return null;
	}

	// ===== WEB相关变量部分结束

	// ===== 过滤器变量部分开始
	// 从上层传递下来的过滤器配置
	private static FilterConfig filterConfig;
	private static List<Filter> filterList;
	private static Map<Bundle, List<Filter>> bundleFilterListMap;
	// 过滤器桥接对象
	private ServiceRegistration<?> servletFilterBridgeServiceRegistration;

	/**
	 * 得到过滤器配置
	 * 
	 * @return
	 */
	public static FilterConfig getFilterConfig() {
		return filterConfig;
	}

	/**
	 * 设置过滤器配置
	 * 
	 * @param filterConfig
	 */
	public static void setFilterConfig(FilterConfig filterConfig) {
		WebContext.filterConfig = filterConfig;
		if (filterConfig == null)
			return;
		for (Filter filter : filterList) {
			try {
				filter.init(filterConfig);
			} catch (ServletException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 得到所有过滤器
	 * 
	 * @return
	 */
	public static Filter[] getFilters() {
		return filterList.toArray(new Filter[0]);
	}

	// ===== 过滤器变量部分结束

	// ===== 监听器变量部分开始
	private static List<EventListener> listenerList;
	private static Map<String, List<EventListener>> typeNameListenerListMap;
	private static Map<Bundle, List<EventListener>> bundleListenerListMap;
	// 监听器桥接对象
	private ServiceRegistration<?> servletListenerBridgeServiceRegistration;

	// ===== 监听器变量部分结束

	public WebContext() {
		filterList = new ArrayList<Filter>();
		bundleFilterListMap = new HashMap<Bundle, List<Filter>>();
		listenerList = new ArrayList<EventListener>();
		typeNameListenerListMap = new HashMap<String, List<EventListener>>();
		bundleListenerListMap = new HashMap<Bundle, List<EventListener>>();
		pathServletMap = new HashMap<String, Servlet>();
		pathWildcardPatternMap = new HashMap<String, WildcardPattern>();
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
					// 移除插件的过滤器
					if (bundle.equals(coreBundle)) {
						WebContext.removeAllFilter();
					} else {
						WebContext.removeBundleAllFilter(bundle);
					}
					// 移除插件的监听器
					if (bundle.equals(coreBundle)) {
						WebContext.removeAllListener();
					} else {
						WebContext.removeBundleAllListener(bundle);
					}
				}
			}
		};
	}

	@Override
	protected void init() {
		super.addSimpleServiceStaticFieldLink(ServletContext.class.getName(),
				"servletContext");

		final BundleContext bundleContext = Activator.getContext();

		// 添加插件监听器
		bundleContext.addBundleListener(bundleListener);

		// 启动时，从ServletContext中读取相关运行时状态
		Object filterConfigObject = getServletContext().getAttribute(
				QUICKWEBFRAMEWORK_STATE_FILTERCONFIG);
		if (filterConfigObject != null)
			setFilterConfig((FilterConfig) filterConfigObject);

		// 注册过滤器桥接对象
		servletFilterBridgeServiceRegistration = bundleContext.registerService(
				ServletFilterBridge.class.getName(), new ServletFilterBridge(),
				null);
		// 注册监听器桥接对象
		servletListenerBridgeServiceRegistration = bundleContext
				.registerService(ServletListenerBridge.class.getName(),
						new ServletListenerBridge(), null);
	}

	@Override
	protected void destory() {
		Activator.getContext().removeBundleListener(bundleListener);
		servletFilterBridgeServiceRegistration.unregister();
		servletListenerBridgeServiceRegistration.unregister();

		// 停止时，保存相关运行时状态到ServletContext中。
		getServletContext().setAttribute(QUICKWEBFRAMEWORK_STATE_FILTERCONFIG,
				getFilterConfig());
	}

	/**
	 * 得到插件方法的URL
	 * 
	 * @param bundleName
	 * @param methodName
	 * @return
	 */
	public static String getBundleMethodUrl(String bundleName, String methodName) {
		throw new RuntimeException("此方法应分解到MVC,JSP等插件中。。。");
	}

	/**
	 * 移除所有的过滤器
	 */
	public static void removeAllFilter() {
		for (Bundle bundle : bundleFilterListMap.keySet()
				.toArray(new Bundle[0])) {
			removeBundleAllFilter(bundle);
		}
	}

	/**
	 * 移除某Bundle所有的过滤器
	 * 
	 * @param bundle
	 */
	public static void removeBundleAllFilter(Bundle bundle) {
		if (!bundleFilterListMap.containsKey(bundle))
			return;
		Filter[] bundleFilterArray = bundleFilterListMap.get(bundle).toArray(
				new Filter[0]);

		for (Filter filter : bundleFilterArray) {
			removeFilter(bundle, filter);
		}
		bundleFilterListMap.remove(bundle);
	}

	/**
	 * 移除过滤器
	 * 
	 * @param bundle
	 * @param filter
	 */
	public static void removeFilter(Bundle bundle, Filter filter) {

		// 从Bundle对应的过滤器列表中移除
		if (!bundleFilterListMap.containsKey(bundle))
			return;
		List<Filter> bundleFilterList = bundleFilterListMap.get(bundle);
		bundleFilterList.remove(filter);

		// 从所有的过滤器列表中移除
		filterList.remove(filter);
		// 销毁过滤器
		filter.destroy();
		log.debug(String.format("已成功移除插件[%s]的过滤器[%s]！",
				bundle.getSymbolicName(), filter));
	}

	/**
	 * 添加过滤器
	 * 
	 * @param bundle
	 * @param filter
	 */
	public static void addFilter(Bundle bundle, Filter filter) {
		if (filterConfig != null) {
			try {
				filter.init(filterConfig);
			} catch (ServletException e) {
				throw new RuntimeException(e);
			}
		}
		String filterClassName = filter.getClass().getName();
		// 是否存在同类名实例
		boolean hasSameClassNameObject = false;
		for (Filter preFilter : filterList) {
			if (preFilter.getClass().getName().equals(filterClassName)) {
				hasSameClassNameObject = true;
				break;
			}
		}
		// 如果存在同类名实例，则抛出异常
		if (hasSameClassNameObject) {

			StringBuilder sb = new StringBuilder();
			sb.append(String.format(
					"警告：将Bundle[%s]的过滤器[类名:%s]加入到FilterContext中时，发现存在多个同类名实例！",
					bundle.getSymbolicName(), filterClassName));
			sb.append("\n--同类名实例列表如下：");
			synchronized (bundleFilterListMap) {
				for (Bundle tmpBundle : bundleFilterListMap.keySet()) {
					List<Filter> tmpBundleFilterList = bundleFilterListMap
							.get(tmpBundle);
					for (Filter tmpFilter : tmpBundleFilterList) {
						if (tmpFilter.getClass().getName()
								.equals(filterClassName)) {
							sb.append(String.format(
									"\n  --Bundle[%s],过滤器[%s ,类名:%s]",
									tmpBundle.getSymbolicName(),
									tmpFilter.toString(), filterClassName));
						}
					}
				}
			}
			String errorMessage = sb.toString();
			log.warn(errorMessage);
		}

		// 加入到Bundle对应的过滤器列表中
		List<Filter> bundleFilterList = null;
		if (bundleFilterListMap.containsKey(bundle)) {
			bundleFilterList = bundleFilterListMap.get(bundle);
		} else {
			bundleFilterList = new ArrayList<Filter>();
			bundleFilterListMap.put(bundle, bundleFilterList);
		}
		bundleFilterList.add(filter);

		// 加入到全部过滤器列表中
		filterList.add(filter);

		// 过滤器的类
		Class<?> filterClass = filter.getClass();
		// 过滤器的FilterSetting实例
		FilterSetting filterSetting = filterClass
				.getAnnotation(FilterSetting.class);
		// 如果此过滤器的类上有FilterSetting注解，则全部过滤器根据FilterSetting注解的index的值进行排序
		if (filterSetting != null) {
			// 有属性的过滤器列表
			List<Filter> hasSettingFilterList = new ArrayList<Filter>();
			// 没有属性的过滤器列表
			List<Filter> noSettingFilterList = new ArrayList<Filter>();
			// 设置与过滤器的Map
			Map<FilterSetting, Filter> settingFilterMap = new HashMap<FilterSetting, Filter>();

			// 分离
			for (Filter tmpFilter : filterList) {
				Class<?> tmpFilterClass = tmpFilter.getClass();
				FilterSetting tmpFilterSetting = tmpFilterClass
						.getAnnotation(FilterSetting.class);
				// 如果没有设置
				if (tmpFilterSetting == null) {
					noSettingFilterList.add(tmpFilter);
				}// 否则有设置
				else {
					hasSettingFilterList.add(tmpFilter);
					settingFilterMap.put(tmpFilterSetting, tmpFilter);
				}
			}
			// 根据index排序
			FilterSetting[] filterSettings = settingFilterMap.keySet().toArray(
					new FilterSetting[0]);
			for (int j = 0; j < filterSettings.length; j++) {
				for (int i = 0; i < filterSettings.length; i++) {
					if (i == 0)
						continue;
					// 如果前面的index大于后面的index，则交换
					if (filterSettings[i - 1].index() > filterSettings[i]
							.index()) {
						FilterSetting tmpExchangeObject = filterSettings[i - 1];
						filterSettings[i - 1] = filterSettings[i];
						filterSettings[i] = tmpExchangeObject;
					}
				}
			}

			// 得到新的列表
			List<Filter> newFilterList = new ArrayList<Filter>();
			for (int i = 0; i < filterSettings.length; i++) {
				newFilterList.add(settingFilterMap.get(filterSettings[i]));
			}
			newFilterList.addAll(noSettingFilterList);
			filterList = newFilterList;
		}

		log.debug(String.format("已添加插件[%s]的过滤器[%s]！", bundle.getSymbolicName(),
				filter));
	}

	/**
	 * 得到所有监听器列表
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T extends EventListener> List<T> getListenerList(
			Class<T> clazz) {
		String listenerTypeName = getServletInterface(clazz).getName();
		if (!typeNameListenerListMap.containsKey(listenerTypeName))
			return null;
		return (List<T>) typeNameListenerListMap.get(listenerTypeName);
	}

	/**
	 * 得到所有监听器
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends EventListener> T[] getListeners(Class<T> clazz) {
		List<T> list = getListenerList(clazz);
		if (list == null)
			return null;
		return list.toArray((T[]) Array.newInstance(clazz, 0));
	}

	/**
	 * 移除所有监听器
	 */
	public static void removeAllListener() {
		for (Bundle bundle : bundleListenerListMap.keySet().toArray(
				new Bundle[0])) {
			removeBundleAllListener(bundle);
		}
	}

	/**
	 * 移除某Bundle的所有监听器
	 * 
	 * @param bundle
	 */
	public static void removeBundleAllListener(Bundle bundle) {
		if (!bundleListenerListMap.containsKey(bundle))
			return;
		EventListener[] bundleListenerArray = bundleListenerListMap.get(bundle)
				.toArray(new EventListener[0]);

		for (EventListener listener : bundleListenerArray) {
			removeListener(bundle, listener);
		}
		bundleListenerListMap.remove(bundle);
	}

	/**
	 * 移除监听器
	 * 
	 * @param listener
	 */
	public static void removeListener(Bundle bundle, EventListener listener) {

		// 从Bundle对应的监听器列表中移除
		if (!bundleListenerListMap.containsKey(bundle))
			return;
		List<? extends EventListener> bundleListenerList = bundleListenerListMap
				.get(bundle);
		bundleListenerList.remove(listener);

		List<Class<? extends EventListener>> listenerClassList = getServletInterfaceList(listener
				.getClass());
		for (Class<? extends EventListener> listenerClass : listenerClassList) {
			// 从所有监听器列表中移除
			List<? extends EventListener> listenerList = getListenerList(listenerClass);
			if (listenerList != null)
				listenerList.remove(listener);
			log.debug(String.format("已成功移除插件[%s]的[%s]类型监听器[%s]！",
					bundle.getSymbolicName(), listenerClass.getName(), listener));
		}
		// 从全部监听器对象列表中移除
		listenerList.remove(listener);
	}

	@SuppressWarnings("unchecked")
	private static List<Class<? extends EventListener>> getServletInterfaceList(
			Class<? extends EventListener> clazz) {
		List<Class<? extends EventListener>> rtnList = new ArrayList<Class<? extends EventListener>>();
		if (clazz.getName().startsWith("javax.servlet."))
			rtnList.add(clazz);

		Class<?>[] interfaceClassArray = clazz.getInterfaces();
		for (Class<?> interfaceClass : interfaceClassArray) {
			if (interfaceClass.equals(EventListener.class))
				continue;
			if (EventListener.class.isAssignableFrom(interfaceClass)) {
				rtnList.addAll(getServletInterfaceList((Class<? extends EventListener>) interfaceClass));
			}
		}
		return rtnList;
	}

	private static Class<? extends EventListener> getServletInterface(
			Class<? extends EventListener> clazz) {
		List<Class<? extends EventListener>> rtnList = getServletInterfaceList(clazz);
		if (rtnList == null || rtnList.isEmpty())
			return EventListener.class;
		else
			return rtnList.get(0);
	}

	/**
	 * 添加监听器
	 * 
	 * @param bundle
	 *            监听器所属的Bundle
	 * @param listener
	 *            监听器
	 */
	public static void addListener(Bundle bundle, EventListener listener) {

		String listenerClassName = listener.getClass().getName();
		// 是否存在同类名实例
		boolean hasSameClassNameObject = false;
		for (EventListener preListener : listenerList) {
			if (preListener.getClass().getName().equals(listenerClassName)) {
				hasSameClassNameObject = true;
				break;
			}
		}
		// 如果存在同类名实例，则抛出异常
		if (hasSameClassNameObject) {

			StringBuilder sb = new StringBuilder();
			sb.append(String.format(
					"警告：将Bundle[%s]的过滤器[类名:%s]加入到FilterContext中时，发现存在多个同类名实例！",
					bundle.getSymbolicName(), listenerClassName));
			sb.append("\n--同类名实例列表如下：");
			synchronized (bundleListenerListMap) {
				for (Bundle tmpBundle : bundleListenerListMap.keySet()) {
					List<EventListener> tmpBundleListenerList = bundleListenerListMap
							.get(tmpBundle);
					for (EventListener tmpListener : tmpBundleListenerList) {
						if (tmpListener.getClass().getName()
								.equals(listenerClassName)) {
							sb.append(String.format(
									"\n  --Bundle[%s],监听器[%s ,类名:%s]",
									tmpBundle.getSymbolicName(),
									tmpListener.toString(), listenerClassName));
						}
					}
				}
			}
			String errorMessage = sb.toString();
			log.warn(errorMessage);
		}

		// 加入到Bundle对应的监听器列表中
		List<EventListener> bundleListenerList = null;
		if (bundleListenerListMap.containsKey(bundle)) {
			bundleListenerList = bundleListenerListMap.get(bundle);
		} else {
			bundleListenerList = new ArrayList<EventListener>();
			bundleListenerListMap.put(bundle, bundleListenerList);
		}
		bundleListenerList.add(listener);

		List<Class<? extends EventListener>> listenerClassList = getServletInterfaceList(listener
				.getClass());
		for (Class<? extends EventListener> listenerClass : listenerClassList) {
			// 加入到所有监听器列表中
			String listenerTypeName = listenerClass.getName();
			List<EventListener> typeListenerList = null;
			if (typeNameListenerListMap.containsKey(listenerTypeName)) {
				typeListenerList = typeNameListenerListMap
						.get(listenerTypeName);
			} else {
				typeListenerList = new ArrayList<EventListener>();
				typeNameListenerListMap.put(listenerTypeName, typeListenerList);
			}
			typeListenerList.add(listener);
			log.debug(String.format("已添加插件[%s]的[%s]类型监听器[%s]！",
					bundle.getSymbolicName(), listenerClass.getName(), listener));
		}
		// 加入到全部监听器对象列表中
		listenerList.add(listener);
	}

	/**
	 * 根据别名(路径)找到Servlet
	 * 
	 * @param alias
	 * @return
	 */
	public static Servlet getServletByPath(String alias) {
		for (String path : pathServletMap.keySet()) {
			if (path.startsWith("*.") || path.endsWith("*")) {
				WildcardPattern pattern = pathWildcardPatternMap.get(path);
				if (pattern == null) {
					pattern = new WildcardPattern(path);
					pathWildcardPatternMap.put(path, pattern);
				}
				if (pattern.implies(alias)) {
					return pathServletMap.get(path);
				}
			} else {
				if (path.equals(alias)) {
					return pathServletMap.get(alias);
				}
			}
		}
		return null;
	}

	/**
	 * 获取所有Servlet所注册的路径数组
	 * 
	 * @return
	 */
	public static String[] getAllServletPaths() {
		return pathServletMap.keySet().toArray(new String[0]);
	}

	/**
	 * 注册Servlet
	 * 
	 * @param path
	 * @param servlet
	 * @param initparams
	 * @throws javax.servlet.ServletException
	 */
	public static void registerServlet(String path, Servlet servlet,
			Dictionary<String, Object> initparams)
			throws javax.servlet.ServletException {
		if (pathServletMap.containsKey(path)) {
			throw new RuntimeException(String.format(
					"路径[%s]已经被映射到了Servlet[%s]", path, pathServletMap.get(path)));
		}
		final Servlet servletMirror = servlet;
		final Dictionary<String, Object> initparamsMirror = initparams;
		// 初始化Servlet
		servlet.init(new ServletConfig() {

			@Override
			public String getInitParameter(String arg0) {
				if (initparamsMirror == null)
					return null;
				Object obj = initparamsMirror.get(arg0);
				if (obj == null)
					return null;
				return obj.toString();
			}

			@Override
			public Enumeration<String> getInitParameterNames() {
				if (initparamsMirror == null)
					return null;
				return initparamsMirror.keys();
			}

			@Override
			public ServletContext getServletContext() {
				return WebContext.getServletContext();
			}

			@Override
			public String getServletName() {
				return servletMirror.toString();
			}
		});
		pathServletMap.put(path, servlet);
		log.debug(String.format("已注册路径[%s]到Servlet[%s].", path, servlet));
	}

	/**
	 * 注册资源
	 * 
	 * @param alias
	 * @param name
	 */
	public static void registerResources(String alias, String name) {
		throw new RuntimeException("此方法还未实现！");
	}

	/**
	 * 取消注册Servlet或资源
	 * 
	 * @param path
	 */
	public static void unregister(String path) {
		if (!pathServletMap.containsKey(path))
			return;
		Servlet servlet = pathServletMap.get(path);
		pathServletMap.remove(path);
		log.debug(String.format("已取消注册路径为[%s]的Servlet[%s].", path, servlet));
	}
}
