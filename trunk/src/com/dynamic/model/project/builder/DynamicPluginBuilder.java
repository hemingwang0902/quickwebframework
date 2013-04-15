package com.dynamic.model.project.builder;



import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.core.IServer;

import com.dynamic.model.project.Constants;
import com.dynamic.model.project.model.TomcatServerOsgiModel;
import com.dynamic.model.project.parser.BuiderParameter;
import com.dynamic.model.project.parser.BuilderProperty;
import com.dynamic.model.project.sync.PluginDeployEngine;
import com.dynamic.model.project.util.Console;


public class DynamicPluginBuilder extends IncrementalProjectBuilder {
	
	public static final String BUILDER_ID = "com.dynamic.model.project.dynamicPluginBuilder";
	
	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		// TODO Auto-generated method stub
		super.setInitializationData(config, propertyName, data);
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(BUILDER_ID);
		//IExtension[] dmPluginExtensions = point.getExtensions();
	}
	/*
	 * 资源发生变化时调用
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		//getProject().accept(new DynamicVisitor(monitor));
		if(kind==AUTO_BUILD){
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				getProject().accept(new DynamicVisitor(monitor));
			} else {
				delta.accept(new DynamicDeltaVisitor(monitor));
			}
		}
		return null;
	}
	/***
	 * 打包
	 * @param resource
	 * @param monitor
	 */
	private void build(IResource resource, IProgressMonitor monitor) {
		if(BuiderParameter.getProjectAutoDeploy(resource.getProject())){
			String syncType=BuilderProperty.getSyncPath();
			if(syncType.endsWith(Constants.JAR_FILE_EXTENSION_NAME)){
				Console.println(DynamicPluginBuilder.class.getName()+" Begin Sync File "+resource.getName());
				//parse match Server
				IServer tamcatServer=TomcatServerOsgiModel.getTomcatServer();
				//buidler processing
				if(tamcatServer==null||tamcatServer.getLaunch()==null){
					return;
				}else if(BuiderParameter.contains(resource.getName())){
					TomcatServerOsgiModel serverModel = new TomcatServerOsgiModel(tamcatServer);
					PluginDeployEngine deployEngine=new PluginDeployEngine();
					deployEngine.setServerModel(serverModel);
					deployEngine.syncBundelJar(resource.getProject());
				}
			}
		}
	}
	
	private class DynamicDeltaVisitor implements IResourceDeltaVisitor {
		private IProgressMonitor monitor;

		public DynamicDeltaVisitor(IProgressMonitor paramIProgressMonitor) {
			this.monitor = paramIProgressMonitor;
		}

		public boolean visit(IResourceDelta delta) {
			DynamicPluginBuilder.this.build(delta.getResource(), this.monitor);
			return false;
		}
	}

	private class DynamicVisitor implements IResourceVisitor {
		private IProgressMonitor monitor;

		public DynamicVisitor(IProgressMonitor paramIProgressMonitor) {
			this.monitor = paramIProgressMonitor;
		}

		public boolean visit(IResource resource) {
			DynamicPluginBuilder.this.build(resource, this.monitor);
			return false;
		}
	}
}
