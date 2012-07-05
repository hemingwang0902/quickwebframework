package com.quickwebframework.mvc.spring.entity.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.quickwebframework.service.WebAppService;

public class PluginControllerInfo {

	// 控制器服务
	private WebAppService webAppService;
	// 方法名称列表
	private List<String> methodNameList;
	// 映射URL与处理器对象映射
	private Map<String, Object> mappingUrlHandlerMap;
	// 处理器与适配器映射
	private Map<Object, AnnotationMethodHandlerAdapter> handlerAdapterMap;

	public PluginControllerInfo(WebAppService webAppService) {
		this.webAppService = webAppService;
		methodNameList = new ArrayList<String>();
		mappingUrlHandlerMap = new HashMap<String, Object>();
		handlerAdapterMap = new HashMap<Object, AnnotationMethodHandlerAdapter>();
	}

	public WebAppService getWebAppService() {
		return webAppService;
	}

	public List<String> getMethodNameList() {
		return methodNameList;
	}

	public void setMethodNameList(List<String> methodNameList) {
		this.methodNameList = methodNameList;
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
