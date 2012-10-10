package qwf.test.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.core.FrameworkContext;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;

public class Activator implements BundleActivator {

	private static Log log = LogFactory.getLog(Activator.class);

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		Class<?> tmpClass = systemClassLoader.loadClass("javax.activation.DataSource");
		System.out.println("systemClassLoader -> tmpClass:" + tmpClass);
		
		ClassLoader currentClassLoader = Activator.class.getClassLoader();
		tmpClass = currentClassLoader.loadClass("javax.activation.DataSource");
		System.out.println("currentClassLoader -> tmpClass:" + tmpClass);
		
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();

		FrameworkContext.registerWebApp(this, bundleContext);
		//FrameworkContext.setRootUrlHandleServlet(new RootUrlHandleServlet());
		log.info("qwf.test.core插件已启动!");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		//FrameworkContext.setRootUrlHandleServlet(null);
		log.info("qwf.test.core插件已停止!");
	}

}
