package com.quickwebframework.mvc.spring.entity.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class PluginControllerInfo {

	// 控制器服务
	private Bundle bundle;
	// HTTP方法列表
	private List<String> httpMethodList;

	// 映射URL与处理器对象映射
	private Map<String, Object> mappingUrlHandlerMap;
	// 处理器与适配器映射
	private Map<Object, AnnotationMethodHandlerAdapter> handlerAdapterMap;

	public PluginControllerInfo(Bundle bundle) {
		this.bundle = bundle;
		httpMethodList = new ArrayList<String>();
		mappingUrlHandlerMap = new HashMap<String, Object>();
		handlerAdapterMap = new HashMap<Object, AnnotationMethodHandlerAdapter>();
	}

	public Bundle getBundle() {
		return bundle;
	}

	public List<String> getHttpMethodList() {
		return httpMethodList;
	}

	/**
	 * 映射URL与处理器对象映射
	 * 
	 * @return
	 */
	public Map<String, Object> getMappingUrlHandlerMap() {
		return mappingUrlHandlerMap;
	}

	/**
	 * 得到处理器与适配器映射
	 * 
	 * @return
	 */
	public Map<Object, AnnotationMethodHandlerAdapter> getHandlerAdapterMap() {
		return handlerAdapterMap;
	}
}
