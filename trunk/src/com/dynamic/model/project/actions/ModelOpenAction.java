package com.dynamic.model.project.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.wst.server.core.IServer;

import com.dynamic.model.project.model.TomcatServerOsgiModel;
import com.dynamic.model.project.sync.PluginDeployBase;

public class ModelOpenAction extends PluginDeployBase implements IWorkbenchWindowActionDelegate{
	@Override
	public void run(IAction arg0) {
		// TODO Auto-generated method stub
		IServer tamcatServer=TomcatServerOsgiModel.getTomcatServer();
		TomcatServerOsgiModel serverModel = new TomcatServerOsgiModel(tamcatServer);
		String directory= serverModel.getDeployPath();
		if (directory == null) {
			return;
		}
		try {
			Runtime.getRuntime()
					.exec("explorer.exe /n," + directory.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow arg0) {
		// TODO Auto-generated method stub
		
	}
}
