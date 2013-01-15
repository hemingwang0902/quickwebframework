package com.quickwebframework.mvc;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

public interface MvcFrameworkService {

	/**
	 * 添加一个Bundle到适配器
	 * 
	 * @param bundle
	 * 
	 * @return
	 */
	public boolean addBundle(Bundle bundle);

	/**
	 * 从适配器中移除一个Bundle
	 * 
	 * @param bundle
	 *            Web App服务
	 * @return
	 */
	public boolean removeBundle(Bundle bundle);

	/**
	 * MVC框架中是否已包含指定的Bundle
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean containsBundle(Bundle bundle);

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

	/**
	 * 得到插件名称与Http方法信息列表的Map
	 * 
	 * @return
	 */
	public Map<String, List<HttpMethodInfo>> getBundleHttpMethodInfoListMap();
}
