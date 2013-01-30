package com.quickwebframework.view.struts2.servlet.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class PluginHttpServletResponse implements HttpServletResponse {

	private HttpServletResponse srcResponse;

	public PluginHttpServletResponse(HttpServletResponse srcResponse) {
		this.srcResponse = srcResponse;
	}

	@Override
	public void flushBuffer() throws IOException {
		srcResponse.flushBuffer();
	}

	@Override
	public int getBufferSize() {
		return srcResponse.getBufferSize();
	}

	@Override
	public String getCharacterEncoding() {
		return srcResponse.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return srcResponse.getContentType();
	}

	@Override
	public Locale getLocale() {
		return srcResponse.getLocale();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return srcResponse.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return srcResponse.getWriter();
	}

	@Override
	public boolean isCommitted() {
		return srcResponse.isCommitted();
	}

	@Override
	public void reset() {
		srcResponse.reset();
	}

	@Override
	public void resetBuffer() {
		srcResponse.resetBuffer();
	}

	@Override
	public void setBufferSize(int arg0) {
		srcResponse.setBufferSize(arg0);
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		srcResponse.setCharacterEncoding(arg0);
	}

	@Override
	public void setContentLength(int arg0) {
		srcResponse.setContentLength(arg0);
	}

	@Override
	public void setContentType(String arg0) {
		srcResponse.setContentType(arg0);
	}

	@Override
	public void setLocale(Locale arg0) {
		srcResponse.setLocale(arg0);
	}

	@Override
	public void addCookie(Cookie arg0) {
		srcResponse.addCookie(arg0);
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		srcResponse.addDateHeader(arg0, arg1);
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		srcResponse.addHeader(arg0, arg1);
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		srcResponse.addIntHeader(arg0, arg1);
	}

	@Override
	public boolean containsHeader(String arg0) {
		return srcResponse.containsHeader(arg0);
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		return srcResponse.encodeRedirectURL(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String encodeRedirectUrl(String arg0) {
		return srcResponse.encodeRedirectUrl(arg0);
	}

	@Override
	public String encodeURL(String arg0) {
		return srcResponse.encodeURL(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String encodeUrl(String arg0) {
		return srcResponse.encodeUrl(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		return srcResponse.getHeader(arg0);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return srcResponse.getHeaderNames();
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		return srcResponse.getHeaders(arg0);
	}

	@Override
	public int getStatus() {
		return srcResponse.getStatus();
	}

	@Override
	public void sendError(int arg0) throws IOException {
		srcResponse.sendError(arg0);
	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		srcResponse.sendError(arg0, arg1);
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		srcResponse.sendRedirect(arg0);
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		srcResponse.setDateHeader(arg0, arg1);
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		srcResponse.setHeader(arg0, arg1);
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		srcResponse.setIntHeader(arg0, arg1);
	}

	@Override
	public void setStatus(int arg0) {
		srcResponse.setStatus(arg0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setStatus(int arg0, String arg1) {
		srcResponse.setStatus(arg0, arg1);
	}

}
