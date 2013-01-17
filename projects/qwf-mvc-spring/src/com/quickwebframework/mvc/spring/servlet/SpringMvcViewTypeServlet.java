package com.quickwebframework.mvc.spring.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.quickwebframework.framework.WebContext;
import com.quickwebframework.mvc.spring.SpringMvcContext;
import com.quickwebframework.servlet.ViewTypeServlet;
import com.quickwebframework.viewrender.ViewRenderContext;
import com.quickwebframework.viewrender.ViewRenderService;

public class SpringMvcViewTypeServlet extends ViewTypeServlet {

	private static final long serialVersionUID = -6768041494697468584L;
	public static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-mvc-spring.SpringMvcViewTypeServlet.viewTypeName";

	public SpringMvcViewTypeServlet(String viewTypeName) {
		super(viewTypeName);
	}

	@Override
	public String[] getUrls() {
		return null;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String pluginName = request.getAttribute(WebContext.CONST_PLUGIN_NAME)
				.toString();
		String pathName = request.getAttribute(WebContext.CONST_PATH_NAME)
				.toString();
		// 处理
		ModelAndView mav = SpringMvcContext.getSpringMvcFrameworkService()
				.handle(request, response, pluginName, pathName);
		if (mav != null) {
			renderView(request, response, pluginName, mav);
			return;
		}
	}

	/**
	 * 渲染视图
	 * 
	 * @return
	 */
	public void renderView(HttpServletRequest request,
			HttpServletResponse response, String pluginName, ModelAndView mav) {
		try {
			ViewRenderService viewRenderService = ViewRenderContext
					.getViewRenderService();
			if (viewRenderService != null) {
				String viewName = mav.getViewName();
				if (!viewName.contains(viewRenderService
						.getPluginNameAndPathSplitString())) {
					viewName = pluginName
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
}
