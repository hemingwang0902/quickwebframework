package com.dynamic.model.project.builder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.dynamic.model.project.ProjectActivator;
import com.dynamic.model.project.parser.BuilderProperty;
import com.dynamic.model.project.parser.Task;


public class AssignmentManager {
	/**
	 * 执行PluginNature任务
	 * @param project
	 */
	public void taskPlugin(IProject project, String natureId) {
		//get config task map
		Map<String, List<Task>> taskMap = BuilderProperty.getTaskMap();
		List<Task> taskList = taskMap.get(natureId);
		URL pluginRoot=ProjectActivator.class.getClassLoader().getResource("/resource"); 
		try {
			String baseRoot = FileLocator.toFileURL(pluginRoot).getFile();
			for (Task task : taskList) {
				if (task.getResource().contains("@")) {
					String resource = baseRoot+ task.getResource();
					String destination = project.getLocationURI().getPath()
							+ task.getDestination();
					copyDirectory(resource, destination);
				} else {
					String resource = baseRoot
							+ task.getResource();
					String destination = project.getLocationURI().getPath()
							+ task.getDestination();
					fileCopy(resource, destination);
					project.refreshLocal(IResource.DEPTH_INFINITE,
							new NullProgressMonitor());
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行PluginNature任务
	 * @param project
	 */
	public void unPlugin(IProject project, String natureId) {
		Map<String, List<Task>> taskMap =BuilderProperty.getTaskMap();
		List<Task> taskList = taskMap.get(natureId);
		for (Task task : taskList) {
			String destination = project.getLocationURI().getPath()
					+ task.getDestination();
			fileDelete(destination);
		}
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE,
					new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 拷贝文件
	 * @param sourceFile2
	 * @param realFile
	 */
	private void fileCopy(String resource, String destination) {
		Project prj = new Project();
		Copy copy = new Copy();
		copy.setProject(prj);
		copy.setFile(new File(resource));
		File destDir = new File(destination).getParentFile();
		copy.setTodir(destDir);
		copy.execute();
	}

	/***
	 * 目录复制
	 * @param resource
	 * @param destination
	 */
	private void copyDirectory(String resource, String destination) {
		Project prj = new Project();
		Copy copy = new Copy();
		copy.setProject(prj);
		String[] source = resource.split("@");
		copy.setOverwrite(true);
		copy.setTodir(new File(destination));
		// FileSet
		if (source.length > 1) {
			FileSet fileSet = new FileSet();
			fileSet.setDir(new File(source[0]));
			fileSet.setIncludes(source[1]);
			copy.addFileset(fileSet);
		}
		copy.execute();
	}

	/***
	 * 删除文件
	 * 
	 * @param realFile
	 */
	private void fileDelete(String destination) {
		if (destination == null || destination.equals("")) {
			return;
		}
		File destFile = new File(destination);
		if (destFile.exists()) {
			destFile.delete();
		}
	}
}
