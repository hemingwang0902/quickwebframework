package com.quickwebframework.core;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
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

@SuppressWarnings("unchecked")
public class PluginServletContext extends MapperObject implements
		ServletContext {

	public PluginServletContext(Object orginObject) {
		super(orginObject);
	}

	@Override
	public Object getAttribute(String arg0) {
		return invokeOrginObjectMethod(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return (Enumeration<String>) invokeOrginObjectMethod();
	}

	@Override
	public ServletContext getContext(String arg0) {
		return new PluginServletContext(invokeOrginObjectMethod(arg0));
	}

	@Override
	public String getContextPath() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getInitParameter(String arg0) {
		return (String) invokeOrginObjectMethod(arg0);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return (Enumeration<String>) invokeOrginObjectMethod();
	}

	@Override
	public int getMajorVersion() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public String getMimeType(String arg0) {
		return (String) invokeOrginObjectMethod(arg0);
	}

	@Override
	public int getMinorVersion() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String arg0) {
		return (RequestDispatcher) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String getRealPath(String arg0) {
		return (String) invokeOrginObjectMethod(arg0);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return (RequestDispatcher) invokeOrginObjectMethod(arg0);
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException {
		return (URL) invokeOrginObjectMethod(arg0);
	}

	@Override
	public InputStream getResourceAsStream(String arg0) {
		return (InputStream) invokeOrginObjectMethod(arg0);
	}

	@Override
	public Set<String> getResourcePaths(String arg0) {
		return (Set<String>) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String getServerInfo() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		return null;
	}

	@Override
	public String getServletContextName() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public Enumeration<String> getServletNames() {
		return (Enumeration<String>) invokeOrginObjectMethod();
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		return (Enumeration<Servlet>) invokeOrginObjectMethod();
	}

	@Override
	public void log(String arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void log(Exception arg0, String arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void log(String arg0, Throwable arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void removeAttribute(String arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	// =====================
	// Servelt 3.0新增方法
	// =====================
	@Override
	public Dynamic addFilter(String arg0, String arg1) {
		return (Dynamic) invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public Dynamic addFilter(String arg0, Filter arg1) {
		return (Dynamic) invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		return (Dynamic) invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void addListener(String arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public <T extends EventListener> void addListener(T arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void addListener(Class<? extends EventListener> arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) {
		return (javax.servlet.ServletRegistration.Dynamic) invokeOrginObjectMethod(
				arg0, arg1);
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) {
		return (javax.servlet.ServletRegistration.Dynamic) invokeOrginObjectMethod(
				arg0, arg1);
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) {
		return (javax.servlet.ServletRegistration.Dynamic) invokeOrginObjectMethod(
				arg0, arg1);
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		return (T) invokeOrginObjectMethod(arg0);
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		return (T) invokeOrginObjectMethod(arg0);
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		return (T) invokeOrginObjectMethod(arg0);
	}

	@Override
	public void declareRoles(String... arg0) {
		invokeOrginObjectMethod((Object[]) arg0);
	}

	@Override
	public ClassLoader getClassLoader() {
		return (ClassLoader) invokeOrginObjectMethod();
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		return (Set<SessionTrackingMode>) invokeOrginObjectMethod();
	}

	@Override
	public int getEffectiveMajorVersion() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public int getEffectiveMinorVersion() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return (Set<SessionTrackingMode>) invokeOrginObjectMethod();
	}

	@Override
	public FilterRegistration getFilterRegistration(String arg0) {
		return (FilterRegistration) invokeOrginObjectMethod(arg0);
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return (Map<String, ? extends FilterRegistration>) invokeOrginObjectMethod();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return (JspConfigDescriptor) invokeOrginObjectMethod();
	}

	@Override
	public ServletRegistration getServletRegistration(String arg0) {
		return (ServletRegistration) invokeOrginObjectMethod(arg0);
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		return (Map<String, ? extends ServletRegistration>) invokeOrginObjectMethod();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return (SessionCookieConfig) invokeOrginObjectMethod();
	}

	@Override
	public boolean setInitParameter(String arg0, String arg1) {
		return (Boolean) invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {
		invokeOrginObjectMethod(arg0);
	}
}
