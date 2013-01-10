package com.quickwebframework.bridge;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.quickwebframework.framework.WebContext;
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
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		ArrayFilterChain arrayFilterChain = new ArrayFilterChain(
				WebContext.getFilters());
		arrayFilterChain.doFilter(request, response);
		if (arrayFilterChain.isContinueFilterChain())
			filterChain.doFilter(request, response);
		else {
			Filter lastFilter = arrayFilterChain.lastFilter;
			Class<?> lastFilterClass = lastFilter.getClass();
			FilterSetting lastFilterSetting = lastFilterClass
					.getAnnotation(FilterSetting.class);
			if (lastFilterSetting != null
					&& lastFilterSetting.returnToController()) {
				filterChain.doFilter(request, response);
			} else {
				log.info("过滤器链未全部执行完成，在执行完过滤器[" + arrayFilterChain.lastFilter
						+ "]后断开。");
			}
		}
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