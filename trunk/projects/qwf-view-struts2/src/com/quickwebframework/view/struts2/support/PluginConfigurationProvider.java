package com.quickwebframework.view.struts2.support;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.struts2.config.StrutsXmlConfigurationProvider;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class PluginConfigurationProvider extends XmlConfigurationProvider {

	private static final Logger LOG = LoggerFactory
			.getLogger(StrutsXmlConfigurationProvider.class);
	private String filename;
	private String reloadKey;
	private ServletContext servletContext;

	/**
	 * Constructs the configuration provider
	 * 
	 * @param errorIfMissing
	 *            If we should throw an exception if the file can't be found
	 */
	public PluginConfigurationProvider() {
		this("struts.xml", null);
	}

	/**
	 * Constructs the configuration provider
	 * 
	 * @param filename
	 *            The filename to look for
	 * @param errorIfMissing
	 *            If we should throw an exception if the file can't be found
	 * @param ctx
	 *            Our ServletContext
	 */
	public PluginConfigurationProvider(String filename, ServletContext ctx) {
		super(filename, false);
		this.servletContext = Activator.getServletContext();
		this.filename = filename;
		reloadKey = "configurationReload-" + filename;
		Map<String, String> dtdMappings = new HashMap<String, String>(
				getDtdMappings());
		dtdMappings
				.put("-//Apache Software Foundation//DTD Struts Configuration 2.0//EN",
						"struts-2.0.dtd");
		dtdMappings
				.put("-//Apache Software Foundation//DTD Struts Configuration 2.1//EN",
						"struts-2.1.dtd");
		dtdMappings
				.put("-//Apache Software Foundation//DTD Struts Configuration 2.1.7//EN",
						"struts-2.1.7.dtd");
		dtdMappings
				.put("-//Apache Software Foundation//DTD Struts Configuration 2.3//EN",
						"struts-2.3.dtd");
		setDtdMappings(dtdMappings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.opensymphony.xwork2.config.providers.XmlConfigurationProvider#register
	 * (com.opensymphony.xwork2.inject.ContainerBuilder, java.util.Properties)
	 */
	@Override
	public void register(ContainerBuilder containerBuilder,
			LocatableProperties props) throws ConfigurationException {
		if (servletContext != null
				&& !containerBuilder.contains(ServletContext.class)) {
			containerBuilder.factory(ServletContext.class,
					new Factory<ServletContext>() {
						public ServletContext create(Context context)
								throws Exception {
							return servletContext;
						}
					});
		}
		super.register(containerBuilder, props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.opensymphony.xwork2.config.providers.XmlConfigurationProvider#init
	 * (com.opensymphony.xwork2.config.Configuration)
	 */
	@Override
	public void loadPackages() {
		ActionContext ctx = ActionContext.getContext();
		ctx.put(reloadKey, Boolean.TRUE);
		super.loadPackages();
	}

	/**
	 * Look for the configuration file on the classpath and in the file system
	 * 
	 * @param fileName
	 *            The file name to retrieve
	 * @see com.opensymphony.xwork2.config.providers.XmlConfigurationProvider#getConfigurationUrls
	 */
	@Override
	protected Iterator<URL> getConfigurationUrls(String fileName)
			throws IOException {
		URL url = null;
		url = findInBundle(fileName);
		if (url == null) {
			return super.getConfigurationUrls(fileName);
		} else {
			List<URL> list = new ArrayList<URL>();
			list.add(url);
			return list.iterator();
		}
	}

	protected URL findInBundle(String fileName) throws IOException {
		URL url = null;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Trying to load resource " + fileName
					+ " from OSGi bundle.");
		}
		return url;
	}

	/**
	 * Overrides needs reload to ensure it is only checked once per request
	 */
	@Override
	public boolean needsReload() {
		ActionContext ctx = ActionContext.getContext();
		if (ctx != null) {
			return ctx.get(reloadKey) == null && super.needsReload();
		} else {
			return super.needsReload();
		}

	}

	public String toString() {
		return ("Struts XML configuration provider (" + filename + ")");
	}
}