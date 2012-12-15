package qwf.test.calculator.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
	@RequestMapping(value = "index")
	public String index_get(HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}
}
