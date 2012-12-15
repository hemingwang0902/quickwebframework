package com.quickwebframework.bridge;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

import com.quickwebframework.entity.HandlerExceptionResolver;
import com.quickwebframework.entity.HttpMethodInfo;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.entity.MvcModelAndView;
import com.quickwebframework.framework.OsgiContext;
import com.quickwebframework.framework.WebContext;

public class HttpServletBridge extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6484809258029142503L;

	public static Log log = LogFactory.getLog(HttpServletBridge.class);
	public static final String ARG_BUNDLE_NAME = "com.quickwebframework.util.ARG_BUNDLE_NAME";
	public static final String ARG_METHOD_NAME = "com.quickwebframework.util.ARG_METHOD_NAME";
	public static final String ARG_RESOURCE_PATH = "com.quickwebframework.util.ARG_RESOURCE_PATH";
	public static final String ARG_RESOURCE_INPUTSTREAM = "com.quickwebframework.web.servlet.PluginResourceDispatcherServlet.ARG_RESOURCE_INPUTSTREAM";

	/**
	 * 渲染视图
	 * 
	 * @return
	 */
	public void renderView(MvcModelAndView mav, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (WebContext.getViewRenderService() != null) {
				// 渲染视图
				WebContext

				.getViewRenderService().renderView(
						mav.getBundle().getSymbolicName(), mav.getViewName(),
						request, response);
			} else {
				response.sendError(500,
						"[com.quickwebframework.core.DispatcherServlet] cannot found ViewRender!");
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void handleUrlNotFound(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		if (WebContext.getUrlNotFoundHandleServlet() == null)
			response.sendError(404, "URL " + request.getRequestURI()
					+ " not found!");
		else
			WebContext.getUrlNotFoundHandleServlet().service(request, response);
	}

	private void processHttp(HttpServletRequest request,
			HttpServletResponse response, String bundleName, String methodName) {
		try {
			// 如果插件名称为null或空字符串
			if (bundleName == null || bundleName.isEmpty()) {
				handleUrlNotFound(request, response);
				return;
			}

			try {
				MvcModelAndView mav = WebContext.getMvcFrameworkService()
						.handle(request, response, bundleName, methodName);
				if (mav == null) {
					return;
				}
				String viewName = mav.getViewName();

				// 如果视图不为空
				if (viewName != null) {
					renderView(mav, request, response);
				}
			} catch (Exception ex) {
				HandlerExceptionResolver resolver = WebContext
						.getHandlerExceptionResolver();

				if (resolver == null)
					throw ex;
				// 解决处理器异常
				resolver.resolveException(request, response, ex);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// 处理HTTP请求
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String requestURIWithoutContextPath = request.getRequestURI()
				.substring(request.getContextPath().length());
		if (requestURIWithoutContextPath.isEmpty())
			requestURIWithoutContextPath = "/";

		// 如果是根路径
		if ("/".equals(requestURIWithoutContextPath)) {
			serviceRootUrl(request, response);
			return;
		}

		Object bundleNameObject = request.getAttribute(ARG_BUNDLE_NAME);
		if (bundleNameObject == null) {
			processHttp(request, response, null, null);
			return;
		}
		String bundleName = bundleNameObject.toString();
		Object methodNameObject = request.getAttribute(ARG_METHOD_NAME);
		Object resourcePathObject = request.getAttribute(ARG_RESOURCE_PATH);
		// 如果是视图
		if (methodNameObject != null) {
			String methodName = methodNameObject.toString();
			processHttp(request, response, bundleName, methodName);
		}
		// 如果是资源
		else if (resourcePathObject != null) {
			String resourcePath = resourcePathObject.toString();
			request.setAttribute(ARG_RESOURCE_INPUTSTREAM,
					getBundleResource(bundleName, resourcePath));
		}
	}

	// 处理根URL："/"请求
	private void serviceRootUrl(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		if (WebContext.getRootUrlHandleServlet() == null) {
			response.setContentType("text/html;charset=utf-8");
			StringBuilder sb = new StringBuilder();
			sb.append("<html><head><title>Powered by QuickWebFramework</title></head><body>Welcome to use QuickWebFramework!You can manage bundles in the <a href=\"qwf/index\">Bundle Manage Page</a>!");
			if (WebContext.getMvcFrameworkService() != null) {
				Map<String, List<HttpMethodInfo>> map = WebContext
						.getMvcFrameworkService()
						.getBundleHttpMethodInfoListMap();
				sb.append("<table>");
				for (String bundleName : map.keySet()) {
					List<HttpMethodInfo> httpMethodInfoList = map
							.get(bundleName);
					if (httpMethodInfoList.isEmpty())
						continue;
					sb.append("<tr><td><b>" + bundleName + "</b></td></tr>");
					for (HttpMethodInfo httpMethodInfo : httpMethodInfoList) {
						String url = httpMethodInfo.getMappingUrl();
						String httpMethod = httpMethodInfo.getHttpMethod();
						sb.append("<tr><td><a style=\"margin-left:20px\" href=\""
								+ url
								+ "\">"
								+ url
								+ "</a>("
								+ httpMethod
								+ ")</td></tr>");
					}
				}
				sb.append("</table>");
			}
			sb.append("</body></html>");
			response.getWriter().write(sb.toString());
			return;
		}
		WebContext.getRootUrlHandleServlet().service(request, response);
	}

	// 得到Bundle资源
	private InputStream getBundleResource(String bundleName, String resourcePath)
			throws IOException {
		Bundle bundle = OsgiContext.getBundleByName(bundleName);
		URL resourceUrl = bundle.getResource(resourcePath);
		if (resourceUrl == null)
			return null;
		return resourceUrl.openStream();
	}
}
