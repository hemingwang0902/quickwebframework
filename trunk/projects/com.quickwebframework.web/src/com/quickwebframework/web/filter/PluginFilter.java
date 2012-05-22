package com.quickwebframework.web.filter;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.quickwebframework.web.listener.QuickWebFrameworkLoaderListener;

public class PluginFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		Object dispatcherServletObject = QuickWebFrameworkLoaderListener
				.getDispatcherServletObject();
		if (dispatcherServletObject == null) {
			arg2.doFilter(arg0, arg1);
			return;
		}

		// dispatcherServlet的类
		Class<?> dispatcherServletClazz = dispatcherServletObject.getClass();

		boolean isContinueFilter = true;
		try {
			// 找到对应的处理方法
			Method httpMethod = dispatcherServletClazz.getMethod("doFilter",
					Object.class, Object.class, Object.class);
			if (httpMethod != null) {
				isContinueFilter = (Boolean) httpMethod.invoke(
						dispatcherServletObject, arg0, arg1, arg2);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		// 如果要继续执行其他过滤器
		if (isContinueFilter) {
			arg2.doFilter(arg0, arg1);
		}
	}
}
