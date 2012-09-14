package com.quickwebframework.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.quickwebframework.entity.HandlerExceptionResolver;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.service.MvcFrameworkService;
import com.quickwebframework.service.ViewRenderService;
import com.quickwebframework.service.WebAppService;

public abstract class FrameworkContext {
	private static Log log = LogFactory.getLog(FrameworkContext.class);
	// 核心Bundle
	private static Bundle coreBundle;
	// 插件名称插件Map
	private static Map<String, Bundle> bundleNameBundleMap = new HashMap<String, Bundle>();

	// MVC框架服务
	public static MvcFrameworkService mvcFrameworkService;
	// 视图渲染服务
	public static ViewRenderService viewRenderService;

	// 根URL处理Servlet
	private static HttpServlet rootUrlHandleServlet;

	public static HttpServlet getRootUrlHandleServlet() {
		return rootUrlHandleServlet;
	}

	public static void setRootUrlHandleServlet(HttpServlet rootUrlHandleServlet) {
		FrameworkContext.rootUrlHandleServlet = rootUrlHandleServlet;
	}

	// URL未找到处理Servlet
	private static HttpServlet urlNotFoundHandleServlet;

	public static HttpServlet getUrlNotFoundHandleServlet() {
		return urlNotFoundHandleServlet;
	}

	public static void setUrlNotFoundHandleServlet(
			HttpServlet urlNotFoundHandleServlet) {
		FrameworkContext.urlNotFoundHandleServlet = urlNotFoundHandleServlet;
	}

	// 得到处理器异常解决器
	private static HandlerExceptionResolver handlerExceptionResolver;

	public static HandlerExceptionResolver getHandlerExceptionResolver() {
		return handlerExceptionResolver;
	}

	public static void setHandlerExceptionResolver(
			HandlerExceptionResolver handlerExceptionResolver) {
		FrameworkContext.handlerExceptionResolver = handlerExceptionResolver;
	}

	// 插件扩展信息MAP
	private static Map<Bundle, BundleExtInfo> bundleExtInfoMap = new HashMap<Bundle, BundleExtInfo>();;
	// 所有过滤器的列表
	private static List<Filter> filterList = new ArrayList<Filter>();

	public static List<Filter> getFilterList() {
		return filterList;
	}

	public static void init(Bundle coreBundle) {
		FrameworkContext.coreBundle = coreBundle;

		// 添加OSGi插件监听器，当某插件停止时，停止此插件的线程，当com.quickwebframework.bundle插件停止时，停止所有线程
		coreBundle.getBundleContext().addBundleListener(new BundleListener() {

			@Override
			public void bundleChanged(BundleEvent arg0) {
				int eventType = arg0.getType();
				Bundle bundle = arg0.getBundle();
				String bundleName = bundle.getSymbolicName();

				// 如果插件已经启动
				if (eventType == BundleEvent.STARTED) {
					bundleNameBundleMap.put(bundleName, bundle);
				}
				// 如果插件的状态是正在停止或已经停止
				else if (eventType == BundleEvent.STOPPED
						|| eventType == BundleEvent.STOPPING) {
					if (bundleNameBundleMap.containsKey(bundleName)) {
						bundleNameBundleMap.remove(bundleName);
					}
					whenBundleStoped(bundle);
				}
			}
		});
	}

	// 根据插件名称得到Bundle
	public static Bundle getBundleByName(String bundleName) {
		if (bundleNameBundleMap.containsKey(bundleName)) {
			return bundleNameBundleMap.get(bundleName);
		}
		return null;
	}

	/**
	 * 注册WEB应用
	 * 
	 * @param bundleContext
	 */
	public static void registerWebApp(BundleActivator bundleActivator,
			BundleContext bundleContext) {

		if (FrameworkContext.mvcFrameworkService == null) {
			throw new RuntimeException(
					"注册WebApp时，未发现有注册的MvcFrameworkService服务！");
		}

		// 注册服务
		final Bundle currentBundle = bundleContext.getBundle();
		final ClassLoader currentClassLoader = bundleActivator.getClass()
				.getClassLoader();

		FrameworkContext.mvcFrameworkService.addWebApp(new WebAppService() {
			@Override
			public Bundle getBundle() {
				return currentBundle;
			}

			@Override
			public ClassLoader getClassLoader() {
				return currentClassLoader;
			}
		});
	}

	// 插件扩展信息类
	private static class BundleExtInfo {
		private List<Filter> fileterList;
		private List<Thread> threadList;

		public BundleExtInfo() {
			fileterList = new ArrayList<Filter>();
			threadList = new ArrayList<Thread>();
		}
	}

	private static BundleExtInfo getOrCreateBundleExtInfo(Bundle bundle) {
		BundleExtInfo bundleExtInfo = null;
		if (bundleExtInfoMap.containsKey(bundle)) {
			bundleExtInfo = bundleExtInfoMap.get(bundle);
		} else {
			bundleExtInfo = new BundleExtInfo();
			bundleExtInfoMap.put(bundle, bundleExtInfo);
		}
		return bundleExtInfo;
	}

	public static void addFilter(Bundle bundle, Filter filter) {
		BundleExtInfo bundleExtInfo = getOrCreateBundleExtInfo(bundle);
		bundleExtInfo.fileterList.add(filter);
		// 加入到过滤器列表中
		filterList.add(filter);
	}

	public static void addThread(Bundle bundle, Thread thread) {
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

	// 当插件停止时
	public static void whenBundleStoped(Bundle bundle) {

		// 如果停止的是框架插件
		if (bundle.equals(coreBundle)) {
			for (Bundle key : bundleExtInfoMap.keySet()) {
				whenBundleStoped(key);
			}
		} else {
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
	}
}
