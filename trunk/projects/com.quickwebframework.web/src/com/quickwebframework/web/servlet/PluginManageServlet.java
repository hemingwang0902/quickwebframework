package com.quickwebframework.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.quickwebframework.web.util.IoUtil;
import com.quickwebframework.web.listener.QuickWebFrameworkLoaderListener;

public class PluginManageServlet extends javax.servlet.http.HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -370194350835819493L;
	public static final String MAPPING_PROPERTY_KEY = "quickwebframework.pluginManageServlet.mapping";

	// 模板字符串
	private String templateString;

	public PluginManageServlet() {
		try {
			InputStream inputStream = this
					.getClass()
					.getClassLoader()
					.getResourceAsStream(
							"com/quickwebframework/web/template/bundleManage.txt");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			IoUtil.copyStream(inputStream, outputStream);
			inputStream.close();
			templateString = outputStream.toString("utf-8");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	// 初始化插件管理Servlet
	public static HttpServlet initServlet(ServletContext servletContext,
			Properties quickWebFrameworkProperties) {

		String pluginManageDispatcherServletMapping = quickWebFrameworkProperties
				.getProperty(PluginManageServlet.MAPPING_PROPERTY_KEY);
		if (pluginManageDispatcherServletMapping == null)
			return null;

		// 添加插件管理Servlet
		PluginManageServlet pluginManageServlet = new PluginManageServlet();
		return pluginManageServlet;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		BundleContext bundleContext = QuickWebFrameworkLoaderListener
				.getBundleContext();

		if (bundleContext == null) {
			System.out
					.println("BundleContext is null,Current OSGi Framework's state is "
							+ QuickWebFrameworkLoaderListener.getFramework()
									.getState()
							+ ",now trying to start OSGi Framework!"
							+ "\n\nPS:UNINSTALLED = 1"
							+ "\nINSTALLED = 2"
							+ "\nRESOLVED = 4"
							+ "\nSTARTING = 8"
							+ "\nSTOPPING = 16" + "\nACTIVE = 32");
			try {
				QuickWebFrameworkLoaderListener.getFramework().start();
				bundleContext = QuickWebFrameworkLoaderListener
						.getBundleContext();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		Bundle[] bundles = bundleContext.getBundles();

		StringBuilder sbPart0 = new StringBuilder();
		sbPart0.append("<table border=\"1\">");
		sbPart0.append("<thead>");
		sbPart0.append("<tr>");
		sbPart0.append("<th>ID</th>");
		sbPart0.append("<th>符号名称</th>");
		sbPart0.append("<th>名称</th>");
		sbPart0.append("<th>版本</th>");
		sbPart0.append("<th>状态</th>");
		sbPart0.append("<th>操作</th>");
		sbPart0.append("</tr>");
		sbPart0.append("</thead>");
		sbPart0.append("<tbody>");
		for (Bundle bundle : bundles) {
			sbPart0.append("<tr>");
			sbPart0.append("<td>").append(bundle.getBundleId()).append("</td>");
			sbPart0.append("<td>").append(bundle.getSymbolicName())
					.append("</td>");
			String bundleName = "";
			try {
				bundleName = bundle.getHeaders().get("Bundle-Name");
				if (bundleName != null && !bundleName.isEmpty())
					bundleName = new String(bundleName.getBytes(), "utf-8");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			sbPart0.append("<td>").append(bundleName).append("</td>");
			sbPart0.append("<td>").append(bundle.getVersion()).append("</td>");
			sbPart0.append("<td>");
			String stateString = null;
			if (bundle.getState() == Bundle.UNINSTALLED)
				stateString = "已卸载";
			else if (bundle.getState() == Bundle.INSTALLED)
				stateString = "已安装";
			else if (bundle.getState() == Bundle.RESOLVED)
				stateString = "已解析";
			else if (bundle.getState() == Bundle.STARTING)
				stateString = "启动中";
			else if (bundle.getState() == Bundle.STOPPING)
				stateString = "停止中";
			else if (bundle.getState() == Bundle.ACTIVE)
				stateString = "激活";
			sbPart0.append(stateString);
			sbPart0.append("</td>");
			sbPart0.append("<td>");
			sbPart0.append("<div style=\"float:left\">");
			sbPart0.append("<form method=\"post\">");
			sbPart0.append("<input id=\"hiddenPluginOperateMod_")
					.append(bundle.getBundleId())
					.append("\" type=\"hidden\" name=\"mod\" value=\"\" />");
			sbPart0.append("<input type=\"hidden\" name=\"pluginId\" value=\"")
					.append(bundle.getBundleId()).append("\" />");

			if (bundle.getState() == Bundle.INSTALLED
					|| bundle.getState() == Bundle.RESOLVED) {
				sbPart0.append(
						"<input type=\"submit\" class=\"button\" value=\"启动\" onclick=\"document.getElementById('hiddenPluginOperateMod_")
						.append(bundle.getBundleId())
						.append("').value='startPlugin'\" />");
			} else if (bundle.getState() == Bundle.ACTIVE) {
				sbPart0.append(
						"<input type=\"submit\" class=\"button\" value=\"停止\" onclick=\"document.getElementById('hiddenPluginOperateMod_")
						.append(bundle.getBundleId())
						.append("').value='stopPlugin'\" />");
			}
			sbPart0.append(
					"<input type=\"submit\" class=\"button\" value=\"卸载\" onclick=\"document.getElementById('hiddenPluginOperateMod_")
					.append(bundle.getBundleId())
					.append("').value='uninstallPlugin'\" />");
			sbPart0.append("</form>");
			sbPart0.append("</div>");
			sbPart0.append("</td>");
			sbPart0.append("</tr>");
		}
		sbPart0.append("</tbody>");
		sbPart0.append("</table>");

		Object messageObj = request.getAttribute("message");
		if (messageObj != null) {
			sbPart0.append("<table border=\"1\">");
			sbPart0.append("<thead>");
			sbPart0.append("<tr>");
			sbPart0.append("<th>消息</th>");
			sbPart0.append("</tr>");
			sbPart0.append("</thead>");
			sbPart0.append("<tbody>");
			sbPart0.append("<tr>");
			sbPart0.append("<td>");
			sbPart0.append("<p>").append(messageObj).append("</p>");
			sbPart0.append("</td>");
			sbPart0.append("</tr>");
			sbPart0.append("</tbody>");
			sbPart0.append("</table>");
		}

		String outputString = templateString;
		outputString = outputString.replace("{0}", sbPart0.toString());

		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html");
			response.getWriter().write(outputString);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void pushMessage(HttpServletRequest request, String message) {
		request.setAttribute("message", message);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		BundleContext bundleContext = QuickWebFrameworkLoaderListener
				.getBundleContext();

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		// 如果Request是Multipart
		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<?> items;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e1) {
				e1.printStackTrace();
				pushMessage(request, "upload.parseRequest(request)异常," + e1);
				doGet(request, response);
				return;
			}

			Properties formFieldProperties = new Properties();
			Map<String, FileItem> formFileMap = new HashMap<String, FileItem>();

			for (Object obj : items) {
				FileItem item = (FileItem) obj;
				// 如果是表单字段
				if (item.isFormField()) {
					formFieldProperties.setProperty(item.getFieldName(),
							item.getString());
				}
				// 否则是上传文件
				else {
					formFileMap.put(item.getFieldName(), item);
				}
			}

			String mod = formFieldProperties.getProperty("mod");

			// 重启框架
			if ("restartFramework".equals(mod)) {
				try {
					// 更新OSGi Framework
					QuickWebFrameworkLoaderListener.getFramework().update();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
			// 安装插件
			else if ("installPlugin".equals(mod)) {
				if (!formFileMap.containsKey("pluginFile")) {
					pushMessage(request, "未找到pluginFile参数！");
					doGet(request, response);
					return;
				}
				FileItem pluginFile = formFileMap.get("pluginFile");

				// 得到临时目录路径
				String tmpFolderPath = System.getProperty("java.io.tmpdir");
				// 插件随机文件路径
				String randomFilePath = tmpFolderPath + File.separator
						+ "pluginpkg_cache" + UUID.randomUUID().toString()
						+ ".tmp";

				File randomFile = new File(randomFilePath);
				try {
					pluginFile.write(randomFile);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}

				try {
					Bundle bundle = bundleContext.installBundle(randomFile
							.toURI().toURL().toString());
					bundle.start();

				} catch (Exception e) {
					e.printStackTrace();
					pushMessage(request, "安装插件时出错异常，" + e);
					doGet(request, response);
					return;
				} finally {
					randomFile.delete();
				}
			} else if ("updatePlugin".equals(mod)) {
				if (!formFileMap.containsKey("pluginFile")) {
					pushMessage(request, "未找到pluginFile参数！");
					doGet(request, response);
					return;
				}
				FileItem pluginFile = formFileMap.get("pluginFile");

				// 得到临时目录路径
				String tmpFolderPath = System.getProperty("java.io.tmpdir");
				// 插件随机文件路径
				String randomFilePath = tmpFolderPath + File.separator
						+ "pluginpkg_cache" + UUID.randomUUID().toString()
						+ ".tmp";

				File randomFile = new File(randomFilePath);
				try {
					pluginFile.write(randomFile);
				} catch (Exception ex) {
					pushMessage(request, "上传文件时出错：" + ex);
					doGet(request, response);
					return;
				}
				try {
					// 插件的符号名称
					String bundleSymbolicName;

					ZipFile pluginZipFile = null;
					try {
						pluginZipFile = new ZipFile(randomFile);
						ZipEntry manifestZipEntry = pluginZipFile
								.getEntry("META-INF/MANIFEST.MF");
						if (manifestZipEntry == null) {
							throw new RuntimeException(
									"未找到META-INF/MANIFEST.MF文件");
						}
						InputStream manifestInputStream = pluginZipFile
								.getInputStream(manifestZipEntry);

						Properties manifestProp = new Properties();
						manifestProp.load(manifestInputStream);
						manifestInputStream.close();
						bundleSymbolicName = manifestProp
								.getProperty("Bundle-SymbolicName");
						if (bundleSymbolicName == null
								|| bundleSymbolicName.isEmpty()) {
							throw new RuntimeException("未找到Bundle-SymbolicName");
						}
					} catch (Exception ex) {
						pushMessage(request, "读取清单文件时出错，" + ex);
						if (pluginZipFile != null) {
							try {
								pluginZipFile.close();
							} catch (Exception ex2) {
							}
						}
						randomFile.delete();
						doGet(request, response);
						return;
					}
					// 关闭ZipFile
					pluginZipFile.close();

					Bundle bundle = null;
					for (Bundle tmpBundle : bundleContext.getBundles()) {
						if (tmpBundle.getSymbolicName().equals(
								bundleSymbolicName)) {
							bundle = tmpBundle;
							break;
						}
					}
					if (bundle == null) {
						pushMessage(request, String.format("未找到插件[%s]，无法更新!",
								bundleSymbolicName));
						doGet(request, response);
						return;
					}

					InputStream inputStream = new FileInputStream(randomFile);
					bundle.update(inputStream);
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
					pushMessage(request, "更新插件时异常，" + e);
					doGet(request, response);
					return;
				} catch (Error e) {
					e.printStackTrace();
					pushMessage(request, "更新插件时出错，" + e);
					doGet(request, response);
					return;
				} finally {
					randomFile.delete();
				}
			}
		} else {
			String mod = request.getParameter("mod");
			if ("uninstallPlugin".equals(mod)) {
				String pluginIdStr = request.getParameter("pluginId");
				Long pluginId = Long.valueOf(pluginIdStr);
				Bundle bundle = bundleContext.getBundle(pluginId);
				try {
					bundle.uninstall();
				} catch (Exception ex) {
					ex.printStackTrace();
					pushMessage(request, "卸载插件时异常，" + ex);
					doGet(request, response);
					return;
				}
			} else if ("stopPlugin".equals(mod)) {
				String pluginIdStr = request.getParameter("pluginId");
				Long pluginId = Long.valueOf(pluginIdStr);
				Bundle bundle = bundleContext.getBundle(pluginId);
				try {
					bundle.stop();
				} catch (Exception ex) {
					ex.printStackTrace();
					pushMessage(request, "停止插件时异常，" + ex);
					doGet(request, response);
					return;
				}
			} else if ("startPlugin".equals(mod)) {
				String pluginIdStr = request.getParameter("pluginId");
				Long pluginId = Long.valueOf(pluginIdStr);
				Bundle bundle = bundleContext.getBundle(pluginId);
				try {
					bundle.start();
				} catch (Exception ex) {
					pushMessage(request, "启动插件时异常，" + ex);
					ex.printStackTrace();
					doGet(request, response);
					return;
				}
			}
		}
		doGet(request, response);
		return;
	}
}
