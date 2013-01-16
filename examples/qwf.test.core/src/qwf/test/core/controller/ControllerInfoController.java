package qwf.test.core.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.quickwebframework.framework.WebContext;

@Controller
public class ControllerInfoController {

	@RequestMapping(value = "controller", method = RequestMethod.GET)
	public String get_controller(HttpServletRequest request,
			HttpServletResponse response) {
		// 得到插件名称与方法名称列表的MAP
		/*
		Map<String, List<HttpMethodInfo>> bundleNameHttpMethodInfoListMap = WebContext
				.getMvcFrameworkService().getBundleHttpMethodInfoListMap();
		request.setAttribute("bundleNameHttpMethodInfoListMap",
				bundleNameHttpMethodInfoListMap);
		*/
		return "controller";
	}
}
