package com.quickwebframework.bridge;

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

import org.osgi.framework.BundleContext;


public abstract class ListenerBridge extends FilterBridge implements
		ServletContextListener, ServletContextAttributeListener,
		ServletRequestListener, ServletRequestAttributeListener,
		HttpSessionActivationListener, HttpSessionAttributeListener,
		HttpSessionBindingListener, HttpSessionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3779917967114677693L;

	public ListenerBridge(BundleContext bundleContext) {
		super(bundleContext);
	}

	// 开始 ServletContextListener
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO
	}

	// 停止 ServletContextListener

	// 开始 ServletContextAttributeListener
	@Override
	public void attributeAdded(ServletContextAttributeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent arg0) {
		// TODO Auto-generated method stub

	}

	// 结束 ServletContextAttributeListener

	// 开始 ServletRequestListener
	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestInitialized(ServletRequestEvent arg0) {
		// TODO Auto-generated method stub

	}

	// 结束 ServletRequestListener

	// 开始 ServletRequestAttributeListener
	@Override
	public void attributeAdded(ServletRequestAttributeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attributeRemoved(ServletRequestAttributeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attributeReplaced(ServletRequestAttributeEvent arg0) {
		// TODO Auto-generated method stub

	}

	// 结束 ServletRequestAttributeListener

	// 开始 HttpSessionActivationListener
	@Override
	public void sessionDidActivate(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	// 结束 HttpSessionActivationListener

	// 开始 HttpSessionAttributeListener
	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub

	}

	// 结束 HttpSessionAttributeListener

	// 开始 HttpSessionBindingListener
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub

	}

	// 结束 HttpSessionBindingListener

	// 开始 HttpSessionListener
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

	}
	// 结束 HttpSessionListener
}