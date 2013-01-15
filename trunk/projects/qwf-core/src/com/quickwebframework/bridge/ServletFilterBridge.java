package com.quickwebframework.bridge;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.DefaultRootServlet;
import com.quickwebframework.stereotype.FilterSetting;

public class ServletFilterBridge implements javax.servlet.Filter {

	private static Log log = LogFactory.getLog(ServletFilterBridge.class
			.getName());

	public class ArrayFilterChain implements FilterChain {
		private Filter[] filters;
		private int filterIndex = -1;
		private int filterCount = 0;

		public Filter lastFilter;

		public boolean isContinueFilterChain() {
			return filterIndex >= filterCount;
		}

		public ArrayFilterChain(Filter[] filters) {
			if (filters == null)
				return;
			this.filters = filters;
			filterCount = filters.length;
		}

		@Override
		public void doFilter(ServletRequest arg0, ServletResponse arg1)
				throws IOException, ServletException {
			if (filters == null)
				return;

			filterIndex++;

			// 如果过滤器已使用完
			if (filterIndex >= filterCount)
				return;

			lastFilter = filters[filterIndex];
			lastFilter.doFilter(arg0, arg1, this);
		}
	}

	// 执行过滤
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain filterChain) throws IOException, ServletException {
		// 判断是不是Http的Servlet请求
		if (!arg0.getScheme().equals("http")) {
			log.warn("ServletRequest的Scheme不是http!");
			return;
		}

		// 先执行过滤器
		ArrayFilterChain arrayFilterChain = new ArrayFilterChain(
				WebContext.getFilters());
		arrayFilterChain.doFilter(arg0, arg1);

		// 然后判断是否执行Servlet
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;

		// 是否应该发送给Servlet
		boolean shouldPostToServlet = false;

		if (arrayFilterChain.isContinueFilterChain())
			shouldPostToServlet = true;
		else {
			Filter lastFilter = arrayFilterChain.lastFilter;
			Class<?> lastFilterClass = lastFilter.getClass();
			FilterSetting lastFilterSetting = lastFilterClass
					.getAnnotation(FilterSetting.class);
			if (lastFilterSetting != null
					&& lastFilterSetting.returnToController()) {
				shouldPostToServlet = true;
			} else {
				shouldPostToServlet = false;
				log.info("过滤器链未全部执行完成，在执行完过滤器[" + arrayFilterChain.lastFilter
						+ "]后断开。");
			}
		}
		if (!shouldPostToServlet) {
			return;
		}

		// 根据URL分发到相应的Servlet
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String requestUriWithoutContextPath = requestUri.substring(contextPath
				.length());
		if (requestUriWithoutContextPath.isEmpty()) {
			requestUriWithoutContextPath = "/";
		}

		// 如果此路径有映射的Servlet，则交由此Servlet去处理
		Servlet pathServlet = WebContext
				.getServletByPath(requestUriWithoutContextPath);
		if (pathServlet != null) {
			pathServlet.service(request, response);
			return;
		}
		// 如果是根路径
		if ("/".equals(requestUriWithoutContextPath)) {
			DefaultRootServlet.getInstance().service(arg0, arg1);
			return;
		}
		String[] splitResult = StringUtils.split(requestUriWithoutContextPath,
				"/");
		// 如果根据/切分出来的字符串段数小于3，则不合法，返回404错误
		// 正确的插件的URL构成为： [插件名]/[类型名]/[方法名]
		if (splitResult.length < 3) {
			response.sendError(404);
			return;
		}
		String pluginName = splitResult[0];
		String typeName = splitResult[1];
		String methodName = StringUtils.join(splitResult, "/", 2,
				splitResult.length);

		System.out.println("pluginName:" + pluginName);
		System.out.println("typeName:" + typeName);
		System.out.println("methodName:" + methodName);

		// 根据typeName，找对应的类型的处理器
		// TODO:
	}

	// 过滤器初始化
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		WebContext.setFilterConfig(arg0);
	}

	@Override
	public void destroy() {
		// 移除所有的过滤器
		WebContext.removeAllListener();
	}
}
