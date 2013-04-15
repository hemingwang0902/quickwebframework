package com.dynamic.model.project.actions;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import com.dynamic.model.project.model.TomcatServerOsgiModel;
import com.dynamic.model.project.parser.BuiderParameter;
import com.dynamic.model.project.parser.BuilderProperty;
import com.dynamic.model.project.sync.PluginDeployBase;
import com.dynamic.model.project.util.Console;

public class ModelCleanAction extends PluginDeployBase implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction arg0) {
		// TODO Auto-generated method stub
		IServer tamcatServer=TomcatServerOsgiModel.getTomcatServer();
		TomcatServerOsgiModel serverModel = new TomcatServerOsgiModel(tamcatServer);
		if(tamcatServer.getLaunch()==null){
			Console.println("Begin Eliminate Work Directory:"+serverModel.getCatalinaPath());
			File bundelFile=new File(serverModel.getCatalinaPath());
			deleteFile(bundelFile);
			//bundel clean
			IProject hostWeb = serverModel.getHostModule().getProject();
			cleanBundel(serverModel,hostWeb);
		}else{
			Shell shell= PlatformUI.getWorkbench().getDisplay().getActiveShell();
			MessageDialog.openInformation (shell, 
			              "提示信息", 
			              "系统检测到Web服务已启动，请先关闭服务！");
		}
	}
	/***
	 * 清除bundel
	 * @param hostWeb
	 */
	private void cleanBundel(TomcatServerOsgiModel serverModel,IProject hostWeb){
		if(serverModel==null||hostWeb==null){
			return;
		}
		String launchPath=BuilderProperty.getLaunchPath();
		if(launchPath==null){
			return;
		}
		launchPath=launchPath.substring(0,launchPath.lastIndexOf("/"));
		String webRoot=new BuiderParameter().getWebContent(hostWeb);
		//bundel set
		File bundelPath=new File(hostWeb.getLocation().toFile(),webRoot+launchPath);
		Set<String> hostSet=new HashSet<String>();
		if(bundelPath.isDirectory()){
			for (File bundel : bundelPath.listFiles()) {
				hostSet.add(bundel.getName());
			}
		}
		File launchFile=new File(serverModel.getContextPath(),launchPath);
		if(launchFile.exists()){
			for (File bundel : launchFile.listFiles()) {
				if(hostSet.contains(bundel.getName())){
					continue;
				}else{
					deleteFile(bundel);
				}
			}
		}
	}
	/**     
	 * 递归删除目录下的所有文件及子目录下所有文件     
	 * @param dir 将要删除的文件目录     
	 * @return boolean Returns "true" if all deletions were successful.     
	 *  If a deletion fails, the method stops attempting to     
	 *  delete and returns "false".     
	 */    
	private static boolean deleteFile(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteFile(new File(file,children[i]));                
				if (!success) {                    
					return false;                
				}
			}
		}
		Console.println("Delete File : "+file.getPath());
		return file.delete();
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
