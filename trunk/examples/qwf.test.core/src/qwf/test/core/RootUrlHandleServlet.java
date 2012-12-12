package qwf.test.core;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quickwebframework.framework.WebContext;

public class RootUrlHandleServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 564331169593901899L;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendRedirect(WebContext.getInstance().getBundleMethodUrl(
					Activator.getContext().getBundle().getSymbolicName(),
					"index"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
