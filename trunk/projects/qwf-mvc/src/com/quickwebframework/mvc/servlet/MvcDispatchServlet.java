package com.quickwebframework.mvc.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.mvc.MvcModelAndView;
import com.quickwebframework.viewrender.ViewRenderContext;
import com.quickwebframework.viewrender.ViewRenderService;

public class MvcDispatchServlet extends HttpServlet {
	/**
	 * 渲染视图
	 * 
	 * @return
	 */
	public void renderView(MvcModelAndView mav, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ViewRenderService viewRenderService = ViewRenderContext
					.getViewRenderService();
			if (viewRenderService != null) {
				// 渲染视图
				viewRenderService.renderView(request, response,
						mav.getViewName(), mav.getModel());
			} else {
				response.sendError(500, String.format("[%s]未找到视图渲染器服务!", this
						.getClass().getName()));
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
