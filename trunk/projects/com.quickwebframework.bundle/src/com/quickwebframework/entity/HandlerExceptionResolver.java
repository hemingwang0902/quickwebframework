package com.quickwebframework.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理器异常解决器
 * 
 * @author aaa
 * 
 */
public interface HandlerExceptionResolver {
	/**
	 * 解决异常
	 * 
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @param handler
	 *            发生异常的处理器
	 * @param ex
	 *            异常
	 * @return
	 */
	public String resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception;
}
