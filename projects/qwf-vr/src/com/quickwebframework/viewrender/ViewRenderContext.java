package com.quickwebframework.viewrender;

import com.quickwebframework.framework.FrameworkContext;

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
	public void init() {
		super.addSimpleServiceStaticFieldLink(
				ViewRenderService.class.getName(), "viewRenderService");
	}

	@Override
	public void destory() {
	}
}
