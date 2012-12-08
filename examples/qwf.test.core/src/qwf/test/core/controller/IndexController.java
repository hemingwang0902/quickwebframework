package qwf.test.core.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import qwf.test.core.Activator;

import com.quickwebframework.entity.HttpMethodInfo;
import com.quickwebframework.service.MvcFrameworkService;

@Controller
public class IndexController {

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index_get(HttpServletRequest request,
			HttpServletResponse response) {
		BundleContext bundleContext = Activator.getContext();
		ServiceReference<?> sr = bundleContext
				.getServiceReference(MvcFrameworkService.class.getName());
		if (sr == null) {
			throw new RuntimeException("未找到注册的MvcFrameworkService！");
		}
		MvcFrameworkService mvcFrameworkService = (MvcFrameworkService) bundleContext
				.getService(sr);

		try {
			Cookie[] c = request.getCookies();
			if (c == null) {
				response.addCookie(new Cookie("randomUUID", UUID.randomUUID()
						.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 得到插件名称与方法名称列表的MAP
		Map<String, List<HttpMethodInfo>> bundleNameHttpMethodInfoListMap = mvcFrameworkService
				.getBundleHttpMethodInfoListMap();
		request.setAttribute("bundleNameHttpMethodInfoListMap", bundleNameHttpMethodInfoListMap);
		return "index";
	}
}
