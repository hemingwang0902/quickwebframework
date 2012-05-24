package com.quickwebframework.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 视图渲染器接口
 * 
 * @author aaa
 * 
 */
public interface ViewRender {
	/**
	 * 渲染视图
	 * 
	 * @param bundleName
	 *            插件名称
	 * @param viewName
	 *            视图名称
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 */
	public void renderView(String bundleName, String viewName,
			HttpServletRequest request, HttpServletResponse response);
}
