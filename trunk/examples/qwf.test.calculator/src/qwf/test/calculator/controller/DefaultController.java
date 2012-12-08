package qwf.test.calculator.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import qwf.test.core.Config;

@Controller
public class DefaultController {

	@Autowired
	private Config config;

	@RequestMapping(value = "index")
	public String index_get(HttpServletRequest request,
			HttpServletResponse response) {
		return null;
	}
}
