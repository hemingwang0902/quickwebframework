package com.quickwebframework.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.framework.WebContext;

/**
 * 默认处理根路径的Servlet
 * 
 * @author AAA
 * 
 */
public class DefaultRootServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8318707530041789685L;
	private static HttpServlet instance;

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static HttpServlet getInstance() {
		if (instance == null) {
			instance = new DefaultRootServlet();
		}
		return instance;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html;charset=utf-8");
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>Powered by QuickWebFramework</title></head><body>Welcome to use QuickWebFramework!You can manage bundles in the <a href=\"qwf/index\">Bundle Manage Page</a>!");
		String[] allServletPaths = WebContext.getAllServletPaths();
		if (allServletPaths != null && allServletPaths.length > 0) {
			sb.append("<table>");
			sb.append("<tr><td><b>==Java Servlet部分==</b></td></tr>");
			for (String servletPath : allServletPaths) {
				sb.append("<tr><td><a style=\"margin-left:20px\" href=\""
						+ servletPath + "\">" + servletPath + "</a></td></tr>");
			}
			sb.append("</table>");
		}
		/*
		 * if (WebContext.getMvcFrameworkService() != null) { Map<String,
		 * List<HttpMethodInfo>> map = WebContext
		 * .getMvcFrameworkService().getBundleHttpMethodInfoListMap();
		 * sb.append("<table>");
		 * sb.append("<tr><td><b>==MVC部分==</b></td></tr>"); for (String
		 * bundleName : map.keySet()) { List<HttpMethodInfo> httpMethodInfoList
		 * = map.get(bundleName); if (httpMethodInfoList.isEmpty()) continue;
		 * sb.append("<tr><td><b>" + bundleName + "</b></td></tr>"); for
		 * (HttpMethodInfo httpMethodInfo : httpMethodInfoList) { String url =
		 * httpMethodInfo.getMappingUrl(); String httpMethod =
		 * httpMethodInfo.getHttpMethod();
		 * sb.append("<tr><td><a style=\"margin-left:20px\" href=\"" + url +
		 * "\">" + url + "</a>(" + httpMethod + ")</td></tr>"); } }
		 * sb.append("</table>"); }
		 */
		sb.append("</body></html>");
		response.getWriter().write(sb.toString());
	}
}
