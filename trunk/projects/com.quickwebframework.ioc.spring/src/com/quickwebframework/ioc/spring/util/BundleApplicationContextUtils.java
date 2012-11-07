package com.quickwebframework.ioc.spring.util;

import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;

import com.quickwebframework.ioc.spring.service.impl.SpringIocFrameworkService;

public class BundleApplicationContextUtils {

	/**
	 * 根据Bundle得到对应的ApplicationContext对象
	 * 
	 * @param bundle
	 * @return
	 */
	public static ApplicationContext getBundleApplicationContext(Bundle bundle) {
		return SpringIocFrameworkService.bundleApplicationContextMap
				.get(bundle);
	}
}
