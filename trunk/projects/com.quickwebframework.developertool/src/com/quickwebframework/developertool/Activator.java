package com.quickwebframework.developertool;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.core.PluginServletContext;
import com.quickwebframework.service.core.PluginService;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ServletContext getServletContext() {
		ServiceReference servletContextServiceReference = null;
		try {
			ServiceReference[] allServiceReferences = context
					.getAllServiceReferences(null, null);
			for (ServiceReference sr : allServiceReferences) {
				if (sr.toString().contains(ServletContext.class.getName())) {
					servletContextServiceReference = sr;
					break;
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		if (servletContextServiceReference == null) {
			throw new RuntimeException(
					"在OSGi服务中未找到javax.servlet.ServletContext服务！");
		}
		Object obj = context.getService(servletContextServiceReference);
		return new PluginServletContext(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// 得到PluginService
		ServiceReference serviceReference = bundleContext
				.getServiceReference(PluginService.class.getName());
		if (serviceReference == null) {
			throw new RuntimeException("未找到PluginService!");
		}
		PluginService pluginService = (PluginService) bundleContext
				.getService(serviceReference);

		// 得到ServletContext
		ServletContext servletContext = getServletContext();

		// 初始化管理线程
		BundleAutoManageThread thread = new BundleAutoManageThread(
				bundleContext, servletContext.getRealPath("WEB-INF/plugins"));

		pluginService.addThread(context.getBundle(), thread);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
