package com.quickwebframework.proxy;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

public class PluginServletInputStream extends ServletInputStream {
	private Object orginObject;
	private Method readMethod;

	public PluginServletInputStream(Object orginObject) {
		this.orginObject = orginObject;
		try {
			readMethod = ServletInputStream.class.getMethod("read");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read() throws IOException {
		try {
			return (Integer) readMethod.invoke(orginObject);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
