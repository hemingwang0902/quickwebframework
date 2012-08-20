package com.quickwebframework.proxy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class PluginHttpServletResponse extends MapperObject implements
		HttpServletResponse {

	public PluginHttpServletResponse(Object orginObject) {
		super(orginObject);
	}

	@Override
	public void flushBuffer() throws IOException {
		invokeOrginObjectMethod();
	}

	@Override
	public int getBufferSize() {
		return (Integer) invokeOrginObjectMethod();
	}

	@Override
	public String getCharacterEncoding() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public String getContentType() {
		return (String) invokeOrginObjectMethod();
	}

	@Override
	public Locale getLocale() {
		return (Locale) invokeOrginObjectMethod();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new PluginServletOutputStream(invokeOrginObjectMethod());
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return (PrintWriter) invokeOrginObjectMethod();
	}

	@Override
	public boolean isCommitted() {
		return (Boolean) invokeOrginObjectMethod();
	}

	@Override
	public void reset() {
		invokeOrginObjectMethod();
	}

	@Override
	public void resetBuffer() {
		invokeOrginObjectMethod();
	}

	@Override
	public void setBufferSize(int arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void setContentLength(int arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void setContentType(String arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void setLocale(Locale arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void addCookie(Cookie arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public boolean containsHeader(String arg0) {
		return (Boolean) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		return (String) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		return (String) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String encodeURL(String arg0) {
		return (String) invokeOrginObjectMethod(arg0);
	}

	@Override
	public String encodeUrl(String arg0) {
		return (String) invokeOrginObjectMethod(arg0);
	}

	@Override
	public void sendError(int arg0) throws IOException {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}

	@Override
	public void setStatus(int arg0) {
		invokeOrginObjectMethod(arg0);
	}

	@Override
	public void setStatus(int arg0, String arg1) {
		invokeOrginObjectMethod(arg0, arg1);
	}
}
