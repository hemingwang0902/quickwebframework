package com.quickwebframework.view.struts2.servlet.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.quickwebframework.viewrender.ViewRenderService;

public class PluginHttpServletRequest implements HttpServletRequest {
	private static Log log = LogFactory.getLog(PluginHttpServletRequest.class);
	private HttpServletRequest srcRequest;
	private String pluginName;
	private String urlPrefix;
	private ViewRenderService viewRenderService;

	public PluginHttpServletRequest(HttpServletRequest srcRequest,
			String viewTypeName, String pluginName,
			ViewRenderService viewRenderService) {

		this.srcRequest = srcRequest;
		this.pluginName = pluginName;
		urlPrefix = "/" + pluginName + "/" + viewTypeName;
		this.viewRenderService = viewRenderService;
	}

	@Override
	public AsyncContext getAsyncContext() {
		return srcRequest.getAsyncContext();
	}

	@Override
	public Object getAttribute(String arg0) {
		return srcRequest.getAttribute(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return srcRequest.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return srcRequest.getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return srcRequest.getContentLength();
	}

	@Override
	public String getContentType() {
		return srcRequest.getContentType();
	}

	@Override
	public DispatcherType getDispatcherType() {
		return srcRequest.getDispatcherType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return srcRequest.getInputStream();
	}

	@Override
	public String getLocalAddr() {
		return srcRequest.getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return srcRequest.getLocalName();
	}

	@Override
	public int getLocalPort() {
		return srcRequest.getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return srcRequest.getLocale();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return srcRequest.getLocales();
	}

	@Override
	public String getParameter(String arg0) {
		return srcRequest.getParameter(arg0);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return srcRequest.getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return srcRequest.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return srcRequest.getParameterValues(arg0);
	}

	@Override
	public String getProtocol() {
		return srcRequest.getProtocol();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return srcRequest.getReader();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getRealPath(String arg0) {
		return srcRequest.getRealPath(arg0);
	}

	@Override
	public String getRemoteAddr() {
		return srcRequest.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return srcRequest.getRemoteAddr();
	}

	@Override
	public int getRemotePort() {
		return srcRequest.getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		log.debug("getRequestDispatcher->" + arg0);
		if (arg0.startsWith(urlPrefix)) {
			arg0 = arg0.substring(urlPrefix.length());
		}
		String viewName = pluginName
				+ viewRenderService.getPluginNameAndPathSplitString() + arg0;
		log.debug("getRequestDispatcher->viewName->" + viewName);
		return new PluginRequestDispatcher(viewName, viewRenderService);
	}

	@Override
	public String getScheme() {
		return srcRequest.getScheme();
	}

	@Override
	public String getServerName() {
		return srcRequest.getServerName();
	}

	@Override
	public int getServerPort() {
		return srcRequest.getServerPort();
	}

	@Override
	public ServletContext getServletContext() {
		return srcRequest.getServletContext();
	}

	@Override
	public boolean isAsyncStarted() {
		return srcRequest.isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		return srcRequest.isAsyncSupported();
	}

	@Override
	public boolean isSecure() {
		return srcRequest.isSecure();
	}

	@Override
	public void removeAttribute(String arg0) {
		srcRequest.removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		srcRequest.setAttribute(arg0, arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		srcRequest.setCharacterEncoding(arg0);
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return srcRequest.startAsync();
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1)
			throws IllegalStateException {
		return srcRequest.startAsync(arg0, arg1);
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException,
			ServletException {
		return srcRequest.authenticate(arg0);
	}

	@Override
	public String getAuthType() {
		return srcRequest.getAuthType();
	}

	@Override
	public String getContextPath() {
		return srcRequest.getContextPath();
	}

	@Override
	public Cookie[] getCookies() {
		return srcRequest.getCookies();
	}

	@Override
	public long getDateHeader(String arg0) {
		return srcRequest.getDateHeader(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		return srcRequest.getHeader(arg0);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return srcRequest.getHeaderNames();
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {
		return srcRequest.getHeaders(arg0);
	}

	@Override
	public int getIntHeader(String arg0) {
		return srcRequest.getIntHeader(arg0);
	}

	@Override
	public String getMethod() {
		return srcRequest.getMethod();
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {
		return srcRequest.getPart(arg0);
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		return srcRequest.getParts();
	}

	@Override
	public String getPathInfo() {
		return srcRequest.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return srcRequest.getPathTranslated();
	}

	@Override
	public String getQueryString() {
		return srcRequest.getQueryString();
	}

	@Override
	public String getRemoteUser() {
		return srcRequest.getRemoteUser();
	}

	@Override
	public String getRequestURI() {
		return srcRequest.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return srcRequest.getRequestURL();
	}

	@Override
	public String getRequestedSessionId() {
		return srcRequest.getRequestedSessionId();
	}

	@Override
	public String getServletPath() {
		return srcRequest.getServletPath();
	}

	@Override
	public HttpSession getSession() {
		return srcRequest.getSession();
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return srcRequest.getSession(arg0);
	}

	@Override
	public Principal getUserPrincipal() {
		return srcRequest.getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return srcRequest.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return srcRequest.isRequestedSessionIdFromURL();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return srcRequest.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return srcRequest.isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return srcRequest.isUserInRole(arg0);
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		srcRequest.login(arg0, arg1);
	}

	@Override
	public void logout() throws ServletException {
		srcRequest.logout();
	}

}
