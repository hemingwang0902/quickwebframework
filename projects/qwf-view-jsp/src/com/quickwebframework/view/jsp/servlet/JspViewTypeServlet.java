package com.quickwebframework.view.jsp.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;

import com.quickwebframework.framework.OsgiContext;
import com.quickwebframework.framework.WebContext;
import com.quickwebframework.servlet.ViewTypeServlet;

public class JspViewTypeServlet extends ViewTypeServlet {
	private Log log = LogFactory.getLog(JspViewTypeServlet.class);

	private static final long serialVersionUID = 3719762515648054933L;
	public static final String VIEW_TYPE_NAME_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.viewTypeName";
	public static final String JSP_PATH_PREFIX_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.jspPathPrefix";
	public static final String JSP_PATH_SUFFIX_PROPERTY_KEY = "qwf-view-jsp.JspViewTypeServlet.jspPathSuffix";

	// JSP路径前缀
	private String jspPathPrefix;
	// JSP路径后缀
	private String jspPathSuffix;

	public JspViewTypeServlet(String viewTypeName) {
		super(viewTypeName);
		jspPathPrefix = WebContext.getQwfConfig(JSP_PATH_PREFIX_PROPERTY_KEY);
		jspPathSuffix = WebContext.getQwfConfig(JSP_PATH_SUFFIX_PROPERTY_KEY);
		if (jspPathSuffix == null || jspPathSuffix.isEmpty()) {
			jspPathSuffix = ".jsp";
		}
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
		if (jspPathPrefix != null) {
			pathName = jspPathPrefix + pathName;
		}
		if (jspPathSuffix != null) {
			pathName = pathName + jspPathSuffix;
		}
		Bundle bundle = OsgiContext.getBundleByName(pluginName);
		URL url = bundle.getResource(pathName);
		if (url == null) {
			response.sendError(404, "在插件[" + pluginName + "]中未找到[" + pathName
					+ "]资源！");
			return;
		}

		String outputPath = WebContext.getRealPath("WEB-INF/jspoutput");

		String srcJspFolderPath = outputPath + "/src";
		File tmpFile = new File(srcJspFolderPath + "/" + pathName);

		// 创建目录
		tmpFile.getParentFile().mkdirs();
		// 将文件复制到临时文件
		FileUtils.copyURLToFile(url, tmpFile);

		try {
			org.apache.jasper.JspC jspC = new org.apache.jasper.JspC();

			jspC.setJavaEncoding("utf-8");
			jspC.setWebXmlEncoding("utf-8");
			jspC.setUriroot(srcJspFolderPath);
			jspC.setOutputDir(outputPath);
			jspC.setJspFiles(pathName);
			jspC.setCompile(true);
			jspC.execute();
		} catch (Exception ex) {
			log.error("编译JSP文件时出错！", ex);
			response.sendError(500, "编译JSP文件时出错！");
			return;
		} finally {
			// 删除临时文件
			//FileUtils.deleteDirectory(new File(srcJspFolderPath));
			// FileUtils.deleteDirectory(new File(outputPath));
		}
		response.getWriter().write("Found jsp file:" + pathName);
	}
}
