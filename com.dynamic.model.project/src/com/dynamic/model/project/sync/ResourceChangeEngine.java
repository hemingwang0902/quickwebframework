package com.dynamic.model.project.sync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.dynamic.model.project.Constants;
import com.dynamic.model.project.model.TomcatServerOsgiModel;
import com.dynamic.model.project.parser.BuiderParameter;
import com.dynamic.model.project.parser.BuilderProperty;
import com.dynamic.model.project.util.Console;

public class ResourceChangeEngine extends PluginDeployBase{
	
	private int changeMode = 4;
	private IResource sourceResource;
	
	public ResourceChangeEngine(IResource sourceResource,int changeMode){
		this.changeMode=changeMode;
		this.sourceResource=sourceResource;
	}
	/***
	 * 
	 * @param resource
	 * @param changeMode
	 * @param serverOsgiModel
	 */
	public void resourceChangedJob(TomcatServerOsgiModel tomcatServerOsgiModel){
		String bundelName = genareteBundelName(getManifest(sourceResource.getProject()));
		//sync folder
		String launchPath=BuilderProperty.getLaunchPath();
		File bundelPath=new File(tomcatServerOsgiModel.getContextPath(),launchPath.substring(0,launchPath.lastIndexOf("/")));
		String syncPath=BuilderProperty.getSyncPath();
		File workPath=new File(tomcatServerOsgiModel.getCatalinaPath(),syncPath.substring(0,syncPath.lastIndexOf("/")));
		//realfile
		String relative=sourceResource.getProjectRelativePath().toString();
		if(relative.endsWith(Constants.JAVA_FILE_EXTENSION_NAME)){
			String srcBolder=BuiderParameter.getProjectSrc(sourceResource.getProject());
			relative=relative.substring(srcBolder.length());
		}else if(relative.endsWith(Constants.CLASS_FILE_EXTENSION_NAME)){
			String output=BuiderParameter.getProjectOutput(sourceResource.getProject());
			relative=relative.substring(output.length());
		}
		File workFolder=new File(workPath,bundelName);
		if(workFolder.isDirectory()){
			workFolder=new File(workFolder,relative);
			Console.println("Synchronize File:"+workFolder.getPath());
			resourceChanged(workFolder);
		}
		File bundleFolder=new File(bundelPath,bundelName);
		if(bundleFolder.isDirectory()){
			bundleFolder=new File(bundleFolder,relative);
			Console.println("Synchronize File:"+bundleFolder.getPath());
			resourceChanged(bundleFolder);
		}
	}
	/***
	 * 资源改变处理
	 * 
	 * @param sourceResource2
	 * @param realFile
	 */
	private void resourceChanged(File realFile) {
		IFile sourceFile;
		switch (this.changeMode) {
		case 1:
			if (sourceResource instanceof IFile) {
				sourceFile = (IFile) sourceResource;
				fileCopy(sourceFile, realFile);
				return;
			}
			if (!(sourceResource instanceof IFolder)) {
				return;
			}
			realFile.mkdirs();
			break;
		case 2:
			fileDelete(realFile);
			break;
		case 3:
		case 4:
			if (!(sourceResource instanceof IFile)) {
				return;
			}
			sourceFile = (IFile) sourceResource;
			fileCopy(sourceFile, realFile);
		}
	}

	
	/***
	 * 删除文件
	 * 
	 * @param realFile
	 */
	private void fileDelete(File realFile) {
		if (realFile.isFile()) {
			realFile.delete();
		} else if (realFile.isDirectory()) {
			File[] files = realFile.listFiles();

			if (files != null) {
				for (File file : files) {
					fileDelete(file);
				}
			}
			realFile.delete();
		}
	}
	/***
	 * 拷贝文件
	 * 
	 * @param sourceFile2
	 * @param realFile
	 */
	private void fileCopy(IFile sourceFile, File realFile) {
		try {
			if ((!(realFile.exists())) && (!(realFile.createNewFile()))) {
				return;
			}
			InputStream sourceIn = sourceFile.getContents();
			byte[] b = new byte[sourceIn.available()];
			sourceIn.read(b);
			FileOutputStream out = new FileOutputStream(realFile);
			out.write(b);
			sourceIn.close();
			out.close();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
