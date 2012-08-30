package qwf.test.calculator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.quickwebframework.core.FrameworkContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		//FrameworkContext.registerWebApp(this, bundleContext);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
