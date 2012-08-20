package com.quickwebframework.proxy;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletOutputStream;

public class PluginServletOutputStream extends ServletOutputStream {

	private Object orginObject;
	private Method writeMethod;

	public PluginServletOutputStream(Object orginObject) {
		this.orginObject = orginObject;
		try {
			writeMethod = ServletOutputStream.class.getMethod("write",
					int.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void write(int arg0) throws IOException {
		try {
			writeMethod.invoke(orginObject, arg0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
