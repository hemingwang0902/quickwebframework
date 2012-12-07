package com.quickwebframework.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.stereotype.FilterSetting;

public class FilterContext {

	private static Log log = LogFactory.getLog(FilterContext.class);

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
						removeBundleAllFilter(bundle);
					}
				}
			}
		});
	}

	private static List<Filter> filterList = new ArrayList<Filter>();
	private static Map<Bundle, List<Filter>> bundleFilterListMap = new HashMap<Bundle, List<Filter>>();

	/**
	 * 得到过滤器列表
	 * 
	 * @return
	 */
	public static List<Filter> getFilterList() {
		return filterList;
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
}
