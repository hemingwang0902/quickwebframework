package com.quickwebframework.db.jdbc.spring.impl;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;

import com.quickwebframework.db.jdbc.DataSourceContext;
import com.quickwebframework.ioc.spring.util.ApplicationContextListener;
import com.quickwebframework.ioc.spring.util.BundleApplicationContextUtils;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private ApplicationContextListener applicationContextListener;

	static BundleContext getContext() {
		return context;
	}

	public Activator() {
		applicationContextListener = new ApplicationContextListener() {
			@Override
			public Map<String, Object> getPreloadBeans() {
				Map<String, Object> rtnMap = new HashMap<String, Object>();
				String[] propertyNames = DataSourceContext
						.getDataSourcePropertyNames();
				for (String propertyName : propertyNames) {
					DataSource dataSource = DataSourceContext
							.getDataSource(propertyName);
					if (dataSource == null) {
						continue;
					}
					String beanName;
					if (propertyName.isEmpty()) {
						beanName = "dataSource";
					} else {
						beanName = "dataSource_" + propertyName;
					}
					rtnMap.put(beanName, dataSource);
				}
				return rtnMap;
			}

			@Override
			public void contextStarting(ApplicationContext applicationContext,
					Bundle bundle) {
			}

			@Override
			public void contextStarted(ApplicationContext applicationContext,
					Bundle bundle) {
			}
		};
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		BundleApplicationContextUtils
				.addApplicationContextListener(applicationContextListener);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		BundleApplicationContextUtils
				.removeApplicationContextListener(applicationContextListener);
		Activator.context = null;
	}
}
