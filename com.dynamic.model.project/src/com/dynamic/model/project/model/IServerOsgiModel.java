package com.dynamic.model.project.model;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

public abstract interface IServerOsgiModel {
	/***
	 * ��ò���·��
	 * 
	 * @return
	 */
	public abstract String getDeployPath();

	/***
	 * �ж�����״̬
	 * @return
	 */
	public abstract boolean isRunning();

	/***
	 * ��ò���ģ��
	 * @return
	 */
	public abstract IModule[] getModules();

	/***
	 * ��ķ�����
	 * @return
	 */
	public abstract IServer getServer();

	/***
	 * �������
	 * @return
	 */
	public abstract String getHost();
}