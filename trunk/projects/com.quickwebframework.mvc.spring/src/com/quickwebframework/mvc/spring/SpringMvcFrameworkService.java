package com.quickwebframework.mvc.spring;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.entity.MvcModelAndView;
import com.quickwebframework.mvc.spring.entity.impl.PluginControllerInfo;
import com.quickwebframework.mvc.spring.util.FolderClassLoader;
import com.quickwebframework.mvc.spring.util.PluginPathMatcher;
import com.quickwebframework.mvc.spring.util.PluginUrlPathHelper;
import com.quickwebframework.service.MvcFrameworkService;
import com.quickwebframework.service.WebAppService;
import com.quickwebframework.service.core.PluginService;
import com.quickwebframework.util.BundleUtil;
import com.quickwebframework.util.IoUtil;

public class SpringMvcFrameworkService implements MvcFrameworkService {

	private static Log log = LogFactory.getLog(SpringMvcFrameworkService.class);

	// Bundle上下文
	private BundleContext bundleContext;
	private Bundle selfBundle;
	// 插件名与ControllerService对应Map
	private Map<String, PluginControllerInfo> bundleNamePluginControllerInfoMap = new HashMap<String, PluginControllerInfo>();

	// 注册过滤器，线程的服务
	private PluginService pluginService;

	private void refreshPluginService() {
		ServiceReference serviceReference = bundleContext
				.getServiceReference(PluginService.class.getName());
		if (serviceReference == null) {
			pluginService = null;
			return;
		}
		pluginService = (PluginService) bundleContext
				.getService(serviceReference);
	}

	public SpringMvcFrameworkService(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		selfBundle = bundleContext.getBundle();

		// 刷新插件服务(注册过滤器，线程等)
		refreshPluginService();

		bundleContext.addServiceListener(new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent arg0) {
				String serviceReferenceName = arg0.getServiceReference()
						.toString();
				// 如果插件服务改变，刷新插件服务
				if (serviceReferenceName.contains(PluginService.class.getName())) {
					refreshPluginService();
				}
			}
		});
	}

	private boolean initPluginControllerInfo(Bundle bundle,
			PluginControllerInfo pluginControllerInfo) {

		// 得到临时目录
		String tmpFolderPath = System.getProperty("java.io.tmpdir");
		tmpFolderPath = tmpFolderPath + File.separator
				+ UUID.randomUUID().toString();

		// 解压Bundle文件
		BundleUtil.extractBundleFiles(bundle, tmpFolderPath);

		// 初始化AnnotationConfigApplicationContext
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

		// 复制Spring需要的资源文件
		// 比如java/lang/Thread.class.file到临时目录/java/lang/Thread.class
		try {
			/*
			String classFilePath = "/java/lang/Thread.class";
			log.info("selfBundle.getSymbolicName:"
					+ selfBundle.getSymbolicName());
			InputStream inputStream = selfBundle.getResource(
					"/com/quickwebframework/mvc/spring/resource"
							+ classFilePath + ".file").openStream();
			File threadClassFile = new File(tmpFolderPath + classFilePath);
			threadClassFile.getParentFile().mkdirs();
			OutputStream outputStream = new FileOutputStream(threadClassFile);
			IoUtil.copyStream(inputStream, outputStream);
			outputStream.close();
			inputStream.close();
			 */
			
			// 开始Spring扫描
			FolderClassLoader folderClassLoader = new FolderClassLoader( pluginControllerInfo.getWebAppService().getClass().getClassLoader(),
					tmpFolderPath);
			applicationContext.setClassLoader(folderClassLoader);
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
		} finally {
			// 删除临时目录文件
			IoUtil.deleteFile(tmpFolderPath);
		}

		// 从ApplicationContext得到过滤器列表
		Map<String, Filter> filterMap = applicationContext
				.getBeansOfType(Filter.class);
		for (Filter filter : filterMap.values()) {
			pluginService.addFilter(bundle, filter);
		}

		// 从ApplicationContext得到线程列表
		Map<String, Thread> threadMap = applicationContext
				.getBeansOfType(Thread.class);
		for (Thread thread : threadMap.values()) {
			pluginService.addThread(bundle, thread);
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
	public MvcModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, String bundleName, String methodName) {
		// 如果插件名称为null或Map中不存在此插件名称
		if (bundleName == null
				|| !bundleNamePluginControllerInfoMap.containsKey(bundleName)) {
			return null;
		}

		PluginControllerInfo pluginControllerInfo = bundleNamePluginControllerInfoMap
				.get(bundleName);

		String mappingUrl = "/" + bundleName + "/" + methodName;

		// 如果方法名称为null或Map中不存在此方法名称
		if (methodName == null
				|| !pluginControllerInfo.getMappingUrlHandlerMap().containsKey(
						mappingUrl)) {
			return null;
		}

		// 得到处理器对象
		Object handler = pluginControllerInfo.getMappingUrlHandlerMap().get(
				mappingUrl);
		// 得到该处理器对应的适配器
		AnnotationMethodHandlerAdapter adapter = pluginControllerInfo
				.getHandlerAdapterMap().get(handler);

		try {
			// 执行处理，得到模型与视图
			ModelAndView mav = adapter.handle(request, response, handler);
			MvcModelAndView mmav = new MvcModelAndView(mav.getViewName(),
					mav.getModel(), pluginControllerInfo.getWebAppService());
			return mmav;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
