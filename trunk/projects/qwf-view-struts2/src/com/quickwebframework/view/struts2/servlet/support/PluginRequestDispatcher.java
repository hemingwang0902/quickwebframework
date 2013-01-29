package com.quickwebframework.view.struts2.servlet.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class PluginRequestDispatcher implements RequestDispatcher {

	private URL url;

	public PluginRequestDispatcher(URL url) {
		this.url = url;
	}

	@Override
	public void forward(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		HttpServletResponse response = (HttpServletResponse) arg1;
		InputStream input = url.openStream();
		OutputStream output = response.getOutputStream();
		IOUtils.copy(input, output);
		output.flush();
		output.close();
		input.close();
	}

	@Override
	public void include(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		throw new ServletException("此方法还未实现！");
	}
}
