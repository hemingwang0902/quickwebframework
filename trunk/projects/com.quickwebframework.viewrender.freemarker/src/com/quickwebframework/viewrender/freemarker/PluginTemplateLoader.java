package com.quickwebframework.viewrender.freemarker;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.service.PluginService;

import freemarker.cache.TemplateLoader;

public class PluginTemplateLoader implements TemplateLoader {

	// Bundle上下文
	private BundleContext bundleContext;
	// 分隔字符串
	private String pluginNameAndPathSplitString;

	public PluginTemplateLoader(BundleContext bundleContext,
			String pluginNameAndPathSplitString) {
		this.bundleContext = bundleContext;
		this.pluginNameAndPathSplitString = pluginNameAndPathSplitString;
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

		PluginService pluginService = null;
		try {
			ServiceReference[] serviceReferences = bundleContext
					.getServiceReferences(PluginService.class.getName(), null);
			for (ServiceReference serviceReference : serviceReferences) {
				if (pluginName.equals(serviceReference.getBundle()
						.getSymbolicName())) {
					pluginService = (PluginService) bundleContext
							.getService(serviceReference);
					break;
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		if (pluginService == null) {
			throw new RuntimeException(String.format(
					"Can't found plugin[%s],template [%s] load failure.",
					pluginName, name));
		}

		return new PluginTemplateSource(pluginService, path, pluginService
				.getBundle().getLastModified(), pluginNameAndPathSplitString);
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
			inputStream = pluginTemplateSource.controllerService
					.getClassLoader().getResourceAsStream(
							pluginTemplateSource.path);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		if (inputStream == null) {
			throw new IOException("Template file ["
					+ pluginTemplateSource.path
					+ "] not exist in ["
					+ pluginTemplateSource.controllerService.getBundle()
							.getSymbolicName() + "] plugin.");
		}
		java.io.BufferedReader reader = new java.io.BufferedReader(
				new java.io.InputStreamReader(inputStream, encoding));
		return reader;
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
	}

	private static class PluginTemplateSource {
		private final PluginService controllerService;
		private final String path;
		private final long lastModified;
		private final String pluginNameAndPathSplitString;

		public PluginTemplateSource(PluginService controllerService,
				String path, long lastModified,
				String pluginNameAndPathSplitString) {
			this.controllerService = controllerService;
			this.path = path;
			this.lastModified = lastModified;
			this.pluginNameAndPathSplitString = pluginNameAndPathSplitString;
		}

		public boolean equals(Object obj) {
			if (obj instanceof PluginTemplateSource) {
				PluginTemplateSource pluginTemplateSource = (PluginTemplateSource) obj;
				return controllerService
						.equals(pluginTemplateSource.controllerService)
						&& path.equals(pluginTemplateSource.path)
						&& lastModified == pluginTemplateSource.lastModified;
			}
			return false;
		}

		public int hashCode() {
			return (controllerService.getBundle().getSymbolicName()
					+ pluginNameAndPathSplitString + path).hashCode();
		}
	}
}
