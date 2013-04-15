package com.dynamic.model.project.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.dynamic.model.project.builder.DynamicPluginNature;
import com.dynamic.model.project.builder.DynamicWebNature;

public class ProjectUtil {
	/***
	 * ����Nature
	 * @param project
	 * @param natureID
	 * @param monitor
	 */
	public static void addNature(IProject project, String natureID,
			IProgressMonitor monitor) {
		if ((project == null) || (!(project.exists()))
				|| (!(project.isAccessible())))
			return;
		if (isProjectHasNature(project, natureID)) {
			return;
		}
		if (monitor == null)
			monitor = new NullProgressMonitor();
		try {
			if (project.isSynchronized(2)) {
				project.refreshLocal(2, monitor);
			}
			IProjectDescription desc = project.getDescription();

			String[] oldNatures = desc.getNatureIds();
			String[] newNatures = new String[oldNatures.length + 1];
			newNatures[0] = natureID;

			if (oldNatures.length > 0) {
				System.arraycopy(oldNatures, 0, newNatures, 1,
						oldNatures.length);
			}
			desc.setNatureIds(newNatures);
			project.setDescription(desc, monitor);
		} catch (CoreException e) {

		}
	}
	/***
	 * �ж��Ƿ�ΪJavaProject
	 * @param resource
	 * @return
	 */
	public static boolean isJavaProject(IResource resource) {
		return isProjectHasNature(resource, "org.eclipse.jdt.core.javanature");
	}
	/***
	 * �ж��Ƿ�ΪDynamic Web Model
	 * @param resource
	 * @return
	 */
	public static boolean isClientProject(IResource resource) {
		//com.dynamic.model.project.dynamicWebNature
		return isProjectHasNature(resource,DynamicWebNature.NATURE_ID);
	}
	
	/***
	 * �ж��Ƿ�ΪPluginProject
	 * @param resource
	 * @return
	 */
	public static boolean isPluginProject(IResource resource) {
		//com.dynamic.model.project.dynamicPluginNature
		return isProjectHasNature(resource,DynamicPluginNature.NATURE_ID);
	}
	/***
	 * �ж��Ƿ�ΪFragmentProject
	 * @param resource
	 * @return
	 */
	public static boolean isFragmentProject(IResource resource) {
		return isProjectHasNature(resource,
				"org.sotower.dm.project.fragmentnature");
	}
	/***
	 * �ж��Ƿ����Nature
	 * @param resource
	 * @param natureId
	 * @return
	 */
	private static boolean isProjectHasNature(IResource resource,
			String natureId) {
		if (resource == null){
			return false;
		}
		IProject project = null;
		if (resource.getType() == 4){
			project = (IProject) resource;
		}else{
			project = resource.getProject();
		}
		if ((project == null) || (!(project.isAccessible()))){
			return false;
		}
		try {
			//String[] natures = project.getDescription().getNatureIds();
			return project.hasNature(natureId);
		} catch (CoreException localCoreException) {
		}
		return false;
	}
}
