package com.quickwebframework.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class PluginFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			final FilterChain chain) throws IOException, ServletException {
		// 设置REQUEST的编码为UTF-8
		request.setCharacterEncoding("utf-8");

		List<Filter> filterList = new ArrayList<Filter>();

		// 过滤器迭代器，用于过滤器链
		final Iterator<Filter> iterator = filterList.iterator();

		FilterChain quickChain = new FilterChain() {
			public void doFilter(ServletRequest request,
					ServletResponse response) throws IOException,
					ServletException {
				if (!iterator.hasNext()) {
					chain.doFilter(request, response);
					return;
				}
				Filter filter = iterator.next();
				filter.doFilter(request, response, this);
			}
		};
		quickChain.doFilter(request, response);
	}
}
