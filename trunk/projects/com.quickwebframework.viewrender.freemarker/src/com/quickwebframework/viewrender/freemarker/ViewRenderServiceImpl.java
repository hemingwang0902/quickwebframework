package com.quickwebframework.viewrender.freemarker;

import java.util.Properties;

import org.osgi.framework.BundleContext;

import com.quickwebframework.entity.ViewRender;
import com.quickwebframework.service.ViewRenderService;

import freemarker.template.Configuration;

public class ViewRenderServiceImpl implements ViewRenderService {
	private ViewRender viewRender;

	public ViewRenderServiceImpl(BundleContext bundleContext, Properties prop) {
		Configuration configuration = new Configuration();
		// 配置设置
		try {
			configuration.setSettings(prop);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		viewRender = new FreemarkerViewRender(configuration, bundleContext,
				":", ".ftl");
	}

	@Override
	public ViewRender getViewRender() {
		return viewRender;
	}
}
