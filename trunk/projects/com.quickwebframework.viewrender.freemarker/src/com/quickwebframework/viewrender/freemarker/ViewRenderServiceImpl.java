package com.quickwebframework.viewrender.freemarker;

import java.util.Properties;

import org.osgi.framework.BundleContext;

import com.quickwebframework.entity.ViewRender;
import com.quickwebframework.service.ViewRenderService;

import freemarker.template.Configuration;

public class ViewRenderServiceImpl implements ViewRenderService {
	private ViewRender viewRender;

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
		PluginTemplateLoader pluginTemplateLoader = new PluginTemplateLoader(
				bundleContext);

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
	public ViewRender getViewRender() {
		return viewRender;
	}
}
