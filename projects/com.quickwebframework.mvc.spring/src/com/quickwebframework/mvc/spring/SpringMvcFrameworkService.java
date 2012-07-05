package com.quickwebframework.mvc.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.quickwebframework.core.FrameworkContext;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.entity.MvcModelAndView;
import com.quickwebframework.mvc.spring.entity.impl.PluginControllerInfo;
import com.quickwebframework.mvc.spring.util.BundleAnnotationConfigApplicationContext;
import com.quickwebframework.mvc.spring.util.PluginPathMatcher;
import com.quickwebframework.mvc.spring.util.PluginUrlPathHelper;
import com.quickwebframework.service.MvcFrameworkService;
import com.quickwebframework.service.WebAppService;

public class SpringMvcFrameworkService implements MvcFrameworkService {

	private static Log log = LogFactory.getLog(SpringMvcFrameworkService.class);
	private PathMatcher pathMatcher = new AntPathMatcher();

	// 插件名与ControllerService对应Map
	private Map<String, PluginControllerInfo> bundleNamePluginControllerInfoMap = new HashMap<String, PluginControllerInfo>();

	private boolean initPluginControllerInfo(Bundle bundle,
			PluginControllerInfo pluginControllerInfo) {
		// 初始化AnnotationConfigApplicationContext
		BundleAnnotationConfigApplicationContext applicationContext = new BundleAnnotationConfigApplicationContext(
				bundle);

		try {
			// 开始Spring扫描
			ClassLoader bundleClassLoader = pluginControllerInfo
					.getWebAppService().getClassLoader();
			applicationContext.setClassLoader(bundleClassLoader);
			applicationContext.scan("*");
			applicationContext.refresh();
			applicationContext.start();
		} catch (Exception ex) {
			log.error("用Spring扫描插件时出错异常，插件启动失败！", ex);
			// 停止插件
			try {
				bundle.stop();
			} catch (Exception ex2) {
			}
			return false;
		}

		// 从ApplicationContext得到过滤器列表
		Map<String, Filter> filterMap = applicationContext
				.getBeansOfType(Filter.class);
		for (Filter filter : filterMap.values()) {
			FrameworkContext.addFilter(bundle, filter);
		}

		// 从ApplicationContext得到线程列表
		Map<String, Thread> threadMap = applicationContext
				.getBeansOfType(Thread.class);
		for (Thread thread : threadMap.values()) {
			FrameworkContext.addThread(bundle, thread);
		}

		// 从ApplicationContext得到处理器列表
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
						String methodName = mappingUrl;
						pluginControllerInfo.getMethodNameList()
								.add(methodName);

						if (!mappingUrl.startsWith("/")) {
							mappingUrl = "/" + mappingUrl;
						}
						mappingUrl = "/" + bundle.getSymbolicName()
								+ mappingUrl;
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
		return true;
	}

	@Override
	public boolean addWebApp(WebAppService webAppService) {
		Bundle bundle = webAppService.getBundle();
		PluginControllerInfo pluginControllerInfo = new PluginControllerInfo(
				webAppService);
		initPluginControllerInfo(bundle, pluginControllerInfo);
		bundleNamePluginControllerInfoMap.put(bundle.getSymbolicName(),
				pluginControllerInfo);
		return true;
	}

	@Override
	public boolean removeWebApp(WebAppService webAppService) {
		bundleNamePluginControllerInfoMap.remove(webAppService.getBundle()
				.getSymbolicName());
		return true;
	}

	@Override
	public WebAppService getWebAppService(String bundleName) {
		PluginControllerInfo pluginControllerInfo = bundleNamePluginControllerInfoMap
				.get(bundleName);
		if (pluginControllerInfo == null)
			return null;
		return pluginControllerInfo.getWebAppService();
	}

	@Override
	public List<String> getAllUrlList() {
		List<String> list = new ArrayList<String>();

		Map<String, List<String>> map = getBundleNameMethodNameListMap();
		for (String key : map.keySet()) {
			list.addAll(map.get(key));
		}
		return list;
	}

	@Override
	public Map<String, List<String>> getBundleNameMethodNameListMap() {
		Map<String, List<String>> rtnMap = new HashMap<String, List<String>>();
		for (String key : bundleNamePluginControllerInfoMap.keySet()) {
			PluginControllerInfo pluginControllerInfo = bundleNamePluginControllerInfoMap
					.get(key);
			rtnMap.put(key, pluginControllerInfo.getMethodNameList());
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
					mav.getModel(), pluginControllerInfo.getWebAppService());
			return mmav;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
