package com.quickwebframework.mvc.spring.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

public class PluginUrlPathHelper extends UrlPathHelper {
	public static final String ARG_BUNDLE_NAME = "com.quickwebframework.util.ARG_BUNDLE_NAME";
	public static final String ARG_METHOD_NAME = "com.quickwebframework.util.ARG_METHOD_NAME";

	public String getLookupPathForRequest(HttpServletRequest request) {
		return getRequestUri(request);
	}

	public String getRequestUri(HttpServletRequest request) {
		String result = "/" + request.getAttribute(ARG_BUNDLE_NAME) + "/"
				+ request.getAttribute(ARG_METHOD_NAME);
		return result;
	}
}
