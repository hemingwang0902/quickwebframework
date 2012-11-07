package com.quickwebframework.mvc.spring.service.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.entity.MvcModelAndView;
import com.quickwebframework.ioc.spring.util.BundleApplicationContextUtils;
import com.quickwebframework.mvc.spring.entity.impl.PluginControllerInfo;
import com.quickwebframework.mvc.spring.util.PluginPathMatcher;
import com.quickwebframework.mvc.spring.util.PluginUrlPathHelper;
import com.quickwebframework.service.MvcFrameworkService;
import com.quickwebframework.util.BundleUtil;

public class SpringMvcFrameworkService implements MvcFrameworkService {

	private static Log log = LogFactory.getLog(SpringMvcFrameworkService.class);
	private PathMatcher pathMatcher = new AntPathMatcher();
	public static Map<Bundle, ApplicationContext> bundleApplicationContextMap = new HashMap<Bundle, ApplicationContext>();

	// 插件名与ControllerService对应Map
	private Map<String, PluginControllerInfo> bundleNamePluginControllerInfoMap = new HashMap<String, PluginControllerInfo>();

	private ApplicationContext initPluginControllerInfo(Bundle bundle,
			PluginControllerInfo pluginControllerInfo) {

		ApplicationContext applicationContext = BundleApplicationContextUtils
				.getBundleApplicationContext(bundle);

		if (applicationContext == null) {
			throw new RuntimeException("找不到此Bundle对应的ApplicationContext对象！");
		}

		// 从ApplicationContext得到MVC控制器列表
		final Map<String, Object> handlerMap = applicationContext
				.getBeansWithAnnotation(Controller.class);

		Collection<Object> handlers = handlerMap.values();

		if (handlers != null) {
			for (Object handler : handlers) {
				Class<?> controllerClazz = handler.getClass();
				Method[] methods = controllerClazz.getMethods();
				for (Method method : methods) {
					// 查找RequestMapping注解
					RequestMapping requestMapping = method
							.getAnnotation(RequestMapping.class);
					if (requestMapping == null)
						continue;

					for (String mappingUrl : requestMapping.value()) {
						String bundleName = bundle.getSymbolicName();
						String methodName = mappingUrl;

						pluginControllerInfo.getUrlList().add(
								BundleUtil.getBundleMethodUrl(bundleName,
										methodName));

						if (!mappingUrl.startsWith("/")) {
							mappingUrl = "/" + mappingUrl;
						}
						mappingUrl = "/" + bundleName + mappingUrl;
						pluginControllerInfo.getMappingUrlHandlerMap().put(
								mappingUrl, handler);

						StringBuilder sb = new StringBuilder();
						RequestMethod[] requestMethods = requestMapping
								.method();
						if (requestMethods != null) {
							for (RequestMethod requestMethod : requestMethods) {
								sb.append(requestMethod.name());
								sb.append(",");
							}
						}
						if (sb.length() == 0)
							sb.append("所有");
						else
							sb.setLength(sb.length() - 1);

						log.info(String.format(
								"映射内部URL路径[%s]的[%s]HTTP请求到处理器'%s'", mappingUrl,
								sb.toString(), handler.getClass().getName()));

						// 将处理器与对应的适配器放入映射中
						if (!pluginControllerInfo.getHandlerAdapterMap()
								.containsKey(handler)) {
							AnnotationMethodHandlerAdapter adapter = new AnnotationMethodHandlerAdapter();
							adapter.setPathMatcher(new PluginPathMatcher(bundle
									.getSymbolicName()));
							adapter.setUrlPathHelper(new PluginUrlPathHelper());
							pluginControllerInfo.getHandlerAdapterMap().put(
									handler, adapter);
						}
					}
				}
			}
		}
		return applicationContext;
	}

	@Override
	public boolean addBundle(Bundle bundle) {
		String bundleName = bundle.getSymbolicName();

		PluginControllerInfo pluginControllerInfo = new PluginControllerInfo(
				bundle);
		ApplicationContext applicationContext = initPluginControllerInfo(
				bundle, pluginControllerInfo);
		bundleApplicationContextMap.put(bundle, applicationContext);
		bundleNamePluginControllerInfoMap.put(bundleName, pluginControllerInfo);
		log.info("插件[" + bundleName + "]已注册为Spring MVC的Web App.");
		return true;
	}

	@Override
	public boolean removeBundle(Bundle bundle) {
		String bundleName = bundle.getSymbolicName();
		bundleNamePluginControllerInfoMap.remove(bundleName);
		if (bundleApplicationContextMap.containsKey(bundle))
			bundleApplicationContextMap.remove(bundle);
		log.info("插件[" + bundleName + "]注册在Spring MVC的Web App已经移除！");
		return true;
	}

	@Override
	public List<String> getAllUrlList() {
		List<String> list = new ArrayList<String>();

		Map<String, List<String>> map = getBundleNameUrlListMap();
		for (String key : map.keySet()) {
			list.addAll(map.get(key));
		}
		return list;
	}

	@Override
	public Map<String, List<String>> getBundleNameUrlListMap() {
		Map<String, List<String>> rtnMap = new HashMap<String, List<String>>();
		for (String key : bundleNamePluginControllerInfoMap.keySet()) {
			PluginControllerInfo pluginControllerInfo = bundleNamePluginControllerInfoMap
					.get(key);
			rtnMap.put(key, pluginControllerInfo.getUrlList());
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

		String mappingUrl = "/" + bundleName + "/" + methodName;

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
