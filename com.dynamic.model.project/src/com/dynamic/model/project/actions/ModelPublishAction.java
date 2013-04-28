package com.dynamic.model.project.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection; 
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.wst.server.core.IServer;

import com.dynamic.model.project.model.TomcatServerOsgiModel;
import com.dynamic.model.project.parser.BuiderParameter;
import com.dynamic.model.project.sync.PluginDeployEngine;
import com.dynamic.model.project.util.ProjectUtil;

public class ModelPublishAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window = null;
	@Override
	public void run(IAction arg0) {
		// TODO Auto-generated method stub
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		IProject selectProject = getCurrentProject();
		if (selectProject == null) {
			MessageDialog.openInformation(shell, "��ʾ��Ϣ", "��ѡ��Ҫ������ģ����Ŀ��");
		} else if (ProjectUtil.isPluginProject(selectProject)) {
			// parse match Server
			IServer tamcatServer = TomcatServerOsgiModel.getTomcatServer();
			// buidler processing
			if (tamcatServer == null || tamcatServer.getLaunch() == null) {
				MessageDialog.openInformation(shell, "��ʾ��Ϣ", "ϵͳ���ķ�����δ������");
			} else if (BuiderParameter.contains(selectProject.getName())) {
				TomcatServerOsgiModel serverModel = new TomcatServerOsgiModel(
						tamcatServer);
				PluginDeployEngine deployEngine = new PluginDeployEngine();
				deployEngine.setServerModel(serverModel);
				deployEngine.syncBundelJar(selectProject.getProject());
				MessageDialog.openInformation(shell, "��ʾ��Ϣ", "ģ����Ŀ["
						+ selectProject.getName() + "]�����ɹ���");
			}
		} else {
			MessageDialog.openInformation(shell, "��ʾ��Ϣ", "ѡ����Ŀ��ģ����Ŀ��");
		}
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
	}
	/***
	 * ���ѡ����Ŀ
	 * @return
	 */
	public static IProject getCurrentProject(){  
        ISelectionService selectionService =   
            Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();  
        ISelection selection = selectionService.getSelection();  
        IProject project = null;  
        if(selection instanceof IStructuredSelection) {  
            Object element = ((IStructuredSelection)selection).getFirstElement();  
            if (element instanceof IResource) {  
                project= ((IResource)element).getProject();  
            } else if (element instanceof PackageFragmentRootContainer) {  
                IJavaProject jProject =   
                    ((PackageFragmentRootContainer)element).getJavaProject();  
                project = jProject.getProject();  
            } else if (element instanceof IJavaElement) {  
                IJavaProject jProject= ((IJavaElement)element).getJavaProject();  
                project = jProject.getProject();  
            }  
        }else{
        	IEditorPart part =Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        	if(part != null){  
                Object object = part.getEditorInput().getAdapter(IFile.class);  
                if(object != null){  
                    project = ((IFile)object).getProject();  
                }  
            }  
        }
        return project;  
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
