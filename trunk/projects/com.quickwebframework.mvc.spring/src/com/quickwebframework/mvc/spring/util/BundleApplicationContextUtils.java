package com.quickwebframework.mvc.spring.util;

import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;

import com.quickwebframework.mvc.spring.SpringMvcFrameworkService;

public class BundleApplicationContextUtils {

	/**
	 * 根据Bundle得到对应的ApplicationContext对象
	 * 
	 * @param bundle
	 * @return
	 */
	public static ApplicationContext getBundleApplicationContext(Bundle bundle) {
		return SpringMvcFrameworkService.bundleApplicationContextMap
				.get(bundle);
	}
}
