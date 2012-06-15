package com.quickwebframework.service.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.service.core.PluginService;

public class PluginServiceImpl implements PluginService {
	private static Log log = LogFactory.getLog(PluginServiceImpl.class);

	private BundleContext bundleContext;
	private Map<Bundle, BundleExtInfo> bundleExtInfoMap;

	// 所有过滤器的列表
	private List<Filter> filterList;

	@Override
	public List<Filter> getFilterList() {
		return filterList;
	}

	// 插件扩展信息类
	private class BundleExtInfo {
		private List<Filter> fileterList;
		private List<Thread> threadList;

		public BundleExtInfo() {
			fileterList = new ArrayList<Filter>();
			threadList = new ArrayList<Thread>();
		}
	}

	public PluginServiceImpl(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		bundleExtInfoMap = new HashMap<Bundle, BundleExtInfo>();
		filterList = new ArrayList<Filter>();

		this.bundleContext.addBundleListener(new BundleListener() {

			@Override
			public void bundleChanged(BundleEvent arg0) {
				int eventType = arg0.getType();
				// 如果插件的状态不是正在停止，则返回
				if (!(eventType == BundleEvent.STOPPED || eventType == BundleEvent.STOPPING))
					return;
				Bundle bundle = arg0.getBundle();
				String bundleName = bundle.getSymbolicName();
				// 如果bundleExtInfoMap中没有这个Bundle，则返回
				if (!bundleExtInfoMap.containsKey(bundle))
					return;

				BundleExtInfo bundleExtInfo = getOrCreateBundleExtInfo(bundle);

				// 停止此插件的线程
				for (Thread thread : bundleExtInfo.threadList) {
					String threadName = String.format(
							"[Thread Id:%s ,Name:%s ,Class:%s ,Hashcode:%s]",
							thread.getId(), thread.getName(), thread.getClass()
									.getName(), Integer.toHexString(thread
									.hashCode()));
					try {
						thread.interrupt();
						log.info(String.format("已成功向插件[%s]的线程[%s]发送中断命令！",
								bundleName, threadName));
					} catch (Exception ex) {
						log.error(String.format("向插件[%s]的线程[%s]发送中断命令失败！",
								bundleName, threadName));
						ex.printStackTrace();
					}
				}
				// 移除此插件的过滤器
				for (Filter filter : bundleExtInfo.fileterList) {
					filterList.remove(filter);
					log.info(String.format("已成功移除插件[%s]的过滤器[%s]！", bundleName,
							filter));
				}
				// 从bundleExtInfoMap中移除此Bundle
				bundleExtInfoMap.remove(bundle);
			}
		});
	}

	private BundleExtInfo getOrCreateBundleExtInfo(Bundle bundle) {
		BundleExtInfo bundleExtInfo = null;
		if (bundleExtInfoMap.containsKey(bundle)) {
			bundleExtInfo = bundleExtInfoMap.get(bundle);
		} else {
			bundleExtInfo = new BundleExtInfo();
			bundleExtInfoMap.put(bundle, bundleExtInfo);
		}
		return bundleExtInfo;
	}

	@Override
	public void addFilter(Bundle bundle, Filter filter) {
		BundleExtInfo bundleExtInfo = getOrCreateBundleExtInfo(bundle);
		bundleExtInfo.fileterList.add(filter);
		// 加入到过滤器列表中
		filterList.add(filter);
	}

	@Override
	public void addThread(Bundle bundle, Thread thread) {
		BundleExtInfo bundleExtInfo = getOrCreateBundleExtInfo(bundle);
		bundleExtInfo.threadList.add(thread);

		// 启动线程
		try {
			thread.start();
			log.info(String.format("已成功启动插件[%s]的线程[%s]！",
					bundle.getSymbolicName(), thread));
		} catch (Exception ex) {
			log.error(String.format("启动插件[%s]的线程[%s]失败！",
					bundle.getSymbolicName(), thread));
		}
	}
}
