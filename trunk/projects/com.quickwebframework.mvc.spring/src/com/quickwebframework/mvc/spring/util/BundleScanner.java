package com.quickwebframework.mvc.spring.util;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.UrlResource;

public class BundleScanner {

	private ApplicationContext doScan(Bundle bundle,
			ClassLoader bundleClassLoader) {
		// 检查插件的根路径下面是否有applicationContext.xml文件
		URL applicationContextUrl = bundle
				.getResource("applicationContext.xml");

		ApplicationContext applicationContext = null;

		// 如果有，则让Spring加载这个xml文件
		if (applicationContextUrl != null) {
			try {
				BundleGenericXmlApplicationContext bundleGenericXmlApplicationContext = new BundleGenericXmlApplicationContext(
						bundle);
				bundleGenericXmlApplicationContext
						.setNamespaceHandlerResolver(new DefaultNamespaceHandlerResolver(
								this.getClass().getClassLoader()));
				bundleGenericXmlApplicationContext
						.setClassLoader(bundleClassLoader);
				bundleGenericXmlApplicationContext.load(new UrlResource(
						applicationContextUrl));
				bundleGenericXmlApplicationContext.refresh();
				bundleGenericXmlApplicationContext.start();
				applicationContext = bundleGenericXmlApplicationContext;
			} catch (Exception ex) {
				throw new RuntimeException("Spring加载插件["
						+ bundle.getSymbolicName()
						+ "]中的applicationContext.xml文件时出错异常！", ex);
			}
		}
		// 否则扫描插件的所有文件
		else {
			// 初始化AnnotationConfigApplicationContext
			BundleAnnotationConfigApplicationContext bundleAnnotationConfigApplicationContext = new BundleAnnotationConfigApplicationContext(
					bundle);
			try {
				// 开始Spring扫描
				bundleAnnotationConfigApplicationContext
						.setClassLoader(bundleClassLoader);
				bundleAnnotationConfigApplicationContext.scan("*");
				bundleAnnotationConfigApplicationContext.refresh();
				bundleAnnotationConfigApplicationContext.start();
				applicationContext = bundleAnnotationConfigApplicationContext;
			} catch (Exception ex) {
				throw new RuntimeException("用Spring扫描插件["
						+ bundle.getSymbolicName() + "]时出错异常！", ex);
			}
		}
		return applicationContext;
	}

	public ApplicationContext scan(Bundle bundle, ClassLoader bundleClassLoader) {
		return doScan(bundle, bundleClassLoader);
	}
}
