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
		log.info(String.format("已成功移除插件[%s]的过滤器[%s]！",
				bundle.getSymbolicName(), filter));
	}

	/**
	 * 添加过滤器
	 * 
	 * @param bundle
	 * @param filter
	 */
	public static void addFilter(Bundle bundle, Filter filter) {
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
		log.info(String.format("已添加插件[%s]的过滤器[%s]！", bundle.getSymbolicName(),
				filter));
	}
}
