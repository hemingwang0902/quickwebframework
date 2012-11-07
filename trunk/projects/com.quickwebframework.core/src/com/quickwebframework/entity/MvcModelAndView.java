package com.quickwebframework.entity;

import java.util.Map;

import org.osgi.framework.Bundle;

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

	// Bundle
	private Bundle bundle;

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
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

	public MvcModelAndView(String viewName, Map<?, ?> model, Bundle bundle) {
		init(viewName, model, bundle);
	}

	private void init(String viewName, Map<?, ?> model, Bundle bundle) {
		this.viewName = viewName;
		this.model = model;
		this.bundle = bundle;
	}
}
