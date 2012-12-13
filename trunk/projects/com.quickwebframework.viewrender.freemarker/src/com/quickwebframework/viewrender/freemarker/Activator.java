package com.quickwebframework.viewrender.freemarker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.service.ViewRenderService;
import com.quickwebframework.viewrender.freemarker.service.impl.ViewRenderServiceImpl;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {

		String freemarkerPropertyFilePath = null;
		String viewrenderPropertyFilePath = null;

		// 得到freemarker配置文件路径
		ServiceReference<?>[] serviceReferences = context
				.getServiceReferences(
						String.class.getName(),
						"(quickwebframework.config=com.quickwebframework.viewrender.freemarker.properties)");
		if (serviceReferences != null && serviceReferences.length > 0) {
			freemarkerPropertyFilePath = (String) context
					.getService(serviceReferences[0]);
		}
		if (freemarkerPropertyFilePath == null
				|| freemarkerPropertyFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found property 'quickwebframework.config.com.quickwebframework.viewrender.freemarker.properties'！");
		}
		freemarkerPropertyFilePath = WebContext
				.getServletContext().getRealPath(freemarkerPropertyFilePath);

		// 得到viewrender配置文件路径
		serviceReferences = context
				.getServiceReferences(String.class.getName(),
						"(quickwebframework.config=com.quickwebframework.viewrender.properties)");
		if (serviceReferences != null && serviceReferences.length > 0) {
			viewrenderPropertyFilePath = (String) context
					.getService(serviceReferences[0]);
		}
		if (viewrenderPropertyFilePath == null
				|| viewrenderPropertyFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found property 'quickwebframework.config.com.quickwebframework.viewrender.properties'！");
		}
		viewrenderPropertyFilePath = WebContext
				.getServletContext().getRealPath(viewrenderPropertyFilePath);

		// 读取freemarker配置文件
		File freemarkerPropertyFile = new File(freemarkerPropertyFilePath);
		if (!freemarkerPropertyFile.exists()
				|| !freemarkerPropertyFile.isFile()) {
			String message = String.format("Config file [%s] not exist!",
					freemarkerPropertyFilePath);
			throw new IOException(message);
		}

		InputStream inputStream = new FileInputStream(freemarkerPropertyFile);
		Reader reader = new InputStreamReader(inputStream, "utf-8");
		Properties freemarkerProp = new Properties();
		freemarkerProp.load(reader);
		reader.close();
		inputStream.close();

		// 读取viewrender配置文件
		File viewrenderPropertyFile = new File(viewrenderPropertyFilePath);
		if (!viewrenderPropertyFile.exists()
				|| !viewrenderPropertyFile.isFile()) {
			String message = String.format("Config file [%s] not exist!",
					viewrenderPropertyFilePath);
			throw new IOException(message);
		}

		inputStream = new FileInputStream(viewrenderPropertyFile);
		reader = new InputStreamReader(inputStream, "utf-8");
		Properties viewrenderProp = new Properties();
		viewrenderProp.load(reader);
		reader.close();
		inputStream.close();

		ViewRenderService viewRenderService = new ViewRenderServiceImpl(
				context, freemarkerProp, viewrenderProp);
		context.registerService(ViewRenderService.class.getName(),
				viewRenderService, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}
}
