package com.quickwebframework.mvc.spring;

import java.util.Comparator;
import java.util.Map;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class PluginPathMatcher implements PathMatcher {

	private PathMatcher pathMatcher = new AntPathMatcher();
	private String bundleName;

	public PluginPathMatcher(String bundleName) {
		this.bundleName = bundleName;
	}

	private String handlePattern(String pattern) {
		return "/" + bundleName + pattern;
	}

	@Override
	public String combine(String pattern1, String pattern2) {
		System.out.println("QuickwebFramework:combine:" + pattern1 + "  "
				+ pattern2);
		return pathMatcher.combine(pattern1, pattern2);
	}

	@Override
	public String extractPathWithinPattern(String pattern, String path) {
		pattern = handlePattern(pattern);
		return pathMatcher.extractPathWithinPattern(pattern, path);
	}

	@Override
	public Map<String, String> extractUriTemplateVariables(String pattern,
			String path) {
		pattern = handlePattern(pattern);
		return pathMatcher.extractUriTemplateVariables(pattern, path);
	}

	@Override
	public Comparator<String> getPatternComparator(String path) {
		return pathMatcher.getPatternComparator(path);
	}

	@Override
	public boolean isPattern(String path) {
		return pathMatcher.isPattern(path);
	}

	@Override
	public boolean match(String pattern, String path) {
		pattern = handlePattern(pattern);
		return pathMatcher.match(pattern, path);
	}

	@Override
	public boolean matchStart(String pattern, String path) {
		pattern = handlePattern(pattern);
		return pathMatcher.matchStart(pattern, path);
	}
}
