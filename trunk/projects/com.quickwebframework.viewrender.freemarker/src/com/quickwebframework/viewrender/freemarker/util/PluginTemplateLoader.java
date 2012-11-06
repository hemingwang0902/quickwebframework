package com.quickwebframework.viewrender.freemarker.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.osgi.framework.Bundle;

import com.quickwebframework.framework.FrameworkContext;

import freemarker.cache.TemplateLoader;

public class PluginTemplateLoader implements TemplateLoader {

	// 插件名称与路径分隔符
	private String pluginNameAndPathSplitString = ":";

	public String getPluginNameAndPathSplitString() {
		return pluginNameAndPathSplitString;
	}

	public void setPluginNameAndPathSplitString(
			String pluginNameAndPathSplitString) {
		this.pluginNameAndPathSplitString = pluginNameAndPathSplitString;
	}

	// 视图名称前缀
	private String viewNamePrefix = "";

	public void setViewNamePrefix(String viewNamePrefix) {
		this.viewNamePrefix = viewNamePrefix;
	}

	// 视图名称后缀
	private String viewNameSuffix = ".ftl";

	public void setViewNameSuffix(String viewNameSuffix) {
		this.viewNameSuffix = viewNameSuffix;
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		String[] tmpArray = name.split(pluginNameAndPathSplitString);
		if (tmpArray.length < 2) {
			throw new IOException("不符合规则：“[插件名]" + pluginNameAndPathSplitString
					+ "[路径]”");
		}
		String pluginName = tmpArray[0];
		String path = tmpArray[1];
		// 对视图名称进行处理(添加前后缀)
		path = viewNamePrefix + path + viewNameSuffix;

		Bundle bundle = FrameworkContext.getBundleByName(pluginName);

		if (bundle == null) {
			throw new RuntimeException(String.format(
					"Can't found plugin[%s],template [%s] load failure.",
					pluginName, name));
		}

		return new PluginTemplateSource(bundle, path, bundle.getLastModified(),
				pluginNameAndPathSplitString);
	}

	@Override
	public long getLastModified(Object templateSource) {
		return ((PluginTemplateSource) templateSource).lastModified;
	}

	@Override
	public Reader getReader(Object templateSource, String encoding)
			throws IOException {
		PluginTemplateSource pluginTemplateSource = (PluginTemplateSource) templateSource;

		InputStream inputStream = null;
		try {
			URL resourceURL = pluginTemplateSource.bundle
					.getResource(pluginTemplateSource.path);
			if (resourceURL != null)
				inputStream = resourceURL.openStream();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		if (inputStream == null) {
			throw new IOException("Template file [" + pluginTemplateSource.path
					+ "] not exist in ["
					+ pluginTemplateSource.bundle.getSymbolicName()
					+ "] plugin.");
		}
		java.io.BufferedReader reader = new java.io.BufferedReader(
				new java.io.InputStreamReader(inputStream, encoding));
		return reader;
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
	}

	private static class PluginTemplateSource {
		private final Bundle bundle;
		private final String path;
		private final long lastModified;
		private final String pluginNameAndPathSplitString;

		public PluginTemplateSource(Bundle bundle, String path,
				long lastModified, String pluginNameAndPathSplitString) {
			this.bundle = bundle;
			this.path = path;
			this.lastModified = lastModified;
			this.pluginNameAndPathSplitString = pluginNameAndPathSplitString;
		}

		public boolean equals(Object obj) {
			if (obj instanceof PluginTemplateSource) {
				PluginTemplateSource pluginTemplateSource = (PluginTemplateSource) obj;
				return bundle.equals(pluginTemplateSource.bundle)
						&& path.equals(pluginTemplateSource.path)
						&& lastModified == pluginTemplateSource.lastModified;
			}
			return false;
		}

		public int hashCode() {
			return (bundle.getSymbolicName() + pluginNameAndPathSplitString + path)
					.hashCode();
		}
	}
}
