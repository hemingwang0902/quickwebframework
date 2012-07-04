package com.quickwebframework.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@SuppressWarnings("unchecked")
public class PluginHttpServletRequest extends MapperObject implements
		HttpServletRequest {

	public PluginHttpServletRequest(Object orginObject) {
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
	public String getCharacterEncoding() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public int getContentLength() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public String getContentType() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return (ServletInputStream) invokeOrginObjectMethod();
	}

	@Override
	public String getLocalAddr() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getLocalName() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public int getLocalPort() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public Locale getLocale() {
		return (Locale) invokeOrginObjectMethod();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return (Enumeration<Locale>) invokeOrginObjectMethod();
	}

	@Override
	public String getParameter(String arg0) {
		return (String) invokeOrginObjectMethod(arg0);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return (Map<String, String[]>) invokeOrginObjectMethod();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return (Enumeration<String>) invokeOrginObjectMethod();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return (String[]) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String getProtocol() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return (BufferedReader) invokeOrginObjectMethod();
	}

	@Override
	public String getRealPath(String arg0) {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getRemoteAddr() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getRemoteHost() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public int getRemotePort() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return (RequestDispatcher) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String getScheme() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getServerName() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public int getServerPort() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public boolean isSecure() {
		return (Boolean) invokeOrginObjectMethod();
	}

	@Override
	public void removeAttribute(String arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public String getAuthType() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getContextPath() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public Cookie[] getCookies() {
		return (Cookie[]) invokeOrginObjectMethod();
	}

	@Override
	public long getDateHeader(String arg0) {
		return (Long) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		return (String) invokeOrginObjectMethod(arg0);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return (Enumeration<String>) invokeOrginObjectMethod();
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {
		return (Enumeration<String>) invokeOrginObjectMethod(arg0);
	}

	@Override
	public int getIntHeader(String arg0) {
		return (Integer) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String getMethod() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getPathInfo() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getPathTranslated() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getQueryString() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getRemoteUser() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getRequestURI() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public StringBuffer getRequestURL() {
		return (StringBuffer) invokeOrginObjectMethod();
	}

	@Override
	public String getRequestedSessionId() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getServletPath() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public HttpSession getSession() {
		return new PluginHttpSession(invokeOrginObjectMethod());
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return new PluginHttpSession(invokeOrginObjectMethod(arg0));
	}

	@Override
	public Principal getUserPrincipal() {
		return (Principal) invokeOrginObjectMethod();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return (Boolean) invokeOrginObjectMethod();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return (Boolean) invokeOrginObjectMethod();
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return (Boolean) invokeOrginObjectMethod();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return (Boolean) invokeOrginObjectMethod();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return (Boolean) invokeOrginObjectMethod(arg0);
	}
}
