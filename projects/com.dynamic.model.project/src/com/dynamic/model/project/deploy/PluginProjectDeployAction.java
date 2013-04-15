package com.dynamic.model.project.deploy;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.dynamic.model.project.util.ProjectUtil;

public class PluginProjectDeployAction implements IObjectActionDelegate {
	/**
	 * 选择对象
	 */
	private StructuredSelection selection = null;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
	/***
	 * 执行
	 */
	public void run(IAction action) {
		if (this.selection == null) {
			return;
		}
		IProject project = null;
		for (Iterator it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
			Object element = it.next();
			if (element instanceof IProject) {
				project = (IProject) element;
			} else if (element instanceof IAdaptable) {
				project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
			}
		}
		if(ProjectUtil.isPluginProject(project)){
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell();
			new PluginProjectDeployDialog(shell,this.selection).open();
		}else{
			Shell shell=
			         PlatformUI.getWorkbench().getDisplay().getActiveShell();
			MessageDialog.openInformation (shell, 
			              "提示信息", 
			              "当前操作只应用于模块项目！");
		}
	}
	/***
	 * 选择资源改变
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection){
			this.selection = ((StructuredSelection) selection);
		}
	}
}
