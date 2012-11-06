package com.quickwebframework.viewrender.freemarker.service.impl;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;

import com.quickwebframework.service.ViewRenderService;
import com.quickwebframework.viewrender.freemarker.util.FreemarkerViewRender;
import com.quickwebframework.viewrender.freemarker.util.PluginTemplateLoader;

import freemarker.template.Configuration;

public class ViewRenderServiceImpl implements ViewRenderService {
	private FreemarkerViewRender viewRender;

	public ViewRenderServiceImpl(BundleContext bundleContext,
			Properties freeMarkerProp, Properties viewRenderProp) {
		Configuration configuration = new Configuration();
		// 配置Freemarker
		try {
			configuration.setSettings(freeMarkerProp);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// 配置ViewRender
		PluginTemplateLoader pluginTemplateLoader = new PluginTemplateLoader();

		// 分隔符
		if (viewRenderProp
				.containsKey("com.quickwebframework.viewrender.pluginNameAndPathSplitString")) {
			pluginTemplateLoader
					.setPluginNameAndPathSplitString(viewRenderProp
							.getProperty("com.quickwebframework.viewrender.pluginNameAndPathSplitString"));
		}
		// 前缀
		if (viewRenderProp
				.containsKey("com.quickwebframework.viewrender.viewNamePrefix")) {
			pluginTemplateLoader
					.setViewNamePrefix(viewRenderProp
							.getProperty("com.quickwebframework.viewrender.viewNamePrefix"));
		}
		// 后缀
		if (viewRenderProp
				.containsKey("com.quickwebframework.viewrender.viewNameSuffix")) {
			pluginTemplateLoader
					.setViewNameSuffix(viewRenderProp
							.getProperty("com.quickwebframework.viewrender.viewNameSuffix"));
		}
		FreemarkerViewRender freemarkerViewRender = new FreemarkerViewRender(
				configuration, pluginTemplateLoader);
		viewRender = freemarkerViewRender;
	}

	@Override
	public void renderView(String bundleName, String viewName,
			HttpServletRequest request, HttpServletResponse response) {
		viewRender.renderView(bundleName, viewName, request, response);
	}

}
