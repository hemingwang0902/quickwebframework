package qwf.test.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index_get(HttpServletRequest request,
			HttpServletResponse response) {
		return "index";
	}
}
