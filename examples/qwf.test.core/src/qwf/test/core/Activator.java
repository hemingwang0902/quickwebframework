package qwf.test.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;

public class Activator implements BundleActivator {

	private static Log log = LogFactory.getLog(Activator.class);

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		log.info("qwf.test.core插件已启动!");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;

		log.info("qwf.test.core插件已停止!");
	}

}
