package com.dynamic.model.project.model;

import java.io.File;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

import com.dynamic.model.project.Constants;
import com.dynamic.model.project.util.Console;

public class TomcatServerOsgiModel extends ServerOsgiModel {
	//dcatalina.base
	private static String DCATALINA_BASE = "Dcatalina.base";
	//dcatalina.home
	private static String DCATALINA_HOME = "Dcatalina.home";
	//dwtp.deploy
	private static String DCATALINA_DEPLOY = "Dwtp.deploy";
	//catalina
	private static String DCATALINA = "Catalina";
	//work
	private static String WORK = "work";

	private String dcatalinaBase;
	private String dcatalinaHome;
	private String dcatalinaDeploy;
	/***
	 * 获得服务器目录
	 * ge:${plugins}\org.eclipse.wst.server.core\tmp0
	 * @return
	 */
	public String getDcatalinaBase() {
		return this.dcatalinaBase;
	}
	/***
	 * 获得服务器安装目录
	 * eg:${program}\apache-tomcat-7.0.11
	 * @return
	 */
	public String getDcatalinaHome() {
		return this.dcatalinaHome;
	}
	/**
	 * 构造函数
	 * @param server
	 */
	public static IServer getTomcatServer() {
		IServer[] servers = ServerCore.getServers();
		for (IServer server : servers) {
			IRuntimeType type = server.getServerType().getRuntimeType();
			if (type.getName().startsWith(Constants.ApacheTomcat)) {
				return server;
			}
		}
		return null;
	}
	/**
	 * 构造函数
	 * @param server
	 */
	public TomcatServerOsgiModel(IServer server) {
		super(server);
	}
	/***
	 * 解析JVM参数
	 */
	public void parser(String argus) {
		int index1 = argus.indexOf(DCATALINA_BASE);
		if (index1 != -1) {
			this.dcatalinaBase = internalParser(argus, index1);
		}

		index1 = argus.indexOf(DCATALINA_HOME);
		if (index1 != -1) {
			this.dcatalinaHome = internalParser(argus, index1);
		}

		index1 = argus.indexOf(DCATALINA_DEPLOY);
		if (index1 != -1)
			this.dcatalinaDeploy = internalParser(argus, index1);
	}
	private String internalParser(String argus, int index) {
		int index1 = argus.indexOf("\"", index);
		int index2 = argus.indexOf("\"", index1 + 1);
		if ((index1 != -1) && (index2 != -1)) {
			return argus.substring(index1 + 1, index2);
		}
		return "";
	}

	/***
	 * 获得部容器跟目录
	 * eg：org.eclipse.wst.server.core\tmp0\wtpwebapps
	 */
	public String getDeployPath() {
		return this.dcatalinaDeploy;
	}
	/***
	 * 获得部容器Web跟目录
	 * @param module
	 * @return
	 */
	public String getContextPath(){
		return this.dcatalinaDeploy+ File.separator
				+ getModuleContextPath(getHostModule()) + File.separator  ;
	}
	/***
	 * 获得默认work  Catalina Path
	 * @param module
	 * @return
	 */
	public String getCatalinaPath() {
		//String syncPath=  AssignmentManager.getSyncPath().replace("/","\\");
		return this.dcatalinaBase + File.separator + WORK + File.separator
				+ DCATALINA + File.separator + getHost() + File.separator
				+ getModuleContextPath(getHostModule())+File.separator;
	}
	/***
	 * main function
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "-Dcatalina.base=\"I:\\company\\开发环境\\eclipse-ganymede-SR2\\runtime-New_configuration\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\" -Dcatalina.home=\"I:\\company\\tools\\SoTowerStudio3.0.2\\tomcat6\" -Dwtp.deploy=\"I:\\company\\开发环境\\eclipse-ganymede-SR2\\runtime-New_configuration\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\" -Djava.endorsed.dirs=\"I:\\company\\tools\\SoTowerStudio3.0.2\\tomcat6\\endorsed\"";
		s = s.toLowerCase();
		TomcatServerOsgiModel tm = new TomcatServerOsgiModel(null);
		System.out.println(tm.internalParser(s, 0));
	}
}