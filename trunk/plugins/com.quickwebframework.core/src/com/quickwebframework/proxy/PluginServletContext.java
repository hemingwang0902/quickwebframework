package com.quickwebframework.proxy;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

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
}
