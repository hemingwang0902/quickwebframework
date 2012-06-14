package com.quickwebframework.core;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class PluginHttpSession extends MapperObject implements HttpSession {

	public PluginHttpSession(Object orginObject) {
		super(orginObject);
	}

	@Override
	public Object getAttribute(String arg0) {
		return invokeOrginObjectMethod(arg0);
	}

	@Override
	public Enumeration<?> getAttributeNames() {
		return (Enumeration<?>) invokeOrginObjectMethod();
	}

	@Override
	public long getCreationTime() {
		return (Long) invokeOrginObjectMethod();
	}

	@Override
	public String getId() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public long getLastAccessedTime() {
		return (Long) invokeOrginObjectMethod();
	}

	@Override
	public int getMaxInactiveInterval() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public ServletContext getServletContext() {
		return new PluginServletContext(invokeOrginObjectMethod());
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getValue(String arg0) {
		return invokeOrginObjectMethod(arg0);
	}

	@Override
	public String[] getValueNames() {
		return (String[]) invokeOrginObjectMethod();
	}

	@Override
	public void invalidate() {
		invokeOrginObjectMethod();
	}

	@Override
	public boolean isNew() {
		return (Boolean) invokeOrginObjectMethod();
	}

	@Override
	public void putValue(String arg0, Object arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void removeAttribute(String arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void removeValue(String arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void setMaxInactiveInterval(int arg0) {
		invokeOrginObjectMethod(arg0);
	}
}
