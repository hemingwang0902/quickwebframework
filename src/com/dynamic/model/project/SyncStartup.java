package com.dynamic.model.project;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IStartup;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerCore;

import com.dynamic.model.project.model.TomcatServerOsgiModel;
import com.dynamic.model.project.sync.ResourceDeltaVisitor;
import com.dynamic.model.project.sync.TomcatServerListener;
import com.dynamic.model.project.util.Console;

public class SyncStartup implements IStartup{

	
	@Override
	public void earlyStartup() {
		launchSync();
		resourceSync();
	}
	/***
	 * 资源同步
	 */
	private void resourceSync(){
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener(){
			@Override
			public void resourceChanged(
					IResourceChangeEvent resourcechangeevent) {
				try {
					if ((resourcechangeevent != null) && (resourcechangeevent.getDelta() != null))
						resourcechangeevent.getDelta().accept(new ResourceDeltaVisitor());
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		});
		Console.println("Workspace Add Resource Change Listener "+IResourceChangeListener.class.getName());
	}
	/***
	 * 启动同步
	 */
	private void launchSync(){
		IServer tamcatServer = TomcatServerOsgiModel.getTomcatServer();
		ServerCore.addServerLifecycleListener(new IServerLifecycleListener() {
			@Override
			public void serverAdded(IServer server) {
				IRuntimeType type = server.getServerType().getRuntimeType();
				if (type.getName().startsWith(Constants.ApacheTomcat)) {
					server.addServerListener(new TomcatServerListener());
					Console.println(type.getName() + " Add Server Listener "
							+ IServerListener.class.getName());
				}
			}
			@Override
			public void serverChanged(IServer server) {
				// TODO Auto-generated method stub
			}
			@Override
			public void serverRemoved(IServer server) {
				IRuntimeType type = server.getServerType().getRuntimeType();
				if (type.getName().startsWith(Constants.ApacheTomcat)) {
					server.removeServerListener(new TomcatServerListener());
					Console.println(type.getName() + " Remove Server Listener "
							+ IServerListener.class.getName());
				}
			}
		});
		if (tamcatServer == null) {
			return;
		} else {
			tamcatServer.addServerListener(new TomcatServerListener());
			Console.println(tamcatServer.getName() + " Add Server Listener "
					+ IServerListener.class.getName());
		}
	}
}
