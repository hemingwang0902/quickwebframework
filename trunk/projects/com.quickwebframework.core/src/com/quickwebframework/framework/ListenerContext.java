package com.quickwebframework.framework;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;

public class ListenerContext {
	private static Log log = LogFactory.getLog(ListenerContext.class);

	public static void init() {
		final Bundle coreBundle = FrameworkContext.coreBundle;

		coreBundle.getBundleContext().addBundleListener(new BundleListener() {
			@Override
			public void bundleChanged(BundleEvent arg0) {
				int eventType = arg0.getType();
				Bundle bundle = arg0.getBundle();

				// 如果插件的状态是正在停止或已经停止
				if (eventType == BundleEvent.STOPPED
						|| eventType == BundleEvent.STOPPING) {
					if (bundle.equals(coreBundle)) {
						removeAllFilter();
					} else {
						removeBundleAllListener(bundle);
					}
				}
			}
		});
	}

	private static List<EventListener> listenerList = new ArrayList<EventListener>();
	private static Map<String, List<EventListener>> typeNameListenerListMap = new HashMap<String, List<EventListener>>();
	private static Map<Bundle, List<EventListener>> bundleListenerListMap = new HashMap<Bundle, List<EventListener>>();

	/**
	 * 得到所有监听器列表
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends EventListener> List<T> getListenerList(
			Class<T> clazz) {
		String listenerTypeName = getServletInterface(clazz).getName();
		if (!typeNameListenerListMap.containsKey(listenerTypeName))
			return null;
		return (List<T>) typeNameListenerListMap.get(listenerTypeName);
	}

	/**
	 * 移除所有监听器
	 */
	public static void removeAllFilter() {
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
}
