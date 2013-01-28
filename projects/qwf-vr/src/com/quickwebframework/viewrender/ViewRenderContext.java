package com.quickwebframework.viewrender;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;

import com.quickwebframework.framework.FrameworkContext;
import com.quickwebframework.viewrender.impl.Activator;

public class ViewRenderContext extends FrameworkContext {
	private static ViewRenderContext instance;

	public static ViewRenderContext getInstance() {
		if (instance == null)
			instance = new ViewRenderContext();
		return instance;
	}

	// 视图渲染服务
	private static ViewRenderService viewRenderService;

	public static ViewRenderService getViewRenderService() {
		return viewRenderService;
	}

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getContext();
	}

	@Override
	protected void init(int arg0) {
		super.addSimpleServiceStaticFieldLink(
				ViewRenderService.class.getName(), "viewRenderService");
	}

	@Override
	protected void destory(int arg0) {
	}

	@Override
	protected void bundleChanged(BundleEvent event) {

	}

	@Override
	protected void serviceChanged(ServiceEvent event) {

	}
}
