package com.quickwebframework.framework;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import com.quickwebframework.core.Activator;
import org.apache.commons.logging.LogFactory;
import com.quickwebframework.util.BundleContextUtil;

public abstract class FrameworkContext {

	private ServiceListener serviceListener;
	private Map<String, Field> serviceFieldMap;

	/**
	 * 初始化方法
	 */
	protected abstract void init();

	/**
	 * 销毁时方法
	 */
	protected abstract void destory();

	public FrameworkContext() {
		serviceFieldMap = new HashMap<String, Field>();

		final BundleContext bundleContext = Activator.getContext();
		serviceListener = new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent event) {
				String changedServiceName = event.getServiceReference()
						.toString();
				for (String serviceName : serviceFieldMap.keySet()) {
					if (changedServiceName.contains(serviceName)) {
						Field field = serviceFieldMap.get(serviceName);
						setServiceObjectToStaticField(serviceName, field);
					}
				}
			}
		};
		bundleContext.addServiceListener(serviceListener);
	}

	@Override
	public void finalize() {
		BundleContext bundleContext = Activator.getContext();
		bundleContext.removeServiceListener(serviceListener);
	}

	private void setServiceObjectToStaticField(String serviceName, Field field) {
		field.setAccessible(true);
		try {
			field.set(null, BundleContextUtil.getServiceObject(
					Activator.getContext(), serviceName));
		} catch (Exception ex) {
			LogFactory.getLog(FrameworkContext.class.getName()).error(
					"给绑定OSGi服务的字段赋值时出现异常：" + ex.getMessage(), ex);
		}
	}

	protected void addSimpleServiceStaticFieldLink(String serviceName,
			String fieldName) {
		try {
			Class<?> clazz = this.getClass();
			Field field = clazz.getDeclaredField(fieldName);
			serviceFieldMap.put(serviceName, field);
			setServiceObjectToStaticField(serviceName, field);
		} catch (Exception ex) {
			LogFactory.getLog(FrameworkContext.class.getName()).error(
					"得到类的字段时出错，原因：" + ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}
}
