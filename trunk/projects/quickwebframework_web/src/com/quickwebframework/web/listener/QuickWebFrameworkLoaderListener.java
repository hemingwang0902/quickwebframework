package com.quickwebframework.web.listener;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class QuickWebFrameworkLoaderListener extends QuickWebFrameworkFactory
		implements ServletContextListener, ServletContextAttributeListener,
		ServletRequestListener, ServletRequestAttributeListener,
		HttpSessionActivationListener, HttpSessionAttributeListener,
		HttpSessionBindingListener, HttpSessionListener {

	// 开始 ServletContextListener
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// 启动OSGi框架
		startOSGiFreamwork(arg0.getServletContext());

		if (getFrameworkBridgeObject() != null)
			((ServletContextListener) getFrameworkBridgeObject())
					.contextInitialized(arg0);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((ServletContextListener) getFrameworkBridgeObject())
					.contextDestroyed(arg0);

		// 停止OSGi框架
		stopOSGiFramework();
	}

	// 停止 ServletContextListener

	// 开始 ServletContextAttributeListener
	@Override
	public void attributeAdded(ServletContextAttributeEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((ServletContextAttributeListener) getFrameworkBridgeObject())
					.attributeAdded(arg0);
	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((ServletContextAttributeListener) getFrameworkBridgeObject())
					.attributeRemoved(arg0);
	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((ServletContextAttributeListener) getFrameworkBridgeObject())
					.attributeReplaced(arg0);
	}

	// 结束 ServletContextAttributeListener

	// 开始 ServletRequestListener
	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((ServletRequestListener) getFrameworkBridgeObject())
					.requestDestroyed(arg0);
	}

	@Override
	public void requestInitialized(ServletRequestEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((ServletRequestListener) getFrameworkBridgeObject())
					.requestInitialized(arg0);
	}

	// 结束 ServletRequestListener

	// 开始 ServletRequestAttributeListener
	@Override
	public void attributeAdded(ServletRequestAttributeEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((ServletRequestAttributeListener) getFrameworkBridgeObject())
					.attributeAdded(arg0);
	}

	@Override
	public void attributeRemoved(ServletRequestAttributeEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((ServletRequestAttributeListener) getFrameworkBridgeObject())
					.attributeRemoved(arg0);
	}

	@Override
	public void attributeReplaced(ServletRequestAttributeEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((ServletRequestAttributeListener) getFrameworkBridgeObject())
					.attributeReplaced(arg0);
	}

	// 结束 ServletRequestAttributeListener

	// 开始 HttpSessionActivationListener
	@Override
	public void sessionDidActivate(HttpSessionEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((HttpSessionActivationListener) getFrameworkBridgeObject())
					.sessionDidActivate(arg0);
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((HttpSessionActivationListener) getFrameworkBridgeObject())
					.sessionWillPassivate(arg0);
	}

	// 结束 HttpSessionActivationListener

	// 开始 HttpSessionAttributeListener
	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((HttpSessionAttributeListener) getFrameworkBridgeObject())
					.attributeAdded(arg0);
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((HttpSessionAttributeListener) getFrameworkBridgeObject())
					.attributeRemoved(arg0);
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((HttpSessionAttributeListener) getFrameworkBridgeObject())
					.attributeReplaced(arg0);
	}

	// 结束 HttpSessionAttributeListener

	// 开始 HttpSessionBindingListener
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((HttpSessionBindingListener) getFrameworkBridgeObject())
					.valueBound(arg0);
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((HttpSessionBindingListener) getFrameworkBridgeObject())
					.valueUnbound(arg0);
	}

	// 结束 HttpSessionBindingListener

	// 开始 HttpSessionListener
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((HttpSessionListener) getFrameworkBridgeObject())
					.sessionCreated(arg0);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		if (getFrameworkBridgeObject() != null)
			((HttpSessionListener) getFrameworkBridgeObject())
					.sessionDestroyed(arg0);
	}
	// 结束 HttpSessionListener
}
