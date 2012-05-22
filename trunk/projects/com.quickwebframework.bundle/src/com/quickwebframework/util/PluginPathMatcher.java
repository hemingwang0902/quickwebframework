package com.quickwebframework.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.PathMatcher;

public class PluginPathMatcher implements PathMatcher {

	private String bundleName;

	public PluginPathMatcher(String bundleName) {
		this.bundleName = bundleName;
	}

	@Override
	public String combine(String arg0, String arg1) {
		System.out.println("QuickwebFramework:combine:" + arg0 + "  " + arg1);
		return null;
	}

	@Override
	public String extractPathWithinPattern(String arg0, String arg1) {
		System.out.println("QuickwebFramework:extractPathWithinPattern:" + arg0
				+ "  " + arg1);
		return null;
	}

	@Override
	public Map<String, String> extractUriTemplateVariables(String pattern,
			String path) {
		// 从URL中提取参数
		return new HashMap<String, String>();
	}

	@Override
	public Comparator<String> getPatternComparator(String arg0) {
		return new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
	}

	@Override
	public boolean isPattern(String arg0) {
		System.out.println("QuickwebFramework:isPattern:" + arg0);
		return false;
	}

	@Override
	public boolean match(String arg0, String arg1) {
		arg0 = "/" + bundleName + arg0;
		return arg0.equals(arg1);
	}

	@Override
	public boolean matchStart(String arg0, String arg1) {
		System.out
				.println("QuickwebFramework:matchStart:" + arg0 + "  " + arg1);
		return false;
	}
}
