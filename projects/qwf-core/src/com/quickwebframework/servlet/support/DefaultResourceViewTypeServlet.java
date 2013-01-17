package com.quickwebframework.servlet.support;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

import com.quickwebframework.framework.OsgiContext;
import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;
import com.quickwebframework.util.WebResourceUtils;

public class DefaultResourceViewTypeServlet extends ViewTypeServlet {

	private static final long serialVersionUID = -1402692734489050382L;

	public static final String RESOURCE_SERVLET = "qwf-core.DefaultResourceViewTypeServlet";
	public static final String RESOURCE_PATH_PREFIX_PROPERTY_KEY = "qwf-core.DefaultResourceViewTypeServlet.resourcePathPrefix";
	public static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-core.DefaultResourceViewTypeServlet.viewTypeName";

	// 资源路径统一前缀
	private String resourcePathPrefix;

	public DefaultResourceViewTypeServlet(String viewTypeName) {
		super(viewTypeName);

		resourcePathPrefix = WebContext
				.getQwfConfig(RESOURCE_PATH_PREFIX_PROPERTY_KEY);
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 获取插件名称与路径到request的属性中
		String pluginName = request.getAttribute(WebContext.CONST_PLUGIN_NAME)
				.toString();
		String pathName = request.getAttribute(WebContext.CONST_PATH_NAME)
				.toString();

		// 如果有统一前缀，则添加统一前缀
		if (resourcePathPrefix != null) {
			pathName = resourcePathPrefix + pathName;
		}

		// 安全检测

		// 如果请求的资源路径没有后缀，则不允许访问
		if (!pathName.contains(".")) {
			// 返回400 Bad Request
			response.sendError(400, "请求的资源[" + pathName + "]未包括后缀，不允许访问！");
			return;
		}

		// 查找资源
		Bundle bundle = OsgiContext.getBundleByName(pluginName);
		if (bundle == null) {
			response.sendError(404, String.format("未找到名称为[%s]的插件!", pluginName));
			return;
		}
		URL url = bundle.getResource(pathName);
		if (url == null) {
			response.sendError(404,
					String.format("在插件[%s]中未找到资源[%s]!", pluginName, pathName));
			return;
		}
		WebResourceUtils.outputResource(response, pathName, url.openStream());
	}

	@Override
	public String[] getUrls() {
		return null;
	}
}
