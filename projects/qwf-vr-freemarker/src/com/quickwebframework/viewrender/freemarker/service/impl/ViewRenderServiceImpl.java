package com.quickwebframework.viewrender.freemarker.service.impl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.service.ViewRenderService;
import com.quickwebframework.viewrender.freemarker.util.PluginTemplateLoader;

import freemarker.template.Configuration;

public class ViewRenderServiceImpl extends ViewRenderService {
	private Configuration configuration;

	public ViewRenderServiceImpl(Properties freeMarkerProp) {
		configuration = new Configuration();
		// 配置Freemarker
		try {
			configuration.setSettings(freeMarkerProp);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// 配置ViewRender
		PluginTemplateLoader pluginTemplateLoader = new PluginTemplateLoader(
				this);
		configuration.setTemplateLoader(pluginTemplateLoader);
	}

	@Override
	public void renderView(String bundleName, String viewName,
			HttpServletRequest request, HttpServletResponse response) {

		// 如果ViewName中未包括分隔符，即未包含插件名称，则添加当前插件名称为前缀
		if (!viewName.contains(getPluginNameAndPathSplitString())) {
			viewName = bundleName + getPluginNameAndPathSplitString()
					+ viewName;
		}
		// 准备数据
		Map<String, Object> root = new HashMap<String, Object>();
		Enumeration<?> attributeNameEnumeration = request.getAttributeNames();
		while (attributeNameEnumeration.hasMoreElements()) {
			String key = attributeNameEnumeration.nextElement().toString();
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
