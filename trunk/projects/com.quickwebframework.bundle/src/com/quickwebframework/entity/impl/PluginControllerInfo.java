package com.quickwebframework.entity.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.quickwebframework.service.PluginService;

public class PluginControllerInfo {

	// 控制器服务
	private PluginService controllerService;
	// 映射URL与处理器对象映射
	private Map<String, Object> mappingUrlHandlerMap;
	// 处理器与适配器映射
	private Map<Object, AnnotationMethodHandlerAdapter> handlerAdapterMap;
	// 过滤器列表
	private List<Filter> filterList;
	// 线程列表
	private List<Thread> threadList;

	public PluginControllerInfo(PluginService controllerService) {
		this.controllerService = controllerService;
		mappingUrlHandlerMap = new HashMap<String, Object>();
		handlerAdapterMap = new HashMap<Object, AnnotationMethodHandlerAdapter>();
		filterList = new ArrayList<Filter>();
		threadList = new ArrayList<Thread>();
	}

	public PluginService getControllerService() {
		return controllerService;
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

	/**
	 * 得到过滤器列表
	 * 
	 * @return
	 */
	public List<Filter> getFilterList() {
		return filterList;
	}

	public List<Thread> getThreadList() {
		return threadList;
	}
}
