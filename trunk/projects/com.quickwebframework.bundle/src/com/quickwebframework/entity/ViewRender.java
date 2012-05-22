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
	 * @param pluginInfo
	 * @param viewName
	 * @param request
	 * @param response
	 */
	public void renderView(String viewName, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * 得到插件名称与路径分隔字符串
	 * 
	 * @return
	 */
	public String getPluginNameAndPathSplitString();

	/**
	 * 获取视图名称后缀
	 * 
	 * @return
	 */
	public String getViewNameSuffix();
}
