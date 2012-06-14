package com.quickwebframework.mvc.spring.util;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import org.osgi.framework.Bundle;

public class FolderClassLoader extends ClassLoader {
	private String folderPathURI;

	public FolderClassLoader(ClassLoader parent, String folderPath) {
		super(parent);
		folderPathURI = new File(folderPath).toURI().toString();
	}

	@Override
	public URL getResource(String name) {
		try {
			return new URL(folderPathURI + name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Enumeration<URL> getResources(String name) {
		try {
			Vector<URL> v = new Vector<URL>();
			v.add(getResource(name));
			return v.elements();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}