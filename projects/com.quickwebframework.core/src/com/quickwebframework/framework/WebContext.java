package com.quickwebframework.framework;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.quickwebframework.bridge.HttpServletBridge;
import com.quickwebframework.bridge.ServletFilterBridge;
import com.quickwebframework.bridge.ServletListenerBridge;
import com.quickwebframework.core.Activator;
import com.quickwebframework.entity.HandlerExceptionResolver;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.service.MvcFrameworkService;
import com.quickwebframework.service.ViewRenderService;
import com.quickwebframework.stereotype.FilterSetting;

public class WebContext extends FrameworkContext {
	private static WebContext instance;

	public static WebContext getInstance() {
		if (instance == null)
			instance = new WebContext();
		return instance;
	}

	private static Log log = LogFactory.getLog(WebContext.class);

	// QuickwebFramework的过滤器配置状态
	// ===== 常量开始
	public static final String QUICKWEBFRAMEWORK_STATE_FILTERCONFIG = "com.quickwebframework.state.FILTERCONFIG";
	public static final String BUNDLE_METHOD_URL_TEMPLATE = "com.quickwebframework.util.BUNDLE_METHOD_URL_TEMPLATE";
	// ===== 常量结束

	// ===== WEB相关变量部分开始
	private BundleListener bundleListener;

	// 插件方法URL模板
	public String bundleMethodUrlTemplate;
	// MVC框架服务
	private MvcFrameworkService mvcFrameworkService;
	// 视图渲染服务
	private ViewRenderService viewRenderService;
	// WEB项目的ServletContext
	private ServletContext servletContext;
	// 根URL处理Servlet
	private HttpServlet rootUrlHandleServlet;
	// URL未找到处理Servlet
	private HttpServlet urlNotFoundHandleServlet;
	// 得到处理器异常解决器
	private HandlerExceptionResolver handlerExceptionResolver;

	public MvcFrameworkService getMvcFrameworkService() {
		return mvcFrameworkService;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public ViewRenderService getViewRenderService() {
		return viewRenderService;
	}

	public HttpServlet getRootUrlHandleServlet() {
		return rootUrlHandleServlet;
	}

	public void setRootUrlHandleServlet(HttpServlet rootUrlHandleServlet) {
		this.rootUrlHandleServlet = rootUrlHandleServlet;
	}

	public HttpServlet getUrlNotFoundHandleServlet() {
		return urlNotFoundHandleServlet;
	}

	public void setUrlNotFoundHandleServlet(HttpServlet urlNotFoundHandleServlet) {
		this.urlNotFoundHandleServlet = urlNotFoundHandleServlet;
	}

	public HandlerExceptionResolver getHandlerExceptionResolver() {
		return handlerExceptionResolver;
	}

	public void setHandlerExceptionResolver(
			HandlerExceptionResolver handlerExceptionResolver) {
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	// ===== WEB相关变量部分结束

	// ===== 过滤器变量部分开始
	// 从上层传递下来的过滤器配置
	private FilterConfig filterConfig;
	private List<Filter> filterList;
	private Map<Bundle, List<Filter>> bundleFilterListMap;

	/**
	 * 得到过滤器配置
	 * 
	 * @return
	 */
	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	/**
	 * 设置过滤器配置
	 * 
	 * @param filterConfig
	 */
	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
		if (filterConfig == null)
			return;
		for (Filter filter : getFilterList()) {
			try {
				filter.init(filterConfig);
			} catch (ServletException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 得到过滤器列表
	 * 
	 * @return
	 */
	public List<Filter> getFilterList() {
		return filterList;
	}

	// ===== 过滤器变量部分结束

	// ===== 监听器变量部分开始
	private List<EventListener> listenerList;
	private Map<String, List<EventListener>> typeNameListenerListMap;
	private Map<Bundle, List<EventListener>> bundleListenerListMap;

	// ===== 监听器变量部分结束

	public WebContext() {
		filterList = new ArrayList<Filter>();
		bundleFilterListMap = new HashMap<Bundle, List<Filter>>();
		listenerList = new ArrayList<EventListener>();
		typeNameListenerListMap = new HashMap<String, List<EventListener>>();
		bundleListenerListMap = new HashMap<Bundle, List<EventListener>>();
	}

	@Override
	public void init() {
		super.addSimpleServiceFieldLink(ServletContext.class.getName(),
				"servletContext");
		super.addSimpleServiceFieldLink(ViewRenderService.class.getName(),
				"viewRenderService");
		super.addSimpleServiceFieldLink(MvcFrameworkService.class.getName(),
				"mvcFrameworkService");

		final BundleContext bundleContext = Activator.getContext();

		// 设置插件方法URL模板
		ServletContext servletContext = getServletContext();
		Object tmpObj = servletContext.getAttribute(BUNDLE_METHOD_URL_TEMPLATE);
		if (tmpObj != null) {
			bundleMethodUrlTemplate = tmpObj.toString();
		}

		bundleListener = new BundleListener() {
			@Override
			public void bundleChanged(BundleEvent arg0) {
				Bundle bundle = arg0.getBundle();
				int bundleEventType = arg0.getType();

				Bundle coreBundle = Activator.getContext().getBundle();

				// 如果插件的状态是正在停止
				if (bundleEventType == BundleEvent.STOPPING) {
					// 移除插件的控制器
					if (mvcFrameworkService == null)
						return;
					mvcFrameworkService.removeBundle(bundle);
					// 移除插件的过滤器
					if (bundle.equals(coreBundle)) {
						removeAllFilter();
					} else {
						removeBundleAllFilter(bundle);
					}
					// 移除插件的监听器
					if (bundle.equals(coreBundle)) {
						removeAllListener();
					} else {
						removeBundleAllListener(bundle);
					}
				}
			}
		};
		bundleContext.addBundleListener(bundleListener);

		// 启动时，从ServletContext中读取相关运行时状态
		Object filterConfigObject = getServletContext().getAttribute(
				QUICKWEBFRAMEWORK_STATE_FILTERCONFIG);
		if (filterConfigObject != null)
			setFilterConfig((FilterConfig) filterConfigObject);

		// 注册HttpServlet桥接对象
		bundleContext.registerService(HttpServletBridge.class.getName(),
				new HttpServletBridge(), null);
		// 注册过滤器桥接对象
		bundleContext.registerService(ServletFilterBridge.class.getName(),
				new ServletFilterBridge(), null);
		// 注册监听器桥接对象
		bundleContext.registerService(ServletListenerBridge.class.getName(),
				new ServletListenerBridge(), null);
	}

	@Override
	public void destory() {
		if (bundleListener != null)
			Activator.getContext().removeBundleListener(bundleListener);
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
	public String getBundleMethodUrl(String bundleName, String methodName) {
		if (bundleMethodUrlTemplate == null
				|| bundleMethodUrlTemplate.isEmpty())
			return "Missing bundleMethodUrlTemplate";
		return String.format(bundleMethodUrlTemplate, bundleName, methodName);
	}

	/**
	 * 注册WEB应用
	 * 
	 * @param bundleContext
	 */
	public void addBundle(Bundle bundle) {
		if (mvcFrameworkService == null) {
			log.error("注册WebApp时，未发现有注册的MvcFrameworkService服务！");
			throw new RuntimeException(
					"注册WebApp时，未发现有注册的MvcFrameworkService服务！");
		}
		// 注册服务
		mvcFrameworkService.addBundle(bundle);
	}

	/**
	 * 移除所有的过滤器
	 */
	public void removeAllFilter() {
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
	public void removeBundleAllFilter(Bundle bundle) {
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
	public void removeFilter(Bundle bundle, Filter filter) {

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
	public void addFilter(Bundle bundle, Filter filter) {
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
	public <T extends EventListener> List<T> getListenerList(Class<T> clazz) {
		String listenerTypeName = getServletInterface(clazz).getName();
		if (!typeNameListenerListMap.containsKey(listenerTypeName))
			return null;
		return (List<T>) typeNameListenerListMap.get(listenerTypeName);
	}

	/**
	 * 移除所有监听器
	 */
	public void removeAllListener() {
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
	public void removeBundleAllListener(Bundle bundle) {
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
	public void removeListener(Bundle bundle, EventListener listener) {

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
	private List<Class<? extends EventListener>> getServletInterfaceList(
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

	private Class<? extends EventListener> getServletInterface(
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
	public void addListener(Bundle bundle, EventListener listener) {

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
}
