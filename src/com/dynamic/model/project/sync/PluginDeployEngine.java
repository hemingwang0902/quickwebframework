package com.dynamic.model.project.sync;


import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.ThisExpression;


import com.dynamic.model.project.Constants;
import com.dynamic.model.project.model.TomcatServerOsgiModel;
import com.dynamic.model.project.parser.BuiderParameter;
import com.dynamic.model.project.parser.BuilderProperty;
import com.dynamic.model.project.util.Console;


public class PluginDeployEngine extends PluginDeployBase{
	
	
	TomcatServerOsgiModel serverModel;

	public TomcatServerOsgiModel getServerModel() {
		return serverModel;
	}

	public void setServerModel(TomcatServerOsgiModel serverModel) {
		this.serverModel = serverModel;
	}
	/***
	 * 同步jar资源
	 * @param resource
	 * @return
	 */
	public String syncBundelJar(IProject resource){
		String bundelName=deployBundelJar(resource);
		String catalinaPath=serverModel.getCatalinaPath();
		String syncPath= BuilderProperty.getSyncPath();
		if(syncPath.endsWith(Constants.JAR_FILE_EXTENSION_NAME)){
			File syncFolder=new File(catalinaPath,syncPath.substring(0,syncPath.lastIndexOf("/")));
			Console.println("Bublish Sync Project "+resource.getName()+" To : "+syncFolder); 
			if(syncFolder.exists()){
				Project prj=new Project();
				Copy copy=new Copy();
				copy.setOverwrite(true);
				copy.setProject(prj);
				copy.setTodir(syncFolder);
				copy.setFile(new File(bundelName));
				copy.execute();
			}
		}
		return bundelName;
	}
	/***
	 * 不是project为jar bundel
	 * @param IModule webmodel 
	 * @param IProject plugin
	 * @return
	 */
	public String deployBundelJar(IProject resource){
		// mark class file to bundle.
		Project project = new Project();
		project.setName(resource.getName());
		project.setBasedir(resource.getLocation().toString());
		// creat jar
		Jar jar = new Jar();
		Manifest manifest = getManifest(resource);
		try {
			jar.addConfiguredManifest(manifest);
		} catch (ManifestException e) {
			e.printStackTrace();
			return null;
		}
		jar.setProject(project);
		String outPut=BuiderParameter.getProjectOutput(resource);
		File baseDir=new File(project.getBaseDir(),outPut);
		jar.setBasedir(baseDir);
		// destination .
		String bundleName = genarateFullExtName(manifest);
		Console.println("Bublish Project "+resource.getName()+" As Bundel To : "+bundleName); 
		jar.setDestFile(new File(bundleName));
		// extend set.
		FileSet fileSet=getFileSet(project,resource);
		jar.addFileset(fileSet);
		jar.execute();
		return bundleName;
	}
	/***
	 * 不是project为file bundel
	 * @param module
	 * @param model
	 * @return
	 */
	public String deployBundelFile(IProject resource){
		Project project=new Project();
		project.setName(resource.getName());
		project.setBasedir(resource.getLocation().toString());
		Copy copy=new Copy();
		copy.setOverwrite(true);
		copy.setProject(project);
		Manifest manifest = getManifest(resource);
		String bundelName=genarateFullName(manifest).toString();
		File bundelFile=new File(bundelName);
		if(!bundelFile.exists()){
			bundelFile.mkdirs();
		}
		Console.println("Bublish Project "+resource.getName()+" As Bundel To : "+bundelFile); 
		copy.setTodir(bundelFile);
		//FileSet
		FileSet fileSet=getFileSet(project,resource);
		fileSet.setDir(project.getBaseDir());
		copy.addFileset(fileSet);
		//class
		FileSet classSet=new FileSet();
		String outPut=BuiderParameter.getProjectOutput(resource);
		File baseDir=new File(project.getBaseDir(),outPut);
		//复制的宿主目录
		classSet.setDir(baseDir);
		classSet.setIncludes("**/*.*");
		copy.addFileset(classSet);
		copy.execute(); 
		return bundelName;
	}
	/***
	 * 
	 * @param module
	 * @param manifest
	 * @return
	 */
	private String genarateFullExtName(Manifest manifest){
		return genarateFullName(manifest).append(Constants.JAR_FILE_EXTENSION_NAME).toString();
	}
	/***
	 * 生成bundel目录
	 * @param module
	 * @param manifest
	 * @return
	 */
	public StringBuffer genarateFullName(Manifest manifest){
		String contextPath=serverModel.getContextPath();
		File contextFile=new File(contextPath);
		String relativePath =BuilderProperty.getLaunchPath();
		relativePath=relativePath.substring(0,relativePath.lastIndexOf("/"));
		File bundelPath=new File(contextFile,relativePath);
		StringBuffer bundleName = new StringBuffer(bundelPath.getPath());
		return bundleName.append(File.separator).append(genareteBundelName(manifest)); 
	}
}
