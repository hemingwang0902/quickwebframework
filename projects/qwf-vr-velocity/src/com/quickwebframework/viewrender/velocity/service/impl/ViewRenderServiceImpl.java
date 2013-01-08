package com.quickwebframework.viewrender.velocity.service.impl;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.quickwebframework.service.ViewRenderService;
import com.quickwebframework.viewrender.velocity.util.BundleResourceLoader;

public class ViewRenderServiceImpl extends ViewRenderService {

	private VelocityEngine engine;

	public ViewRenderServiceImpl(Properties velocityProp) {
		engine = new VelocityEngine(velocityProp);
		engine.setProperty(Velocity.RESOURCE_LOADER, "class");
		engine.setProperty("class.resource.loader.class",
				BundleResourceLoader.class.getName());
		engine.init();
	}

	@Override
	public void renderView(String bundleName, String viewName,
			HttpServletRequest request, HttpServletResponse response) {
		// 得到模板
		Template template = engine.getTemplate(viewName);
		// 准备数据
		VelocityContext context = new VelocityContext();
		Enumeration<String> attributeNameEnumeration = request
				.getAttributeNames();
		while (attributeNameEnumeration.hasMoreElements()) {
			String attributeName = attributeNameEnumeration.nextElement();
			context.put(attributeName, request.getAttribute(attributeName));
		}
		// 输出
		try {
			template.merge(context, response.getWriter());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
