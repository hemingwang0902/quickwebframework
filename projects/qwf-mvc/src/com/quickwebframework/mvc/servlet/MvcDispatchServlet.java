package com.quickwebframework.mvc.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.mvc.MvcContext;
import com.quickwebframework.mvc.MvcFrameworkService;
import com.quickwebframework.mvc.MvcModelAndView;
import com.quickwebframework.viewrender.ViewRenderContext;
import com.quickwebframework.viewrender.ViewRenderService;

public class MvcDispatchServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9011381588046624048L;
	private static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-mvc.MvcDispatchServlet.viewTypeName";

	private String viewTypeName;

	public String getViewTypeName() {
		return viewTypeName;
	}

	public MvcDispatchServlet() {
		viewTypeName = WebContext.getQwfConfig(VIEW_TYPE_NAME_PROPERTY_KEY);
	}

	/**
	 * 渲染视图
	 * 
	 * @return
	 */
	public void renderView(HttpServletRequest request,
			HttpServletResponse response, MvcModelAndView mav) {
		try {
			ViewRenderService viewRenderService = ViewRenderContext
					.getViewRenderService();
			if (viewRenderService != null) {
				String viewName = mav.getViewName();
				if (!viewName.contains(viewRenderService
						.getPluginNameAndPathSplitString())) {
					viewName = mav.getBundle().getSymbolicName()
							+ viewRenderService
									.getPluginNameAndPathSplitString()
							+ viewName;
				}
				// 渲染视图
				viewRenderService.renderView(request, response, viewName,
						mav.getModel());
			} else {
				response.sendError(500, String.format("[%s]未找到视图渲染器服务!", this
						.getClass().getName()));
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 获取插件名称与路径到request的属性中
		String pluginName = request.getAttribute(WebContext.CONST_PLUGIN_NAME)
				.toString();
		String pathName = request.getAttribute(WebContext.CONST_PATH_NAME)
				.toString();

		MvcFrameworkService mvcFrameworkService = MvcContext
				.getMvcFrameworkService();
		if (mvcFrameworkService == null) {
			response.sendError(500, "未找到MvcFrameworkService服务对象！");
			return;
		}
		MvcModelAndView mav = mvcFrameworkService.handle(request, response,
				pluginName, pathName);
		if (mav != null) {
			renderView(request, response, mav);
			return;
		}
	}
}
