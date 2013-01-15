package com.quickwebframework.viewrender.velocity;

import java.io.FileInputStream;
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
import com.quickwebframework.viewrender.velocity.service.impl.ViewRenderServiceImpl;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static ViewRenderServiceImpl viewRenderService;
	private ServiceRegistration<?> viewRenderServiceRegistration;

	static BundleContext getContext() {
		return context;
	}

	public static ViewRenderService getViewRenderService() {
		return viewRenderService;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		String velocityPropFilePath = WebContext
				.getQwfConfig("qwf-vr-velocity.properties");
		// velocity的配置
		Properties velocityProp = new Properties();
		// 从文件加载velocity的配置
		if (velocityPropFilePath != null && !velocityPropFilePath.isEmpty()) {
			velocityPropFilePath = WebContext.getRealPath(velocityPropFilePath);

			InputStream inputStream = new FileInputStream(velocityPropFilePath);
			Reader reader = new InputStreamReader(inputStream, "utf-8");
			velocityProp.load(reader);
			reader.close();
			inputStream.close();
		}
		// 注册视图渲染服务
		viewRenderService = new ViewRenderServiceImpl(velocityProp);
		viewRenderService.init();
		Dictionary<String, String> dict = new Hashtable<String, String>();
		dict.put("bundle", bundleContext.getBundle().getSymbolicName());
		viewRenderServiceRegistration = context.registerService(
				ViewRenderService.class.getName(), viewRenderService, dict);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		// 取消注册
		viewRenderServiceRegistration.unregister();
	}

}
