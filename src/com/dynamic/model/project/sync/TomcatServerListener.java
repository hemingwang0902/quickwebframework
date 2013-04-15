package com.dynamic.model.project.sync;

import java.io.File;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import com.dynamic.model.project.Constants;
import com.dynamic.model.project.model.TomcatServerOsgiModel;
import com.dynamic.model.project.parser.BuiderParameter;
import com.dynamic.model.project.parser.BuilderProperty;
import com.dynamic.model.project.util.Console;
import com.dynamic.model.project.util.ProjectUtil;

public class TomcatServerListener extends PluginDeployBase implements IServerListener{
	/***
	 * ����״̬
	 */
	private static boolean RELEASE_STATUS=true;
	/***
	 * ���������ļ�
	 */
	public void serverChanged(ServerEvent event) {
		//event.getState()==ServerEvent.PUBLISH_STATE_CHANGE
		if(event.getState()==ServerEvent.PUBLISH_STATE_CHANGE){
			RELEASE_STATUS=true;
		}else if(RELEASE_STATUS && event.getState()==ServerEvent.STATE_CHANGE){
			RELEASE_STATUS=false;
			TomcatServerOsgiModel serverModel = new TomcatServerOsgiModel(
					event.getServer());
			//��ʼ����Ŀ����
			BuiderParameter buiderParameter=new BuiderParameter();
			buiderParameter.initialize(serverModel.getHostModule().getProject());
			//����������
			Set<String> dependSet = BuiderParameter.getDependModel();
			if(dependSet==null){
				return;
			}else{
				cleanHistory(event.getServer(),dependSet);
				String launchDeploy=BuilderProperty.getLaunchPath();
				IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
			    long begin=	System.currentTimeMillis();
			    Console.println("System Begin Builder Sub-Bundel.");
			    PluginDeployEngine deployEngine=new PluginDeployEngine();
			    deployEngine.setServerModel(serverModel);
			    //buider dependent
			    for(String dependent : dependSet){
			    	IProject project= workspace.getProject(dependent);
			    	if(project.exists()){
			    		Console.println("Begin Builder Dependent Model "+project.getName());
						//�Ƿ�Ϊģ����Ŀ
						if(ProjectUtil.isPluginProject(project)){
							if(launchDeploy.endsWith(Constants.JAR_FILE_EXTENSION_NAME)){
								deployEngine.deployBundelJar(project);
							}else{
								deployEngine.deployBundelFile(project);
							}
						}else{
							Console.println("Dependent Model "+project.getName()+" Is Not PluginProject.");
						}
			    	}
			    }
				long end=System.currentTimeMillis();
				Console.println("Published total consuming "+(end-begin)+"ms"); 
			}
		}
	}
	/***
	 * �����ʷ���
	 * @param deployModel
	 */
	private void cleanHistory(IServer server,Set<String> dependSet){
		if(server==null||dependSet.size()==0){
			return;
		}
		TomcatServerOsgiModel tomcatServerModel = new TomcatServerOsgiModel(server);
		String launchPath=  BuilderProperty.getLaunchPath();
		if(launchPath.endsWith(Constants.JAR_FILE_EXTENSION_NAME)){
			return;
		}else{
			launchPath=launchPath.substring(0, launchPath.lastIndexOf("/"));
		}
		File bundelPath=new File(tomcatServerModel.getContextPath(),launchPath);
		IWorkspaceRoot spaceRoot=ResourcesPlugin.getWorkspace().getRoot();
		for(String modul : dependSet){
			IProject project=spaceRoot.getProject(modul);
			if(project==null){
				continue;
			}
			String bundelName= genareteBundelName(getManifest(project));
			//bundel
			File bundelFolder=new File(bundelPath,bundelName);
			if(bundelFolder.isDirectory()){
				deleteFile(bundelFolder);
				Console.println("Clean History:"+bundelFolder.getPath()); 
			}
		}
	}
	/**      
	 * @param dir    
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
		return file.delete();
	}
}
