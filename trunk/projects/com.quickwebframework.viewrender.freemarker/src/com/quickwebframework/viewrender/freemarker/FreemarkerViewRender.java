package com.quickwebframework.viewrender.freemarker;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;

public class FreemarkerViewRender {

	// Freemarker配置
	private Configuration configuration;

	// 插件模板加载器
	private PluginTemplateLoader pluginTemplateLoader;

	public FreemarkerViewRender(Configuration configuration,
			PluginTemplateLoader pluginTemplateLoader) {
		this.configuration = configuration;
		this.pluginTemplateLoader = pluginTemplateLoader;
		configuration.setTemplateLoader(pluginTemplateLoader);
	}

	public void renderView(String bundleName, String viewName,
			HttpServletRequest request, HttpServletResponse response) {

		// 如果ViewName中未包括分隔符，即未包含插件名称，则添加当前插件名称为前缀
		if (!viewName.contains(pluginTemplateLoader
				.getPluginNameAndPathSplitString())) {
			viewName = bundleName
					+ pluginTemplateLoader.getPluginNameAndPathSplitString()
					+ viewName;
		}
		// 准备数据
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
