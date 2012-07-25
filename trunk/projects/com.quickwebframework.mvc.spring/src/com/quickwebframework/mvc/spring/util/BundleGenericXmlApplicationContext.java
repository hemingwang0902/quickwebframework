package com.quickwebframework.mvc.spring.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;

public class BundleGenericXmlApplicationContext extends
		GenericApplicationContext {

	private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
			this);
	private Bundle bundle;

	public BundleGenericXmlApplicationContext(Bundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * Set namespaceHandlerResolver
	 * 
	 * @param namespaceHandlerResolver
	 */
	public void setNamespaceHandlerResolver(
			NamespaceHandlerResolver namespaceHandlerResolver) {
		reader.setNamespaceHandlerResolver(namespaceHandlerResolver);
	}

	/**
	 * Set whether to use XML validation. Default is <code>true</code>.
	 */
	public void setValidating(boolean validating) {
		this.reader.setValidating(validating);
	}

	/**
	 * Load bean definitions from the given XML resources.
	 * 
	 * @param resources
	 *            one or more resources to load from
	 */
	public void load(Resource... resources) {
		this.reader.loadBeanDefinitions(resources);
	}

	/**
	 * Load bean definitions from the given XML resources.
	 * 
	 * @param resourceLocations
	 *            one or more resource locations to load from
	 */
	public void load(String... resourceLocations) {
		this.reader.loadBeanDefinitions(resourceLocations);
	}

	/**
	 * Load bean definitions from the given XML resources.
	 * 
	 * @param relativeClass
	 *            class whose package will be used as a prefix when loading each
	 *            specified resource name
	 * @param resourceNames
	 *            relatively-qualified names of resources to load
	 */
	public void load(Class<?> relativeClass, String... resourceNames) {
		Resource[] resources = new Resource[resourceNames.length];
		for (int i = 0; i < resourceNames.length; i++) {
			resources[i] = new ClassPathResource(resourceNames[i],
					relativeClass);
		}
		this.load(resources);
	}

	@Override
	public Resource[] getResources(String path) {

		String centerPath = null;

		// 如果是寻找所有的classpath
		if (path.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
			centerPath = path
					.substring(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
							.length());

			if (centerPath.startsWith("*")) {
				centerPath = "/";
			} else {
				centerPath = centerPath.substring(0, centerPath.indexOf("*"));
			}
		} else if (path
				.startsWith(ResourcePatternResolver.CLASSPATH_URL_PREFIX)) {
			throw new RuntimeException("暂未实现的path:" + path);
		} else {
			throw new RuntimeException("未知的path:" + path);
		}

		List<Resource> resourceList = new ArrayList<Resource>();
		// 搜索Class文件
		Enumeration<?> enume = bundle.findEntries(centerPath, "*.class", true);
		if (enume == null)
			return null;
		while (enume.hasMoreElements()) {
			resourceList.add(new UrlResource((URL) enume.nextElement()));
		}
		return resourceList.toArray(new Resource[0]);
	}
}
