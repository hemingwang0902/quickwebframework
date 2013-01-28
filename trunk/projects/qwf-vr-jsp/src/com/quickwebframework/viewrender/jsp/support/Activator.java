package com.quickwebframework.viewrender.jsp.support;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.quickwebframework.viewrender.ViewRenderService;
import com.quickwebframework.viewrender.jsp.service.impl.ViewRenderServiceImpl;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private ServiceRegistration<?> viewRenderServiceRegistration;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		// 注册视图渲染服务
		ViewRenderService viewRenderService = new ViewRenderServiceImpl();
		Dictionary<String, String> dict = new Hashtable<String, String>();
		dict.put("bundle", context.getBundle().getSymbolicName());
		viewRenderServiceRegistration = context.registerService(
				ViewRenderService.class.getName(), viewRenderService, dict);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		// 取消注册视图渲染服务
		viewRenderServiceRegistration.unregister();

		Activator.context = null;
	}

}
