package com.quickwebframework.service;

import javax.servlet.http.HttpServlet;

import com.quickwebframework.entity.HandlerExceptionResolver;

/**
 * WEB设置服务
 * 
 * @author aaa
 * 
 */
public interface WebSettingService {
	/**
	 * 得到根跳转URL
	 * 
	 * @return
	 */
	public HttpServlet getRootUrlHandleServlet();

	/**
	 * 得到URL未找到处理Servlet
	 * 
	 * @return
	 */
	public HttpServlet getUrlNotFoundHandleServlet();

	/**
	 * 得到处理器异常解决器
	 * 
	 * @return
	 */
	public HandlerExceptionResolver getHandlerExceptionResolver();
}
