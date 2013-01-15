package com.quickwebframework.mvc.spring.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.quickwebframework.ioc.IocContext;
import com.quickwebframework.ioc.spring.util.BundleApplicationContextUtils;
import com.quickwebframework.mvc.HttpMethodInfo;
import com.quickwebframework.mvc.MvcFrameworkService;
import com.quickwebframework.mvc.MvcModelAndView;
import com.quickwebframework.mvc.spring.core.BundleHandler;
import com.quickwebframework.mvc.spring.entity.impl.PluginControllerInfo;

public class SpringMvcFrameworkService implements MvcFrameworkService {

	private static Log log = LogFactory.getLog(SpringMvcFrameworkService.class);

	private PathMatcher pathMatcher = new AntPathMatcher();
	// 插件与ApplicationContext的映射Map
	public Map<Bundle, ApplicationContext> bundleApplicationContextMap = new HashMap<Bundle, ApplicationContext>();
	// 插件名与ControllerService对应Map
	private Map<String, PluginControllerInfo> bundleNamePluginControllerInfoMap = new HashMap<String, PluginControllerInfo>();
	// Bundle处理器列表
	private List<BundleHandler> bundleHandlerList = new ArrayList<BundleHandler>();

	/**
	 * 注册插件处理器
	 * 
	 * @param bundleHandler
	 */
	public void registerBundleHandler(BundleHandler bundleHandler) {
		bundleHandlerList.add(bundleHandler);
	}

	/**
	 * 取消注册插件处理器
	 * 
	 * @param bundleHandler
	 */
	public void unregisterBundleHandler(BundleHandler bundleHandler) {
		bundleHandlerList.remove(bundleHandler);
	}

	// 添加插件与控制器信息
	public void addBundleControllerInfo(String bundleName,
			PluginControllerInfo pluginControllerInfo) {
		bundleNamePluginControllerInfoMap.put(bundleName, pluginControllerInfo);
	}

	// 移除插件与控制器信息
	public void removeBundleControllerInfo(String bundleName) {
		bundleNamePluginControllerInfoMap.remove(bundleName);
	}

	@Override
	public boolean addBundle(Bundle bundle) {
		// 如果IoC框架中还没有此Bundle,则添加到IoC框架中
		if (!IocContext.containsBundle(bundle))
			IocContext.addBundle(bundle);
		ApplicationContext applicationContext = BundleApplicationContextUtils
				.getBundleApplicationContext(bundle);
		if (applicationContext == null) {
			throw new RuntimeException(String.format(
					"找不到此Bundle[%s]对应的ApplicationContext对象！",
					bundle.getSymbolicName()));
		}
		bundleApplicationContextMap.put(bundle, applicationContext);
		for (BundleHandler bundleHandler : bundleHandlerList) {
			bundleHandler.registerBundle(bundle, applicationContext);
		}
		return true;
	}

	@Override
	public boolean removeBundle(Bundle bundle) {
		if (!bundleApplicationContextMap.containsKey(bundle)) {
			return false;
		}
		ApplicationContext applicationContext = bundleApplicationContextMap
				.get(bundle);
		for (BundleHandler bundleHandler : bundleHandlerList) {
			bundleHandler.unregisterBundle(bundle, applicationContext);
		}

		bundleApplicationContextMap.remove(bundle);
		return true;
	}

	@Override
	public boolean containsBundle(Bundle bundle) {
		return bundleApplicationContextMap.containsKey(bundle);
	}

	@Override
	public Map<String, List<HttpMethodInfo>> getBundleHttpMethodInfoListMap() {
		Map<String, List<HttpMethodInfo>> rtnMap = new HashMap<String, List<HttpMethodInfo>>();
		for (String key : bundleNamePluginControllerInfoMap.keySet()) {
			PluginControllerInfo pluginControllerInfo = bundleNamePluginControllerInfoMap
					.get(key);
			rtnMap.put(key, pluginControllerInfo.getHttpMethodInfoList());
		}
		return rtnMap;
	}

	@Override
	public MvcModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, String bundleName, String methodName) {
		// 如果插件名称为null或Map中不存在此插件名称
		if (bundleName == null
				|| !bundleNamePluginControllerInfoMap.containsKey(bundleName)) {
			try {
				response.sendError(404, "名称为[" + bundleName + "]的插件不存在！");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		PluginControllerInfo pluginControllerInfo = bundleNamePluginControllerInfoMap
				.get(bundleName);

		// 请求的HTTP方法
		String requestMethod = request.getMethod().toUpperCase();

		// URL的模板:[requestMethod]_/[bundleName]/[methodName]
		String mappingUrl = requestMethod + "_" + "/" + bundleName + "/"
				+ methodName;

		// 如果方法名称为null或Map中不存在此方法名称
		if (methodName == null) {
			try {
				response.sendError(404, "未找到方法名称！");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		// 正则匹配得到处理器对象
		Object handler = null;
		for (String urlTemplate : pluginControllerInfo
				.getMappingUrlHandlerMap().keySet()) {
			if (pathMatcher.match(urlTemplate, mappingUrl)) {
				handler = pluginControllerInfo.getMappingUrlHandlerMap().get(
						urlTemplate);
				break;
			}
		}

		if (handler == null) {
			try {
				response.sendError(404, "未找到插件名称为[" + bundleName + "]，方法名称为["
						+ methodName + "]的处理器！");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		// 得到该处理器对应的适配器
		AnnotationMethodHandlerAdapter adapter = pluginControllerInfo
				.getHandlerAdapterMap().get(handler);

		try {
			// 执行处理，得到模型与视图
			ModelAndView mav = adapter.handle(request, response, handler);
			if (mav == null) {
				return null;
			}
			MvcModelAndView mmav = new MvcModelAndView(mav.getViewName(),
					mav.getModel(), pluginControllerInfo.getBundle());
			return mmav;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
