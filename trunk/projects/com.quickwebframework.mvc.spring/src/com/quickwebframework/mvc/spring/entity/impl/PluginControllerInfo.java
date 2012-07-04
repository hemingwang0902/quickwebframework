package com.quickwebframework.mvc.spring.entity.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.quickwebframework.service.WebAppService;

public class PluginControllerInfo {

	// 控制器服务
	private WebAppService webAppService;
	// 映射URL与处理器对象映射
	private Map<String, Object> mappingUrlHandlerMap;
	// 处理器与适配器映射
	private Map<Object, AnnotationMethodHandlerAdapter> handlerAdapterMap;

	public PluginControllerInfo(WebAppService webAppService) {
		this.webAppService = webAppService;
		mappingUrlHandlerMap = new HashMap<String, Object>();
		handlerAdapterMap = new HashMap<Object, AnnotationMethodHandlerAdapter>();
	}

	public WebAppService getWebAppService() {
		return webAppService;
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
