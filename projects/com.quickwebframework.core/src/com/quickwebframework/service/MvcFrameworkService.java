package com.quickwebframework.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.entity.MvcModelAndView;

public interface MvcFrameworkService {

	/**
	 * 添加一个Bundle到适配器
	 * 
	 * @param webAppService
	 *            Web App服务
	 * @return
	 */
	public boolean addWebApp(WebAppService webAppService);

	/**
	 * 从适配器中移除一个Bundle
	 * 
	 * @param webAppService
	 *            Web App服务
	 * @return
	 */
	public boolean removeWebApp(WebAppService webAppService);

	/**
	 * 根据Bundle名称得到WebAppService对象
	 * 
	 * @param bundleName
	 * @return
	 */
	public WebAppService getWebAppService(String bundleName);

	/**
	 * 处理HTTP请求
	 * 
	 * @param request
	 *            请求
	 * @param response
	 *            响应
	 * @param bundleName
	 *            Bundle名称
	 * @param methodName
	 *            方法名称
	 * @return
	 */
	public MvcModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, String bundleName, String methodName);
}
