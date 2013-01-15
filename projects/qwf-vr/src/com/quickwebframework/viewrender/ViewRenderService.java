package com.quickwebframework.viewrender;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.quickwebframework.framework.WebContext;

/**
 * 视图渲染服务
 * 
 * @author aaa
 * 
 */
public abstract class ViewRenderService {

	private static Log log = LogFactory.getLog(ViewRenderService.class
			.getName());

	// 视图渲染器配置键
	public final static String CONFIG_QWF_VIEW_RENDER_PROP = "qwf-core.viewrender.properties";
	// 插件名称与路径分隔字符串配置键
	public final static String CONFIG_PLUGIN_NAME_AND_PATH_SPLIT_STRING = "com.quickwebframework.viewrender.pluginNameAndPathSplitString";
	// 视图名称统一前缀配置键
	public final static String CONFIG_VIEW_NAME_PREFIX = "com.quickwebframework.viewrender.viewNamePrefix";
	// 视图名称统一后缀配置键
	public final static String CONFIG_VIEW_NAME_SUFFIX = "com.quickwebframework.viewrender.viewNameSuffix";

	// 插件名称与路径分隔符
	private String pluginNameAndPathSplitString = ":";
	// 视图名称前缀
	private String viewNamePrefix = "";
	// 视图名称后缀
	private String viewNameSuffix = ".html";

	public String getPluginNameAndPathSplitString() {
		return pluginNameAndPathSplitString;
	}

	public void setPluginNameAndPathSplitString(
			String pluginNameAndPathSplitString) {
		this.pluginNameAndPathSplitString = pluginNameAndPathSplitString;
	}

	public String getViewNamePrefix() {
		return viewNamePrefix;
	}

	public void setViewNamePrefix(String viewNamePrefix) {
		this.viewNamePrefix = viewNamePrefix;
	}

	public String getViewNameSuffix() {
		return viewNameSuffix;
	}

	public void setViewNameSuffix(String viewNameSuffix) {
		this.viewNameSuffix = viewNameSuffix;
	}

	public ViewRenderService() {
		// 得到viewrender配置文件路径
		String viewrenderPropertyFilePath = WebContext
				.getQwfConfig(CONFIG_QWF_VIEW_RENDER_PROP);
		if (viewrenderPropertyFilePath == null
				|| viewrenderPropertyFilePath.isEmpty()) {
			log.info("ViewRender use default setting.");
			return;
		}

		viewrenderPropertyFilePath = WebContext
				.getRealPath(viewrenderPropertyFilePath);
		// 读取viewrender配置文件
		Properties viewRenderProp = getProperties(viewrenderPropertyFilePath);

		// 分隔符
		if (viewRenderProp
				.containsKey(CONFIG_PLUGIN_NAME_AND_PATH_SPLIT_STRING)) {
			setPluginNameAndPathSplitString(viewRenderProp
					.getProperty(CONFIG_PLUGIN_NAME_AND_PATH_SPLIT_STRING));
		}
		// 前缀
		if (viewRenderProp.containsKey(CONFIG_VIEW_NAME_PREFIX)) {
			setViewNamePrefix(viewRenderProp
					.getProperty(CONFIG_VIEW_NAME_PREFIX));
		}
		// 后缀
		if (viewRenderProp.containsKey(CONFIG_VIEW_NAME_SUFFIX)) {
			setViewNameSuffix(viewRenderProp
					.getProperty(CONFIG_VIEW_NAME_SUFFIX));
		}
	}

	// 得到配置信息
	private Properties getProperties(String fileName) {
		File file = new File(fileName);
		if (!file.exists() || !file.isFile()) {
			String message = String.format("Properties file [%s] not exist!",
					fileName);
			throw new RuntimeException(message);
		}
		try {
			InputStream inputStream = new FileInputStream(fileName);
			Reader reader = new InputStreamReader(inputStream, "utf-8");
			Properties viewrenderProp = new Properties();
			viewrenderProp.load(reader);
			reader.close();
			inputStream.close();
			return viewrenderProp;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public abstract void renderView(HttpServletRequest request,
			HttpServletResponse response, String viewName,
			Map<String, Object> model);
}
