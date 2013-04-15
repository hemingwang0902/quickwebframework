package com.dynamic.model.project.sync;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;

import com.dynamic.model.project.model.TomcatServerOsgiModel;
import com.dynamic.model.project.parser.BuiderParameter;
import com.dynamic.model.project.util.ProjectUtil;


public class ResourceDeltaVisitor implements IResourceDeltaVisitor {
	/***
	 * 浏览资源
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		switch (delta.getKind()) {
		case 1:
			resourceChanged(resource, 1);
			break;
		case 2:
			resourceChanged(resource, 2);
			break;
		case 3:
		case 4:
			if (!(resource instanceof IFile)){
				break;
			}
			resourceChanged(resource, 4);
		}
		return true;
	}
	/***
	 * 资源改变监听
	 * @param resource
	 * @param changeMode
	 */
	public void resourceChanged(IResource resource, int changeMode) {
		if(ProjectUtil.isPluginProject(resource)){
			IServer tomcatServer=TomcatServerOsgiModel.getTomcatServer();
			if(tomcatServer==null||tomcatServer.getLaunch()==null){
				return;
			}else if(BuiderParameter.contains(resource.getProject().getName())){
				TomcatServerOsgiModel tomcatServerModel = new TomcatServerOsgiModel(tomcatServer);
				ResourceChangeEngine resourceChangeEngine=new ResourceChangeEngine(resource,changeMode);
				resourceChangeEngine.resourceChangedJob(tomcatServerModel);
			}
		}
	}
}