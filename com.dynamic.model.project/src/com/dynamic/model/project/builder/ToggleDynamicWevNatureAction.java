package com.dynamic.model.project.builder;


import java.util.Iterator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;


public class ToggleDynamicWevNatureAction implements IObjectActionDelegate {

	private ISelection selection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject) {
					project = (IProject) element;
				} else if (element instanceof IAdaptable) {
					project = (IProject) ((IAdaptable) element)
							.getAdapter(IProject.class);
				}
				if (project != null) {
					toggleNature(project);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * Toggles sample nature on a project
	 * 
	 * @param project
	 *            to have sample nature added or removed
	 */
	private void toggleNature(IProject project) {
		//PluginNature
		String PluginNature="org.eclipse.wst.common.project.facet.core.nature";
		AssignmentManager assignment=new AssignmentManager();
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			Shell shell= PlatformUI.getWorkbench().getDisplay().getActiveShell();
			if(!project.hasNature(PluginNature)){
				MessageDialog.openInformation (shell, 
				              "提示信息", 
				              "当前操作只应用于Dynamic Web项目！");
				return;
			}
			for (int i = 0; i < natures.length; ++i) {
				if (DynamicWebNature.NATURE_ID.equals(natures[i])) {
					boolean result = MessageDialog.openConfirm(shell,"确认项目操作","该操作将删除项目"
							+ project.getName()
							+ "动态模块特性，是否继续？");
					if(result){
						// Remove the nature
						String[] newNatures = new String[natures.length - 1];
						System.arraycopy(natures, 0, newNatures, 0, i);
						System.arraycopy(natures, i + 1, newNatures, i,
								natures.length - i - 1);
						description.setNatureIds(newNatures);
						project.setDescription(description, null);
						assignment.unPlugin(project,DynamicWebNature.NATURE_ID);
						return;
					}
					return;
				}
			}

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = DynamicWebNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
			assignment.taskPlugin(project,DynamicWebNature.NATURE_ID);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
