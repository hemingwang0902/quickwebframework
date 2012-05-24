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

	// 插件模板加载器
	private PluginTemplateLoader pluginTemplateLoader;

	@Override
	public String getPluginNameAndPathSplitString() {
		return pluginTemplateLoader.getPluginNameAndPathSplitString();
	}

	public FreemarkerViewRender(Configuration configuration,
			PluginTemplateLoader pluginTemplateLoader) {
		this.configuration = configuration;
		this.pluginTemplateLoader = pluginTemplateLoader;
		configuration.setTemplateLoader(pluginTemplateLoader);
	}

	@Override
	public void renderView(String viewName, HttpServletRequest request,
			HttpServletResponse response) {
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
