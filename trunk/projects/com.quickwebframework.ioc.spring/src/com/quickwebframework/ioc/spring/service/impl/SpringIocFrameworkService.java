package com.quickwebframework.ioc.spring.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;

import com.quickwebframework.ioc.spring.util.BundleApplicationContextUtils;
import com.quickwebframework.ioc.spring.util.BundleScanner;
import com.quickwebframework.service.IocFrameworkService;
import com.quickwebframework.util.BundleUtil;

public class SpringIocFrameworkService implements IocFrameworkService {

	private BundleScanner scanner = new BundleScanner();
	public static Map<Bundle, ApplicationContext> bundleApplicationContextMap = new HashMap<Bundle, ApplicationContext>();

	/**
	 * 加入Bundle
	 * 
	 * @param bundle
	 */
	@Override
	public void addBundle(Bundle bundle) {
		if (!bundleApplicationContextMap.containsKey(bundle)) {
			ClassLoader bundleClassLoader = BundleUtil
					.getBundleClassLoader(bundle);
			ApplicationContext applicationContext = scanner.scan(bundle,
					bundleClassLoader);
			bundleApplicationContextMap.put(bundle, applicationContext);
		} else {
			// Bundle already added,so here do nothing.
		}
	}

	/**
	 * 移除Bundle
	 * 
	 * @param bundle
	 */
	@Override
	public void removeBundle(Bundle bundle) {
		bundleApplicationContextMap.remove(bundle);
	}

	/**
	 * 
	 */
	@Override
	public Object getBundleApplicationContext(Bundle bundle) {
		return BundleApplicationContextUtils
				.getBundleApplicationContext(bundle);
	}
}
