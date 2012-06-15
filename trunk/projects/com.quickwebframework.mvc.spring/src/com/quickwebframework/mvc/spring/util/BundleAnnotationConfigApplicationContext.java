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

	public static List<String> getAllClassFilePathListInBundle(Bundle bundle,
			String startPath) {
		List<String> rtnList = new ArrayList<String>();
		Enumeration<?> urlEnum = bundle.getEntryPaths(startPath);
		while (urlEnum.hasMoreElements()) {
			String url = (String) urlEnum.nextElement();

			// 如果是class文件
			if (url.endsWith(".class")) {
				rtnList.add(url);
			}
			// 如果是目录
			if (url.endsWith("/")) {
				rtnList.addAll(getAllClassFilePathListInBundle(bundle, url));
			}
		}
		return rtnList;
	}

	@Override
	public Resource[] getResources(String path) {
		List<Resource> resourceList = new ArrayList<Resource>();

		// 如果是搜索所有包的所有class文件
		if ("classpath*:*/**/*.class".contains(path)) {
			List<String> filePathList = getAllClassFilePathListInBundle(bundle,
					"");
			for (String filePath : filePathList) {
				URL url = bundle.getEntry(filePath);
				resourceList.add(new UrlResource(url));
			}
			return resourceList.toArray(new Resource[0]);
		} else {
			throw new RuntimeException("还未遇到此ClassPath! + " + path);
		}
	}
}
