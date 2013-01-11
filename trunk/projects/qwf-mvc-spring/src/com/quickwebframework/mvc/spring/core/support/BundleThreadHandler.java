package com.quickwebframework.mvc.spring.core.support;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;

import com.quickwebframework.framework.ThreadContext;
import com.quickwebframework.mvc.spring.core.BundleHandler;

public class BundleThreadHandler implements BundleHandler {

	@Override
	public void registerBundle(Bundle bundle,
			ApplicationContext applicationContext) {
		// 从ApplicationContext得到线程列表
		Map<String, Thread> threadMap = applicationContext
				.getBeansOfType(Thread.class);
		for (Thread thread : threadMap.values()) {
			ThreadContext.addThread(bundle, thread);
		}
	}

	@Override
	public void unregisterBundle(Bundle bundle,
			ApplicationContext applicationContext) {
		// 从ApplicationContext得到线程列表
		Map<String, Thread> threadMap = applicationContext
				.getBeansOfType(Thread.class);
		for (Thread thread : threadMap.values()) {
			ThreadContext.removeThread(bundle, thread);
		}
	}
}
