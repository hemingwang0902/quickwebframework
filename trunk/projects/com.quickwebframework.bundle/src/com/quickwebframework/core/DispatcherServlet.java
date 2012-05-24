package com.quickwebframework.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
import com.quickwebframework.entity.ViewRender;
import com.quickwebframework.entity.impl.PluginControllerInfo;
import com.quickwebframework.service.LogFactory;
import com.quickwebframework.service.PluginService;
import com.quickwebframework.service.ViewRenderService;
import com.quickwebframework.service.WebSettingService;
import com.quickwebframework.util.BundleUtil;
import com.quickwebframework.util.FolderClassLoader;
import com.quickwebframework.util.IoUtil;
import com.quickwebframework.util.PluginPathMatcher;
import com.quickwebframework.util.PluginUrlPathHelper;

public class DispatcherServlet {
	private static Log log = LogFactory.getLog(DispatcherServlet.class);

	// Bundle上下文
	private BundleContext bundleContext;
	// 插件名与ControllerService对应Map
	private Map<String, PluginControllerInfo> bundleNamePluginControllerInfoMap;

	/**
	 * 渲染视图
	 * 
	 * @return
	 */
	public void renderView(ServletContext servletContext,
			PluginService controllerService, String viewName,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			ServiceReference viewRenderServiceReference = bundleContext
					.getServiceReference(ViewRenderService.class.getName());
			if (viewRenderServiceReference != null) {
				ViewRenderService viewRenderService = (ViewRenderService) bundleContext
						.getService(viewRenderServiceReference);
				ViewRender viewRender = viewRenderService.getViewRender();
				// 渲染视图
				viewRender.renderView(controllerService.getBundle()
						.getSymbolicName(), viewName, request, response);
			} else {
				response.sendError(500,
						"[com.quickwebframework.core.DispatcherServlet] cannot found ViewRender!");
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public DispatcherServlet(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		final Bundle currentBundle = bundleContext.getBundle();
		bundleNamePluginControllerInfoMap = new HashMap<String, PluginControllerInfo>();

		try {
			ServiceReference[] serviceReferences = bundleContext
					.getServiceReferences(PluginService.class.getName(), null);
			if (serviceReferences != null) {
				for (ServiceReference serviceReference : serviceReferences) {
					PluginService obj = (PluginService) bundleContext
							.getService(serviceReference);
					initControllerService(obj);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		bundleContext.addServiceListener(new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent arg0) {
				int serviceEventType = arg0.getType();
				if (serviceEventType == ServiceEvent.REGISTERED) {
					log.info(String.format("[%s]插件的[%s]服务已注册", arg0
							.getServiceReference().getBundle()
							.getSymbolicName(), arg0.getServiceReference()));

					ServiceReference serviceReference = arg0
							.getServiceReference();
					Object obj = bundleContext.getService(serviceReference);
					// 如果上服务不是ControllerService
					if (!PluginService.class.isInstance(obj))
						return;
					initControllerService((PluginService) obj);
				} else if (serviceEventType == ServiceEvent.UNREGISTERING) {
					log.info(String.format("[%s]插件的[%s]服务正在取消注册", arg0
							.getServiceReference().getBundle()
							.getSymbolicName(), arg0.getServiceReference()));

					ServiceReference serviceReference = arg0
							.getServiceReference();

					// 如果卸载的插件就是自己
					if (currentBundle.equals(serviceReference.getBundle()))
						return;

					Object obj = bundleContext.getService(serviceReference);
					// 如果上服务不是ControllerService
					if (!PluginService.class.isInstance(obj))
						return;
					uninitControllerService((PluginService) obj);
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
			Bundle selfBundle = bundleContext.getBundle();

			String classFilePath = "/java/lang/Thread.class";
			InputStream inputStream = selfBundle
					.getResource(
							"/com/quickwebframework/resource" + classFilePath
									+ ".file").openStream();
			File threadClassFile = new File(tmpFolderPath + classFilePath);
			threadClassFile.getParentFile().mkdirs();
			OutputStream outputStream = new FileOutputStream(threadClassFile);
			IoUtil.copyStream(inputStream, outputStream);
			outputStream.close();
			inputStream.close();

			// 开始Spring扫描
			FolderClassLoader folderClassLoader = new FolderClassLoader(
					pluginControllerInfo.getControllerService()
							.getClassLoader(), tmpFolderPath);
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
		pluginControllerInfo.getFilterList().addAll(filterMap.values());

		// 从ApplicationContext得到线程列表
		Map<String, Thread> threadMap = applicationContext
				.getBeansOfType(Thread.class);
		pluginControllerInfo.getThreadList().addAll(threadMap.values());
		// 开启线程
		for (Thread thread : pluginControllerInfo.getThreadList()) {
			try {
				thread.start();
				log.info(String.format("已成功启动插件[%s]的线程[%s]！",
						bundle.getSymbolicName(), thread));
			} catch (Exception ex) {
				log.error(String.format("启动插件[%s]的线程[%s]失败！",
						bundle.getSymbolicName(), thread));
			}
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

	private void initControllerService(PluginService controllerService) {
		Bundle bundle = controllerService.getBundle();
		String bundleName = bundle.getSymbolicName();
		// 如果Map的键中有此插件名称
		if (bundleNamePluginControllerInfoMap.containsKey(bundleName)) {
			PluginControllerInfo pluginControllerInfo = bundleNamePluginControllerInfoMap
					.get(bundleName);
			PluginService preControllerService = pluginControllerInfo
					.getControllerService();
			Bundle preBundle = preControllerService.getBundle();
			if (preBundle.getState() == Bundle.ACTIVE)
				return;
			bundleNamePluginControllerInfoMap.remove(bundleName);
		}

		PluginControllerInfo pluginControllerInfo = new PluginControllerInfo(
				controllerService);
		if (initPluginControllerInfo(bundle, pluginControllerInfo)) {
			bundleNamePluginControllerInfoMap.put(bundleName,
					pluginControllerInfo);
		}
	}

	private void uninitControllerService(PluginService controllerService) {
		Bundle bundle = controllerService.getBundle();
		String bundleName = bundle.getSymbolicName();

		if (bundleNamePluginControllerInfoMap.containsKey(bundleName)) {
			PluginControllerInfo pluginControllerInfo = bundleNamePluginControllerInfoMap
					.get(bundleName);
			// 中断线程
			for (Thread thread : pluginControllerInfo.getThreadList()) {
				try {
					thread.interrupt();
					log.info(String.format("已成功向插件[%s]的线程[%s]中断命令！",
							bundleName, thread));
				} catch (Exception ex) {
					log.error(String.format("向插件[%s]的线程[%s]中断命令失败！",
							bundleName, thread));
					ex.printStackTrace();
				}
			}
		}
		bundleNamePluginControllerInfoMap.remove(bundleName);
	}

	private String processHttp(Object request, Object response,
			String bundleName, String methodName) {
		try {
			PluginHttpServletRequest req = new PluginHttpServletRequest(request);
			PluginHttpServletResponse rep = new PluginHttpServletResponse(
					response);

			if (!bundleNamePluginControllerInfoMap.containsKey(bundleName)) {
				rep.sendError(404, "Bundle not found!");
				return null;
			}

			PluginControllerInfo pluginControllerInfo = bundleNamePluginControllerInfoMap
					.get(bundleName);

			String mappingUrl = String.format("/%s/%s", bundleName, methodName);

			if (!pluginControllerInfo.getMappingUrlHandlerMap().containsKey(
					mappingUrl)) {
				rep.sendError(404, "Method not found!");
				return null;
			}

			// 得到处理器对象
			Object handler = pluginControllerInfo.getMappingUrlHandlerMap()
					.get(mappingUrl);
			// 得到该处理器对应的适配器
			AnnotationMethodHandlerAdapter adapter = pluginControllerInfo
					.getHandlerAdapterMap().get(handler);
			// 执行处理，得到模型与视图
			ModelAndView mav = adapter.handle(req, rep, handler);
			String viewName = mav.getViewName();

			// 如果视图不为空
			if (viewName != null) {
				renderView(req.getServletContext(),
						pluginControllerInfo.getControllerService(), viewName,
						req, rep);
			}
			return viewName;

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// 处理HTTP请求
	public String service(Object request, Object response, String bundleName,
			String methodName) {
		return processHttp(request, response, bundleName, methodName);
	}

	// 处理根URL："/"请求
	public void serviceRootUrl(Object request, Object response)
			throws IOException {
		PluginHttpServletResponse rep = new PluginHttpServletResponse(response);
		ServiceReference serviceReference = bundleContext
				.getServiceReference(WebSettingService.class.getName());
		if (serviceReference == null) {
			rep.getWriter()
					.write("<html><head><title>Powered by QuickWebFramework</title></head><body>Welcome to use <a href=\"http://quickwebframework.com\">QuickWebFramework</a>!</body></html>");
			return;
		}
		WebSettingService webSettingService = (WebSettingService) bundleContext
				.getService(serviceReference);
		rep.sendRedirect(webSettingService.getRootRedirectUrl());
	}

	// 得到资源
	public InputStream doGetResource(Object request, Object response,
			String bundleName, String resourcePath) {
		if (!bundleNamePluginControllerInfoMap.containsKey(bundleName)) {
			return null;
		}
		return bundleNamePluginControllerInfoMap.get(bundleName)
				.getControllerService().getClassLoader()
				.getResourceAsStream(resourcePath);
	}

	// 处理过滤器,返回值是是否继续处理其他的过滤器
	public boolean doFilter(Object request, Object response, Object chain)
			throws IOException, ServletException {
		PluginHttpServletRequest req = new PluginHttpServletRequest(request);
		PluginHttpServletResponse rep = new PluginHttpServletResponse(response);
		BooleanFilterChain booleanFilterChain = new BooleanFilterChain();

		for (PluginControllerInfo pluginControllerInfo : bundleNamePluginControllerInfoMap
				.values()) {
			for (Filter filter : pluginControllerInfo.getFilterList()) {
				filter.doFilter(req, rep, booleanFilterChain);
				if (!booleanFilterChain.isContinueFilter)
					return false;
			}
		}
		return true;
	}

	public class BooleanFilterChain implements FilterChain {
		// 是否继续过滤
		private boolean isContinueFilter = false;

		public boolean isContinueFilter() {
			return isContinueFilter;
		}

		public void setContinueFilter(boolean isContinueFilter) {
			this.isContinueFilter = isContinueFilter;
		}

		@Override
		public void doFilter(ServletRequest arg0, ServletResponse arg1)
				throws IOException, ServletException {
			isContinueFilter = true;
		}

	}
}
