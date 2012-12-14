package com.quickwebframework.web.servlet;

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.quickwebframework.web.listener.QuickWebFrameworkLoaderListener;

public class PluginViewDispatcherServlet extends QwfServlet {

	private static PluginViewDispatcherServlet instance;

	public static PluginViewDispatcherServlet getInstance() {
		return instance;
	}

	private static final long serialVersionUID = -148988758320673145L;

	public static final String MAPPING_PROPERTY_KEY = "quickwebframework.pluginViewServlet.mapping";
	public static final String BUNDLE_NAME_PARAMETER_NAME_PROPERTY_KEY = "quickwebframework.pluginViewServlet.bundleNameParameterName";
	public static final String METHOD_NAME_PARAMETER_NAME_PROPERTY_KEY = "quickwebframework.pluginViewServlet.methodNameParameterName";

	public static final String ARG_BUNDLE_NAME = "com.quickwebframework.util.ARG_BUNDLE_NAME";
	public static final String ARG_METHOD_NAME = "com.quickwebframework.util.ARG_METHOD_NAME";
	public static final String BUNDLE_METHOD_URL_TEMPLATE = "com.quickwebframework.util.BUNDLE_METHOD_URL_TEMPLATE";

	// URL映射风格.1:/view/[插件名称]/[方法名称] 2:/[插件名称]/view/[方法名称]
	// 3:/view?bn=[插件名称]&mn=[方法名称]
	private static int urlMappingStyle = 0;

	private String mapping;
	private String bundleNameParameterName;
	private String methodNameParameterName;

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public void setBundleNameParameterName(String bundleNameParameterName) {
		this.bundleNameParameterName = bundleNameParameterName;
	}

	public void setMethodNameParameterName(String methodNameParameterName) {
		this.methodNameParameterName = methodNameParameterName;
	}

	// 初始化插件视图Servlet
	public static QwfServlet initServlet(ServletContext servletContext,
			Properties quickWebFrameworkProperties) {
		// 添加插件视图Servlet
		instance = new PluginViewDispatcherServlet();
		String pluginViewDispatcherServletMapping = quickWebFrameworkProperties
				.getProperty(PluginViewDispatcherServlet.MAPPING_PROPERTY_KEY);
		// 设置映射的URL
		instance.setMapping(pluginViewDispatcherServletMapping);
		// 设置请求参数中的插件名称参数
		instance.setBundleNameParameterName(quickWebFrameworkProperties
				.getProperty(PluginViewDispatcherServlet.BUNDLE_NAME_PARAMETER_NAME_PROPERTY_KEY));
		// 设置请求参数中的方法名称参数
		instance.setMethodNameParameterName(quickWebFrameworkProperties
				.getProperty(PluginViewDispatcherServlet.METHOD_NAME_PARAMETER_NAME_PROPERTY_KEY));

		String urlTemplate = null;
		// 如果映射的URL是类似于 */view/
		if (instance.mapping.startsWith("*")) {
			urlMappingStyle = 1;
			String keyword = instance.mapping.replace("*", "").replace("/", "");
			urlTemplate = "/" + servletContext.getContextPath() + "/%s/"
					+ keyword + "/%s";
			urlTemplate = urlTemplate.replace("//", "/");
		}
		// 如果映射的URL是类似于 /view/*
		else if (instance.mapping.endsWith("*")) {
			urlMappingStyle = 2;
			String keyword = instance.mapping.replace("*", "").replace("/", "");
			urlTemplate = "/" + servletContext.getContextPath() + "/" + keyword
					+ "/%s/%s";
			urlTemplate = urlTemplate.replace("//", "/");
		}
		// 否则应该是类似于/view，只需要直接取出参数
		else {
			urlMappingStyle = 3;
			if (StringUtils.isEmpty(instance.bundleNameParameterName)) {
				throw new RuntimeException(
						"在QuickWebFramework的配置文件中未找到配置项:[quickwebframework.pluginViewServlet.bundleNameParameterName]");
			}
			if (StringUtils.isEmpty(instance.methodNameParameterName)) {
				throw new RuntimeException(
						"在QuickWebFramework的配置文件中未找到配置项:[quickwebframework.pluginViewServlet.methodNameParameterName]");
			}
			urlTemplate = instance.mapping + "?"
					+ instance.bundleNameParameterName + "=%s&"
					+ instance.methodNameParameterName + "=%s";
		}
		if (!urlTemplate.startsWith("/")) {
			urlTemplate = "/" + urlTemplate;
		}
		servletContext.setAttribute(BUNDLE_METHOD_URL_TEMPLATE, urlTemplate);

		return instance;
	}

	@Override
	public boolean isUrlMatch(String requestUrlWithoutContextPath) {
		// 如果是根URL
		if (requestUrlWithoutContextPath.equals("/")) {
			return true;
		}

		if (urlMappingStyle == 1) {
			String[] tmpArray = StringUtils.split(requestUrlWithoutContextPath,
					"/");
			if (tmpArray.length <= 2) {
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
	private void processHttpMethod(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			HttpServlet dispatcherServletObject = QuickWebFrameworkLoaderListener
					.getHttpServletBridgeObject();
			if (dispatcherServletObject == null) {
				response.setContentType("text/html;charset=utf-8");
				StringBuilder sb = new StringBuilder();
				sb.append("<html><head><title>Powered by QuickWebFramework</title></head><body>Welcome to use <a href=\"http://quickwebframework.com\">QuickWebFramework</a>!You can manage bundles in the <a href=\"qwf/index\">Bundle Manage Page</a>!");
				sb.append("<p>QuickWebFrameweb's core bundle not installed or started,please install core bundle and start it first!</p>");
				sb.append("</body></html>");
				response.getWriter().write(sb.toString());
				return;
			}

			String contextPath = request.getContextPath();
			String requestURI = request.getRequestURI();

			// 如果不是根路径
			if (!"/".equals(requestURI)) {
				String bundleName = null;
				String methodName = null;

				// 得到bundleName和methodName

				String requestUrlWithoutContextPath = requestURI
						.substring(contextPath.length());
				if (!this.isUrlMatch(requestUrlWithoutContextPath)) {
				}
				// 如果映射的URL是类似于 */view/
				else if (urlMappingStyle == 1) {
					String[] tmpArray = StringUtils.split(
							requestUrlWithoutContextPath, "/");
					if (tmpArray.length >= 2) {
						bundleName = tmpArray[0];
						methodName = requestUrlWithoutContextPath
								.substring(bundleName.length()
										+ mapping.length());
					}
				}
				// 否则如果是类似于/view/*
				else if (urlMappingStyle == 2) {
					String otherString = requestURI.substring(contextPath
							.length() + mapping.length() - 1);
					String[] tmpArray = StringUtils.split(otherString, "/");
					if (tmpArray.length >= 2) {
						bundleName = tmpArray[0];
						methodName = otherString
								.substring(bundleName.length() + 1);
					}
				}
				// 否则如果是类似于/view，只需要直接取出参数
				else if (urlMappingStyle == 3) {
					bundleName = request.getParameter(bundleNameParameterName);
					methodName = request.getParameter(methodNameParameterName);
				}
				// 将插件名称与方法名称设置到请求属性中
				request.setAttribute(ARG_BUNDLE_NAME, bundleName);
				request.setAttribute(ARG_METHOD_NAME, methodName);
			}
			dispatcherServletObject.service(request, response);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		processHttpMethod(request, response);
	}
}
