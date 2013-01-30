package com.quickwebframework.viewrender.jsp.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;

import com.quickwebframework.util.BundleUtils;

public class JspCompileServletContext implements ServletContext {
	private static Log log = LogFactory.getLog(JspCompileServletContext.class
			.getName());

	public static final String BUNDLE_RESOURCE_URL_PREFIX = "/bundle:";
	private ServletContext srcServletContext;
	private Bundle bundle;
	private Map<String, Object> attributeMap = new HashMap<String, Object>();

	public JspCompileServletContext(ServletContext srcServletContext,
			Bundle bundle) {
		this.srcServletContext = srcServletContext;
		this.bundle = bundle;
	}

	@Override
	public Dynamic addFilter(String arg0, String arg1) {
		return srcServletContext.addFilter(arg0, arg1);
	}

	@Override
	public Dynamic addFilter(String arg0, Filter arg1) {
		return srcServletContext.addFilter(arg0, arg1);
	}

	@Override
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		return srcServletContext.addFilter(arg0, arg1);
	}

	@Override
	public void addListener(String arg0) {
		srcServletContext.addListener(arg0);
	}

	@Override
	public <T extends EventListener> void addListener(T arg0) {
		srcServletContext.addListener(arg0);
	}

	@Override
	public void addListener(Class<? extends EventListener> arg0) {
		srcServletContext.addListener(arg0);
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) {
		return srcServletContext.addServlet(arg0, arg1);
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) {
		return srcServletContext.addServlet(arg0, arg1);
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) {
		return srcServletContext.addServlet(arg0, arg1);
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		return srcServletContext.createFilter(arg0);
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		return srcServletContext.createListener(arg0);
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		return srcServletContext.createServlet(arg0);
	}

	@Override
	public void declareRoles(String... arg0) {
		srcServletContext.declareRoles(arg0);
	}

	@Override
	public Object getAttribute(String arg0) {
		return attributeMap.get(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return srcServletContext.getAttributeNames();
	}

	@Override
	public ClassLoader getClassLoader() {
		System.out.println("getClassLoader:");
		return srcServletContext.getClassLoader();
	}

	@Override
	public ServletContext getContext(String arg0) {
		return srcServletContext.getContext(arg0);
	}

	@Override
	public String getContextPath() {
		return srcServletContext.getContextPath();
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		return srcServletContext.getDefaultSessionTrackingModes();
	}

	@Override
	public int getEffectiveMajorVersion() {
		return srcServletContext.getEffectiveMajorVersion();
	}

	@Override
	public int getEffectiveMinorVersion() {
		return srcServletContext.getEffectiveMinorVersion();
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return srcServletContext.getEffectiveSessionTrackingModes();
	}

	@Override
	public FilterRegistration getFilterRegistration(String arg0) {
		return srcServletContext.getFilterRegistration(arg0);
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return srcServletContext.getFilterRegistrations();
	}

	@Override
	public String getInitParameter(String arg0) {
		return srcServletContext.getInitParameter(arg0);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return srcServletContext.getInitParameterNames();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return srcServletContext.getJspConfigDescriptor();
	}

	@Override
	public int getMajorVersion() {
		return srcServletContext.getMajorVersion();
	}

	@Override
	public String getMimeType(String arg0) {
		return srcServletContext.getMimeType(arg0);
	}

	@Override
	public int getMinorVersion() {
		return srcServletContext.getMinorVersion();
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String arg0) {
		return srcServletContext.getNamedDispatcher(arg0);
	}

	@Override
	public String getRealPath(String arg0) {
		if (arg0.startsWith("/WEB-INF/lib/")) {
			return srcServletContext.getRealPath(arg0);
		}
		log.debug("getRealPath-->" + arg0);
		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return srcServletContext.getRequestDispatcher(arg0);
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException {
		// 如果是插件中的资源
		if (arg0.startsWith(BUNDLE_RESOURCE_URL_PREFIX)) {
			return new URL(arg0.replace(BUNDLE_RESOURCE_URL_PREFIX, "bundle:/"));
		}
		URL url = srcServletContext.getResource(arg0);
		if (url != null) {
			return url;
		}
		String[] resourcePossiblePaths = new String[] { arg0, arg0 + ".tld",
				"/META-INF" + arg0, "/META-INF" + arg0 + ".tld" };
		for (String resourcePossiblePath : resourcePossiblePaths) {
			url = BundleUtils.getBundleResource(bundle, resourcePossiblePath);
			if (url != null) {
				break;
			}
		}
		if (url == null) {
			log.warn("资源未找到->getResource->" + arg0);
		}
		return url;
	}

	@Override
	public InputStream getResourceAsStream(String arg0) {
		if (arg0.startsWith(BUNDLE_RESOURCE_URL_PREFIX)) {
			String tmpStr = arg0.substring(BUNDLE_RESOURCE_URL_PREFIX.length());
			while (tmpStr.startsWith("/")) {
				tmpStr = tmpStr.substring(1);
			}
			int spIndex = tmpStr.indexOf("/");
			String bundleId = tmpStr.substring(0, spIndex);
			String resourcePath = tmpStr.substring(spIndex);

			bundleId = StringUtils.split(bundleId, '.')[0];
			Bundle bundle = Activator.getContext().getBundle(
					Long.parseLong(bundleId));
			URL url = bundle.getResource(resourcePath);
			try {
				return url.openStream();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		InputStream ins = srcServletContext.getResourceAsStream(arg0);
		if (ins != null) {
			return ins;
		}
		try {
			URL url = getResource(arg0);
			if (url != null) {
				ins = url.openStream();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (ins == null) {
			log.warn("资源未找到->getResourceAsStream->" + arg0);
		}
		return ins;
	}

	@Override
	public Set<String> getResourcePaths(String arg0) {
		return srcServletContext.getResourcePaths(arg0);
	}

	@Override
	public String getServerInfo() {
		return srcServletContext.getServerInfo();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		return srcServletContext.getServlet(arg0);
	}

	@Override
	public String getServletContextName() {
		return srcServletContext.getServletContextName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Enumeration<String> getServletNames() {
		return srcServletContext.getServletNames();
	}

	@Override
	public ServletRegistration getServletRegistration(String arg0) {
		return srcServletContext.getServletRegistration(arg0);
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		return srcServletContext.getServletRegistrations();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Enumeration<Servlet> getServlets() {
		return srcServletContext.getServlets();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return srcServletContext.getSessionCookieConfig();
	}

	@Override
	public void log(String arg0) {
		srcServletContext.log(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void log(Exception arg0, String arg1) {
		srcServletContext.log(arg0, arg1);
	}

	@Override
	public void log(String arg0, Throwable arg1) {
		srcServletContext.log(arg0, arg1);
	}

	@Override
	public void removeAttribute(String arg0) {
		srcServletContext.removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		attributeMap.put(arg0, arg1);
	}

	@Override
	public boolean setInitParameter(String arg0, String arg1) {
		return srcServletContext.setInitParameter(arg0, arg1);
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {
		srcServletContext.setSessionTrackingModes(arg0);
	}
}
