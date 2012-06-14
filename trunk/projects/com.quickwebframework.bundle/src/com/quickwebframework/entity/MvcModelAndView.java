package com.quickwebframework.entity;

import java.util.Map;

import com.quickwebframework.service.WebAppService;

//MVC的模型与视图
public class MvcModelAndView {
	// 视图名称
	private String viewName;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	// 数据
	private Map<?, ?> model;

	public Map<?, ?> getModel() {
		return model;
	}

	// WebAppService
	private WebAppService webAppService;

	public WebAppService getWebAppService() {
		return webAppService;
	}

	public void setWebAppService(WebAppService webAppService) {
		this.webAppService = webAppService;
	}

	public void setModel(Map<?, ?> model) {
		this.model = model;
	}

	// 构造函数
	public MvcModelAndView() {
	}

	public MvcModelAndView(String viewName) {
		init(viewName, null, null);
	}

	public MvcModelAndView(String viewName, Map<?, ?> model) {
		init(viewName, model, null);
	}

	public MvcModelAndView(String viewName, Map<?, ?> model,
			WebAppService webAppService) {
		init(viewName, model, webAppService);
	}

	private void init(String viewName, Map<?, ?> model,
			WebAppService webAppService) {
		this.viewName = viewName;
		this.model = model;
		this.webAppService = webAppService;
	}
}
