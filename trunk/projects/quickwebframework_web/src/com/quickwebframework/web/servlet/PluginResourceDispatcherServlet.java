package com.quickwebframework.web.servlet;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.quickwebframework.web.util.IoUtil;
import com.quickwebframework.web.listener.QuickWebFrameworkLoaderListener;

public class PluginResourceDispatcherServlet extends QwfServlet {

	private static final long serialVersionUID = 9199739683584454735L;

	public static final String MAPPING_PROPERTY_KEY = "quickwebframework.pluginResourceServlet.mapping";
	public static final String BUNDLE_NAME_PARAMETER_NAME_PROPERTY_KEY = "quickwebframework.pluginResourceServlet.bundleNameParameterName";
	public static final String RESOURCE_PATH_PARAMETER_NAME_PROPERTY_KEY = "quickwebframework.pluginResourceServlet.resourcePathParameterName";
	public static final String RESOURCE_PATH_PREFIX_PROPERTY_KEY = "quickwebframework.pluginResourceServlet.resourcePathPrefix";
	public static final String ALLOW_MIME_PROPERTY_KEY = "quickwebframework.pluginResourceServlet.allowMIME";

	// 默认允许的MIME
	private final static String DEFAULT_ALLOW_MIME = ".js=application/x-javascript;.txt=text/plain;.htm=text/html;.html=text/html;.jpg=image/jpeg;.png=image/png";

	// URL映射风格.1:/view/[插件名称]/[路径] 2:/[插件名称]/view/[路径] 3:/view?bn=[插件名称]&mn=[路径]
	private static int urlMappingStyle = 0;

	private String mapping;
	private String bundleNameParameterName;
	private String resourcePathParameterName;
	// 资源路径前缀，通过设置可以配置统一前缀
	private String resourcePathPrefix;

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public void setBundleNameParameterName(String bundleNameParameterName) {
		this.bundleNameParameterName = bundleNameParameterName;
	}

	public void setResourcePathParameterName(String resourcePathParameterName) {
		this.resourcePathParameterName = resourcePathParameterName;
	}

	public void setResourcePathPrefix(String resourcePathPrefix) {
		this.resourcePathPrefix = resourcePathPrefix;
	}

	private Map<String, String> allowMimeMap;

	public void setAllowMime(String allowMime) {
		if (allowMime == null) {
			return;
		}
		allowMimeMap = new HashMap<String, String>();

		// 读取配置
		String[] configLines = allowMime.split(";");
		for (String configLine : configLines) {
			String[] tmpArray = configLine.split("=");
			if (tmpArray.length >= 2) {
				String key = tmpArray[0].trim();
				String value = tmpArray[1].trim();
				allowMimeMap.put(key, value);
			}
		}
	}

	public void setAllowMimeMap(Map<String, String> map) {
		allowMimeMap = map;
	}

	// 初始化函数
	public void init() throws ServletException {
		if (allowMimeMap == null)
			this.setAllowMime(DEFAULT_ALLOW_MIME);
	}

	// 初始化插件资源Servlet
	public static QwfServlet initServlet(ServletContext servletContext,
			Properties quickWebFrameworkProperties) {
		// 添加插件资源Servlet
		PluginResourceDispatcherServlet pluginResourceDispatcherServlet = new PluginResourceDispatcherServlet();

		// 读取允许访问的资源后缀
		Map<String, String> allowMimeMap = new HashMap<String, String>();
		Enumeration<?> quickWebFrameworkPropertieNameEnumeration = quickWebFrameworkProperties
				.propertyNames();
		while (quickWebFrameworkPropertieNameEnumeration.hasMoreElements()) {
			String propertieName = (String) quickWebFrameworkPropertieNameEnumeration
					.nextElement();
			if (propertieName
					.startsWith(PluginResourceDispatcherServlet.ALLOW_MIME_PROPERTY_KEY)) {
				String fileSuffix = propertieName
						.substring(PluginResourceDispatcherServlet.ALLOW_MIME_PROPERTY_KEY
								.length());
				String mimeString = quickWebFrameworkProperties
						.getProperty(propertieName);
				allowMimeMap.put(fileSuffix, mimeString);
			}
		}
		if (allowMimeMap.size() > 0) {
			pluginResourceDispatcherServlet.setAllowMimeMap(allowMimeMap);
		}
		String pluginResourceDispatcherServletMapping = quickWebFrameworkProperties
				.getProperty(PluginResourceDispatcherServlet.MAPPING_PROPERTY_KEY);
		// 设置映射的URL
		pluginResourceDispatcherServlet
				.setMapping(pluginResourceDispatcherServletMapping);
		// 设置请求参数中的插件名称参数
		pluginResourceDispatcherServlet
				.setBundleNameParameterName(quickWebFrameworkProperties
						.getProperty(PluginResourceDispatcherServlet.BUNDLE_NAME_PARAMETER_NAME_PROPERTY_KEY));
		// 设置请求参数中的路径名称参数
		pluginResourceDispatcherServlet
				.setResourcePathParameterName(quickWebFrameworkProperties
						.getProperty(PluginResourceDispatcherServlet.RESOURCE_PATH_PARAMETER_NAME_PROPERTY_KEY));
		// 设置资源路径统一前缀
		pluginResourceDispatcherServlet
				.setResourcePathPrefix(quickWebFrameworkProperties
						.getProperty(PluginResourceDispatcherServlet.RESOURCE_PATH_PREFIX_PROPERTY_KEY));

		// 如果映射的URL是类似于 */view/
		if (pluginResourceDispatcherServlet.mapping.startsWith("*")) {
			urlMappingStyle = 1;
		}
		// 如果映射的URL是类似于 /view/*
		else if (pluginResourceDispatcherServlet.mapping.endsWith("*")) {
			urlMappingStyle = 2;
		}
		// 否则应该是类似于/view，只需要直接取出参数
		else {
			urlMappingStyle = 3;
			if (StringUtils
					.isEmpty(pluginResourceDispatcherServlet.bundleNameParameterName)) {
				throw new RuntimeException(
						"在QuickWebFramework的配置文件中未找到配置项:[quickwebframework.pluginResourceServlet.bundleNameParameterName]");
			}
			if (StringUtils
					.isEmpty(pluginResourceDispatcherServlet.resourcePathParameterName)) {
				throw new RuntimeException(
						"在QuickWebFramework的配置文件中未找到配置项:[quickwebframework.pluginResourceServlet.resourcePathParameterName]");
			}
		}

		return pluginResourceDispatcherServlet;
	}

	@Override
	public boolean isUrlMatch(String requestUrlWithoutContextPath) {
		if (urlMappingStyle == 1) {
			String[] tmpArray = StringUtils.split(requestUrlWithoutContextPath,
					"/");
			if (tmpArray.length < 2) {
				return false;
			}
			String keyword = mapping.replace("*", "").replace("/", "");
			return tmpArray[1].equals(keyword);
		} else if (urlMappingStyle == 2) {
			String[] tmpArray = StringUtils.split(requestUrlWithoutContextPath,
					"/");
			if (tmpArray.length < 2) {
				return false;
			}
			return requestUrlWithoutContextPath.startsWith(mapping.replace("*",
					""));
		} else if (urlMappingStyle == 3) {
			return requestUrlWithoutContextPath.startsWith(mapping);
		}
		return false;
	}

	// 处理HTTP方法
	private void processHttpMethod(String javaMethodName,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Object dispatcherServletObject = QuickWebFrameworkLoaderListener
					.getDispatcherServletObject();
			if (dispatcherServletObject == null) {
				response.sendError(404, "DispatcherServlet object not found!");
				return;
			}
			// dispatcherServlet的类
			Class<?> dispatcherServletClazz = dispatcherServletObject
					.getClass();

			// 找到对应的处理方法
			Method httpMethod = dispatcherServletClazz.getMethod(
					javaMethodName, HttpServletRequest.class,
					HttpServletResponse.class, String.class, String.class);

			if (httpMethod != null) {
				String bundleName = null;
				String resourcePath = null;

				String contextPath = request.getContextPath();
				String requestURI = request.getRequestURI();

				// 如果映射的URL是类似于 */resource/
				if (urlMappingStyle == 1) {
					String otherString = requestURI.substring(contextPath
							.length());
					String[] tmpArray = StringUtils.split(otherString, "/");
					if (tmpArray.length >= 2) {
						bundleName = tmpArray[0];
						resourcePath = otherString.substring(bundleName
								.length() + mapping.length());
					}
				}
				// 如果映射的URL是类似于 /resource/*
				else if (urlMappingStyle == 2) {
					String otherString = requestURI.substring(contextPath
							.length() + mapping.length() - 1);
					String[] tmpArray = StringUtils.split(otherString, "/");
					if (tmpArray.length >= 2) {
						bundleName = tmpArray[0];
						resourcePath = otherString.substring(bundleName
								.length() + 1);
					}
				}
				// 如果映射的URL是类似于 /resource，只需要直接取出参数
				else if (urlMappingStyle == 3) {
					bundleName = request.getParameter(bundleNameParameterName);
					resourcePath = request
							.getParameter(resourcePathParameterName);
				}

				// 如果有统一前缀，则添加统一前缀
				if (resourcePathPrefix != null && !resourcePathPrefix.isEmpty()) {
					resourcePath = resourcePathPrefix + resourcePath;
				}

				// 此处资源路径的后缀判断只否允许访问此资源

				// 如果请求的资源路径没有后缀，则不允许访问
				if (!resourcePath.contains(".")) {
					// 返回400 Bad Request
					response.sendError(400, "请求的资源[" + resourcePath
							+ "]未包括后缀，不允许访问！");
					return;
				}
				String[] tmpArray = resourcePath.split("\\.");
				String extension = "." + tmpArray[tmpArray.length - 1];
				// 如果此扩展名不在被允许访问的列表内
				if (!allowMimeMap.containsKey(extension)) {
					// 返回403 Forbidden
					response.sendError(403, "扩展名[" + extension
							+ "]不在被允许访问的扩展名列表中！");
					return;
				}

				// 调用HTTP方法
				InputStream inputStream = (InputStream) httpMethod.invoke(
						dispatcherServletObject, request, response, bundleName,
						resourcePath);

				// 如果资源未找到
				if (inputStream == null) {
					response.sendError(404, "在插件[" + bundleName + "]中未找到资源["
							+ resourcePath + "]！");
					return;
				}

				// 输出
				String contentType = allowMimeMap.get(extension);
				response.setContentType(contentType);
				OutputStream outputStream = response.getOutputStream();
				IoUtil.copyStream(inputStream, outputStream);
				inputStream.close();
			} else {
				response.sendError(
						404,
						String.format(
								"Java method [%s] not found in dispatcherServletObject!",
								javaMethodName));
				return;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		processHttpMethod("doGetResource", request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		processHttpMethod("doGetResource", request, response);
	}
}
