package com.quickwebframework.viewrender.jsp.support;

import java.lang.reflect.InvocationTargetException;

import javax.naming.NamingException;

import org.apache.tomcat.InstanceManager;
import org.osgi.framework.Bundle;

import com.quickwebframework.util.BundleUtils;

public class DefaultInstanceManager implements InstanceManager {

	private ClassLoader bundleClassLoader;

	public DefaultInstanceManager(Bundle bundle) {
		bundleClassLoader = BundleUtils.getBundleClassLoader(bundle);
	}

	public Object newInstance(String className) throws IllegalAccessException,
			InvocationTargetException, NamingException, InstantiationException,
			ClassNotFoundException {
		return newInstance(className, bundleClassLoader);
	}

	public Object newInstance(String className, ClassLoader classLoader)
			throws IllegalAccessException, InvocationTargetException,
			NamingException, InstantiationException, ClassNotFoundException {
		Class<?> clazz = classLoader.loadClass(className);
		return clazz.newInstance();
	}

	public void newInstance(Object o) throws IllegalAccessException,
			InvocationTargetException, NamingException {
		Class<?> clazz = o.getClass();
		try {
			newInstance(clazz.getName(), clazz.getClassLoader());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void destroyInstance(Object o) throws IllegalAccessException,
			InvocationTargetException {
	}
}
