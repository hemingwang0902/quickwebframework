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

public class CommonFilter implements Filter {

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
		Filter filter = (Filter) dispatcherServletObject;
		filter.doFilter(arg0, arg1, arg2);
	}
}
