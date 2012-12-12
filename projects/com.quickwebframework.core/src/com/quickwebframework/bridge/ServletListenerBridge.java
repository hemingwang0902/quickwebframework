package com.quickwebframework.bridge;

import java.util.List;

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

import com.quickwebframework.framework.WebContext;

public class ServletListenerBridge implements ServletContextListener,
		ServletContextAttributeListener, ServletRequestListener,
		ServletRequestAttributeListener, HttpSessionActivationListener,
		HttpSessionAttributeListener, HttpSessionBindingListener,
		HttpSessionListener {

	// 开始 ServletContextListener
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		List<ServletContextListener> list = WebContext.getInstance()
				.getListenerList(ServletContextListener.class);
		if (list == null)
			return;
		for (ServletContextListener listener : list) {
			listener.contextInitialized(arg0);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		List<ServletContextListener> list = WebContext.getInstance()
				.getListenerList(ServletContextListener.class);
		if (list == null)
			return;
		for (ServletContextListener listener : list) {
			listener.contextDestroyed(arg0);
		}
	}

	// 停止 ServletContextListener

	// 开始 ServletContextAttributeListener
	@Override
	public void attributeAdded(ServletContextAttributeEvent arg0) {
		List<ServletContextAttributeListener> list = WebContext.getInstance()
				.getListenerList(ServletContextAttributeListener.class);
		if (list == null)
			return;
		for (ServletContextAttributeListener listener : list) {
			listener.attributeAdded(arg0);
		}
	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent arg0) {
		List<ServletContextAttributeListener> list = WebContext.getInstance()
				.getListenerList(ServletContextAttributeListener.class);
		if (list == null)
			return;
		for (ServletContextAttributeListener listener : list) {
			listener.attributeRemoved(arg0);
		}
	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent arg0) {
		List<ServletContextAttributeListener> list = WebContext.getInstance()
				.getListenerList(ServletContextAttributeListener.class);
		if (list == null)
			return;
		for (ServletContextAttributeListener listener : list) {
			listener.attributeReplaced(arg0);
		}
	}

	// 结束 ServletContextAttributeListener

	// 开始 ServletRequestListener
	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
		List<ServletRequestListener> list = WebContext.getInstance()
				.getListenerList(ServletRequestListener.class);
		if (list == null)
			return;
		for (ServletRequestListener listener : list) {
			listener.requestDestroyed(arg0);
		}
	}

	@Override
	public void requestInitialized(ServletRequestEvent arg0) {
		List<ServletRequestListener> list = WebContext.getInstance()
				.getListenerList(ServletRequestListener.class);
		if (list == null)
			return;
		for (ServletRequestListener listener : list) {
			listener.requestInitialized(arg0);
		}
	}

	// 结束 ServletRequestListener

	// 开始 ServletRequestAttributeListener
	@Override
	public void attributeAdded(ServletRequestAttributeEvent arg0) {
		List<ServletRequestAttributeListener> list = WebContext.getInstance()
				.getListenerList(ServletRequestAttributeListener.class);
		if (list == null)
			return;
		for (ServletRequestAttributeListener listener : list) {
			listener.attributeAdded(arg0);
		}
	}

	@Override
	public void attributeRemoved(ServletRequestAttributeEvent arg0) {
		List<ServletRequestAttributeListener> list = WebContext.getInstance()
				.getListenerList(ServletRequestAttributeListener.class);
		if (list == null)
			return;
		for (ServletRequestAttributeListener listener : list) {
			listener.attributeRemoved(arg0);
		}
	}

	@Override
	public void attributeReplaced(ServletRequestAttributeEvent arg0) {
		List<ServletRequestAttributeListener> list = WebContext.getInstance()
				.getListenerList(ServletRequestAttributeListener.class);
		if (list == null)
			return;
		for (ServletRequestAttributeListener listener : list) {
			listener.attributeReplaced(arg0);
		}
	}

	// 结束 ServletRequestAttributeListener

	// 开始 HttpSessionActivationListener
	@Override
	public void sessionDidActivate(HttpSessionEvent arg0) {
		List<HttpSessionActivationListener> list = WebContext.getInstance()
				.getListenerList(HttpSessionActivationListener.class);
		if (list == null)
			return;
		for (HttpSessionActivationListener listener : list) {
			listener.sessionDidActivate(arg0);
		}
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent arg0) {
		List<HttpSessionActivationListener> list = WebContext.getInstance()
				.getListenerList(HttpSessionActivationListener.class);
		if (list == null)
			return;
		for (HttpSessionActivationListener listener : list) {
			listener.sessionWillPassivate(arg0);
		}
	}

	// 结束 HttpSessionActivationListener

	// 开始 HttpSessionAttributeListener
	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		List<HttpSessionAttributeListener> list = WebContext.getInstance()
				.getListenerList(HttpSessionAttributeListener.class);
		if (list == null)
			return;
		for (HttpSessionAttributeListener listener : list) {
			listener.attributeAdded(arg0);
		}
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		List<HttpSessionAttributeListener> list = WebContext.getInstance()
				.getListenerList(HttpSessionAttributeListener.class);
		if (list == null)
			return;
		for (HttpSessionAttributeListener listener : list) {
			listener.attributeRemoved(arg0);
		}
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		List<HttpSessionAttributeListener> list = WebContext.getInstance()
				.getListenerList(HttpSessionAttributeListener.class);
		if (list == null)
			return;
		for (HttpSessionAttributeListener listener : list) {
			listener.attributeReplaced(arg0);
		}
	}

	// 结束 HttpSessionAttributeListener

	// 开始 HttpSessionBindingListener
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		List<HttpSessionBindingListener> list = WebContext.getInstance()
				.getListenerList(HttpSessionBindingListener.class);
		if (list == null)
			return;
		for (HttpSessionBindingListener listener : list) {
			listener.valueBound(arg0);
		}
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		List<HttpSessionBindingListener> list = WebContext.getInstance()
				.getListenerList(HttpSessionBindingListener.class);
		if (list == null)
			return;
		for (HttpSessionBindingListener listener : list) {
			listener.valueUnbound(arg0);
		}
	}

	// 结束 HttpSessionBindingListener

	// 开始 HttpSessionListener
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		List<HttpSessionListener> list = WebContext.getInstance()
				.getListenerList(HttpSessionListener.class);
		if (list == null)
			return;
		for (HttpSessionListener listener : list) {
			listener.sessionCreated(arg0);
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		List<HttpSessionListener> list = WebContext.getInstance()
				.getListenerList(HttpSessionListener.class);
		if (list == null)
			return;
		for (HttpSessionListener listener : list) {
			listener.sessionDestroyed(arg0);
		}
	}
	// 结束 HttpSessionListener
}