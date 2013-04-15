package com.dynamic.model.project.model;

import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

import com.dynamic.model.project.util.ProjectUtil;

public abstract class ServerOsgiModel implements IServerOsgiModel {
	//服务器
	private IServer server;
	//主机
	private String host;
	//VM_ARGUMENTS
	private static String JVM_ARGU = "org.eclipse.jdt.launching.VM_ARGUMENTS";
	//模块组
	private IModule[] modules;
	/***
	 * 构造函数
	 * @param server
	 */
	public ServerOsgiModel(IServer server) {
		this.server = server;
		if (server != null) {
			this.host = server.getHost();
			this.modules = server.getModules();
			analysisJvmArgu();
		}
	}
	/***
	 * 获得模块组
	 */
	public IModule[] getModules() {
		return this.modules;
	}
	/***
	 * 获得唯一宿主
	 * @param hostModule
	 */
	public IModule getHostModule() {
		for(IModule module : this.server.getModules()){
			if(ProjectUtil.isClientProject(module.getProject())){
				return module;
			}
		}
		return null;
	}
	/***
	 * 获得模服务器
	 */
	public IServer getServer() {
		return this.server;
	}
	/***
	 * 获得主机
	 */
	public String getHost() {
		return this.host;
	}
	/***
	 * 获得部署路径
	 */
	public abstract String getDeployPath();
	/***
	 * 运行状态
	 */
	public boolean isRunning() {
		if (this.server == null)
			return false;
		return (this.server.getLaunch() != null);
	}
	/***
	 * 分析jvm参数
	 */
	public void analysisJvmArgu() {
		try {
			ILaunchConfiguration config = this.server.getLaunchConfiguration(
					false, new NullProgressMonitor());
			String argus = (String) config.getAttributes().get(JVM_ARGU);
			parser(argus);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 解析参数
	 * @param paramString
	 */
	public abstract void parser(String paramString);
	/****
	 * web上下文
	 * @param module
	 * @return eg：dynamic.web
	 */
	public String getModuleContextPath(IModule module) {
		String result = "";
		IFile componentFile = module.getProject().getFile(
				".settings/org.eclipse.wst.common.component");
		if (!(componentFile.exists()))
			return result;

		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(componentFile.getContents());
			Element projecModules = document.getRootElement();
			Element webModules = projecModules.element("wb-module");
			List<Element> propertyList = webModules.elements("property");
			for (Element property : propertyList) {
				Attribute att = property.attribute("name");
				if (att.getValue().equals("context-root"))
					result = property.attribute("value").getValue();
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return result;
	}
}