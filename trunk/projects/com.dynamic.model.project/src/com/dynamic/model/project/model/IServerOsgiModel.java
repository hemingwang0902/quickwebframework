package com.dynamic.model.project.model;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

public abstract interface IServerOsgiModel {
	/***
	 * 获得部署路径
	 * 
	 * @return
	 */
	public abstract String getDeployPath();

	/***
	 * 判断运行状态
	 * @return
	 */
	public abstract boolean isRunning();

	/***
	 * 获得部署模块
	 * @return
	 */
	public abstract IModule[] getModules();

	/***
	 * 获的服务器
	 * @return
	 */
	public abstract IServer getServer();

	/***
	 * 获得主机
	 * @return
	 */
	public abstract String getHost();
}