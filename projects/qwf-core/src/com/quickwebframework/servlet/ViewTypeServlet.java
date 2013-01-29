package com.quickwebframework.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.quickwebframework.framework.WebContext;

public abstract class ViewTypeServlet extends HttpServlet {

	private static final long serialVersionUID = 1764897069126545760L;
	private String viewTypeName;
	private String viewRenderName;
	private org.osgi.framework.ServiceListener serviceListener;

	/**
	 * 得到视图类型名称
	 * 
	 * @return
	 */
	public String getViewTypeName() {
		return viewTypeName;
	}

	/**
	 * 设置视图类型名称
	 * 
	 * @param viewTypeName
	 */
	public void setViewTypeName(String viewTypeName) {
		this.viewTypeName = viewTypeName;
	}

	/**
	 * 获取视图渲染器名称
	 * 
	 * @return
	 */
	public String getViewRenderName() {
		return viewRenderName;
	}

	/**
	 * 设置视图渲染器名称
	 * 
	 * @param viewRenderName
	 */
	public void setViewRenderName(String viewRenderName) {
		this.viewRenderName = viewRenderName;
	}

	public ViewTypeServlet() {
		String bundleName = getBundleName();
		viewTypeName = WebContext.getQwfConfig(bundleName + ".viewTypeName");
		viewRenderName = WebContext
				.getQwfConfig(bundleName + ".viewRenderName");
		serviceListener = new ServiceListener() {
			@Override
			public void serviceChanged(ServiceEvent event) {
				ServiceReference<?> serviceReference = event
						.getServiceReference();
				if (serviceReference.getBundle().getSymbolicName()
						.equals(viewRenderName)) {

				}
			}
		};
	}

	// 注册
	public void register() {
		BundleContext bundleContext = getBundleContext();

		WebContext.registerViewTypeServlet(this);
	}

	// 取消注册
	public void unregister() {
		BundleContext bundleContext = getBundleContext();

		WebContext.unregisterViewTypeServlet(this);
	}

	/**
	 * 得到插件名称(不能由Bundle.getSymbolicName()方法得到，
	 * 因为当有项目要把所有qwf的bundle整合到一个bundle中时会出问题)
	 * 
	 * @return
	 */
	public abstract String getBundleName();

	/**
	 * 得到插件的BundleContext对象
	 * 
	 * @return
	 */
	public abstract BundleContext getBundleContext();

	/**
	 * 得到此视图类型Servlet下面的所有URL
	 * 
	 * @return
	 */
	public abstract String[] getUrls();

	/**
	 * HTTP服务
	 */
	@Override
	public abstract void service(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException;
}
