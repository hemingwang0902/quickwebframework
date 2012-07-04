package com.quickwebframework.mvc.spring.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class BundleAnnotationConfigApplicationContext extends
		AnnotationConfigApplicationContext {

	private Bundle bundle;

	public BundleAnnotationConfigApplicationContext(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public Resource[] getResources(String path) {
		List<Resource> resourceList = new ArrayList<Resource>();

		// 如果是搜索所有包的所有class文件
		if ("classpath*:*/**/*.class".contains(path)) {
			// 搜索Class文件
			Enumeration<?> enume = bundle.findEntries("", "*.class", true);
			while (enume.hasMoreElements()) {
				resourceList.add(new UrlResource((URL) enume.nextElement()));
			}
			return resourceList.toArray(new Resource[0]);
		} else {
			throw new RuntimeException("还未遇到此ClassPath! + " + path);
		}
	}
}
