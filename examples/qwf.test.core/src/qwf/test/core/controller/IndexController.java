package qwf.test.core.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.quickwebframework.core.Activator;
import com.quickwebframework.service.MvcFrameworkService;

@Controller
public class IndexController {

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index_get(HttpServletRequest request) {
		BundleContext bundleContext = Activator.getContext();
		ServiceReference<?> sr = bundleContext
				.getServiceReference(MvcFrameworkService.class.getName());
		if (sr == null) {
			throw new RuntimeException("未找到注册的MvcFrameworkService！");
		}
		MvcFrameworkService mvcFrameworkService = (MvcFrameworkService) bundleContext
				.getService(sr);

		// 得到插件名称与方法名称列表的MAP
		Map<String, List<String>> bundleNameUrlListMap = mvcFrameworkService
				.getBundleNameUrlListMap();
		request.setAttribute("bundleNameUrlListMap", bundleNameUrlListMap);
		return "index";
	}
}
