package qwf.test.core.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class Index2Controller {
	@RequestMapping(value = "index", method = RequestMethod.POST)
	public String index_POST(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.getWriter().write("This is a post method!");
		return null;
	}
}
