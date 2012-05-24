package com.quickwebframework.viewrender.freemarker;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;

import com.quickwebframework.entity.ViewRender;

import freemarker.template.Configuration;

public class FreemarkerViewRender implements ViewRender {

	// Freemarker配置
	private Configuration configuration;

	// 插件名称与路径分隔符
	private String pluginNameAndPathSplitString = ":";

	@Override
	public String getPluginNameAndPathSplitString() {
		return pluginNameAndPathSplitString;
	}

	public void setPluginNameAndPathSplitString(
			String pluginNameAndPathSplitString) {
		this.pluginNameAndPathSplitString = pluginNameAndPathSplitString;
	}

	// 视图名称前缀
	private String viewNamePrefix = "";

	public void setViewNamePrefix(String viewNamePrefix) {
		this.viewNamePrefix = viewNamePrefix;
	}

	// 视图名称后缀
	private String viewNameSuffix = ".ftl";

	public void setViewNameSuffix(String viewNameSuffix) {
		this.viewNameSuffix = viewNameSuffix;
	}

	public FreemarkerViewRender(Configuration configuration,
			BundleContext bundleContext) {
		this.configuration = configuration;
		configuration.setTemplateLoader(new PluginTemplateLoader(bundleContext,
				getPluginNameAndPathSplitString()));
	}

	@Override
	public void renderView(String viewName, HttpServletRequest request,
			HttpServletResponse response) {
		// 对视图名称进行处理

		// 如果有统一前缀，则添加统一前缀
		if (viewNamePrefix != null && !viewNamePrefix.isEmpty()) {
			viewName = viewName.replace(pluginNameAndPathSplitString,
					pluginNameAndPathSplitString + viewNamePrefix);
		}

		// 为视图名称添加统一的后缀
		viewName = viewName + viewNameSuffix;

		Map<String, Object> root = new HashMap<String, Object>();
		Enumeration<String> attributeNameEnumeration = request
				.getAttributeNames();
		while (attributeNameEnumeration.hasMoreElements()) {
			String key = attributeNameEnumeration.nextElement();
			root.put(key, request.getAttribute(key));
		}
		try {
			response.setCharacterEncoding(configuration.getDefaultEncoding());
			response.setContentType("text/html");
			configuration.getTemplate(viewName).process(root,
					response.getWriter());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
