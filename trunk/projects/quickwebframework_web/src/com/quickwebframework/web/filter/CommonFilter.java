package com.quickwebframework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.quickwebframework.web.listener.QuickWebFrameworkLoaderListener;

public class CommonFilter implements Filter {
	// QuickwebFramework的过滤器配置状态
	public static final String QUICKWEBFRAMEWORK_STATE_FILTERCONFIG = "com.quickwebframework.state.FILTERCONFIG";

	private Filter getFrameworkBridgeFilter() {
		Object frameworkBridgeObject = QuickWebFrameworkLoaderListener
				.getFrameworkBridgeObject();
		if (frameworkBridgeObject == null) {
			return null;
		}
		return (Filter) frameworkBridgeObject;
	}

	@Override
	public void destroy() {
		Filter frameworkBridgeFilter = getFrameworkBridgeFilter();
		if (frameworkBridgeFilter != null)
			frameworkBridgeFilter.destroy();
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		Filter frameworkBridgeFilter = getFrameworkBridgeFilter();
		if (frameworkBridgeFilter == null)
			arg0.getServletContext().setAttribute(
					QUICKWEBFRAMEWORK_STATE_FILTERCONFIG, arg0);
		else
			frameworkBridgeFilter.init(arg0);
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		Filter frameworkBridgeFilter = getFrameworkBridgeFilter();
		if (frameworkBridgeFilter == null)
			arg2.doFilter(arg0, arg1);
		else
			frameworkBridgeFilter.doFilter(arg0, arg1, arg2);
	}
}
