package com.quickwebframework.viewrender.freemarker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.viewrender.ViewRenderService;
import com.quickwebframework.viewrender.freemarker.service.impl.ViewRenderServiceImpl;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private ServiceRegistration<?> viewRenderServiceRegistration;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext context) throws Exception {
		Activator.context = context;

		// 得到freemarker配置文件路径
		String freemarkerPropertyFilePath = WebContext
				.getQwfConfig("qwf-vr-freemarker.properties");
		if (freemarkerPropertyFilePath == null
				|| freemarkerPropertyFilePath.isEmpty()) {
			throw new RuntimeException(
					"Can't found qwf config 'qwf-vr-freemarker.properties'！");
		}
		freemarkerPropertyFilePath = WebContext
				.getRealPath(freemarkerPropertyFilePath);

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

		// 注册视图渲染服务
		ViewRenderService viewRenderService = new ViewRenderServiceImpl(
				freemarkerProp);
		Dictionary<String, String> dict = new Hashtable<String, String>();
		dict.put("bundle", context.getBundle().getSymbolicName());
		viewRenderServiceRegistration = context.registerService(
				ViewRenderService.class.getName(), viewRenderService, dict);
	}

	public void stop(BundleContext context) throws Exception {
		// 取消注册视图渲染服务
		viewRenderServiceRegistration.unregister();
		
		Activator.context = null;
	}
}
