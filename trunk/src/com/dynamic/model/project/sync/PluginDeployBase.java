package com.dynamic.model.project.sync;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.Manifest.Section;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.dynamic.model.project.Constants;

public abstract class PluginDeployBase {

	/***
	 * 读取项目Manifest
	 * @param model
	 * @return
	 */
	protected Manifest getManifest(IProject model){
		try {
			IFile manifestMf=model.getFile(Constants.MANIFEST_MF);
			InputStreamReader reader =
					new InputStreamReader(manifestMf.getContents());
			Manifest manifest = new Manifest(reader);
			reader.close();
			return manifest;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/***
	 * 获得FileSet
	 * @param project
	 * @param model
	 * @return 
	 */
	protected FileSet getFileSet(Project project,IProject model){
		FileSet fileSet=new FileSet();
		fileSet.setProject(project);
		fileSet.setDir(project.getBaseDir());
		Properties bp = new Properties();
		try {
			bp.load(model.getFile(Constants.BUILD_PROPERTIES).getContents());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		fileSet.setIncludes(bp.getProperty("bin.includes"));
		if(bp.getProperty("bin.excludes")==null){
			return fileSet;
		}
		fileSet.setExcludes(bp.getProperty("bin.excludes"));
		return fileSet;
	}
	/***
	 * 
	 * @param manifest
	 * @return
	 */
	protected static String genareteBundelName(Manifest manifest){
		StringBuffer bundleName = new StringBuffer();
		String version = getBundleVersion(manifest);
		String subName = getBundleSymbolicName(manifest);
		bundleName.append(subName.split(";")[0]).append(".").append(version);
		return bundleName.toString(); 
	}
	/***
	 * get Bundle Symbolic Name
	 * @param location jar path
	 * @return
	 */
	public static String getBundleSymbolicName(Manifest manifest) {
		Section section = manifest.getMainSection();
		//Manifest Attributes
		String subName = section.getAttributeValue(Constants.BUNDLE_SYMBOLIC_NAME);
		//Bundle-SymbolicName
		return subName;
	}
	/***
	 * Bundle Version
	 * @param location
	 * @return
	 */
	public static String getBundleVersion(Manifest manifest) {
		Section section = manifest.getMainSection();
		//Manifest Attributes
		String subName = section.getAttributeValue(Constants.BUNDLE_VERSION);
		//Manifest Attributes
		return subName;
	}
}
