package com.quickwebframework.entity;

/**
 * 提供HTTP方法信息类
 * 
 * @author aaa
 * 
 */
public class HttpMethodInfo {
	// GET,POST
	private String httpMethod;
	// 映射的URL
	private String mappingUrl;

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getMappingUrl() {
		return mappingUrl;
	}

	public void setMappingUrl(String mappingUrl) {
		this.mappingUrl = mappingUrl;
	}
}
