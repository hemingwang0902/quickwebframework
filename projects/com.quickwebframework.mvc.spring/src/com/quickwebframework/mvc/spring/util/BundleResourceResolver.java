package com.quickwebframework.mvc.spring.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 插件资源解析器
 * 
 * @author aaa
 * 
 */
public class BundleResourceResolver {

	private Bundle bundle;

	public BundleResourceResolver(Bundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * 得到资源
	 * 
	 * @param path
	 * @return
	 */
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
