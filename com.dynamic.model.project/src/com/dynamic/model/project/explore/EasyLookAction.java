package com.dynamic.model.project.explore;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class EasyLookAction
  implements IObjectActionDelegate
{
  private Object selected = null;
  private Class selectedClass = null;

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }

  public void run(IAction action)
  {
    if (this.selected == null) {
      return;
    }
    File directory = null;
    if (this.selected instanceof IResource) {
      directory = new File(
        ((IResource)this.selected).getLocation().toOSString());
    }
    if (this.selected instanceof IFile) {
      directory = directory.getParentFile();
    }
    if (directory == null)
      return;
    try
    {
      Runtime.getRuntime().exec(
        "explorer.exe /n," + directory.toString());
    }
    catch (Exception localException)
    {
    }
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    try {
      IAdaptable adaptable = null;
      this.selected = "unknown";
      if (selection instanceof IStructuredSelection) {
        adaptable = (IAdaptable)((IStructuredSelection)selection)
          .getFirstElement();
        this.selectedClass = adaptable.getClass();
        if (adaptable instanceof IResource)
          this.selected = ((IResource)adaptable);
        else if ((adaptable instanceof PackageFragment) && 
          (((PackageFragment)adaptable)
          .getPackageFragmentRoot() instanceof JarPackageFragmentRoot))
          this.selected = 
            getJarFile(((PackageFragment)adaptable).getPackageFragmentRoot());
        else if (adaptable instanceof JarPackageFragmentRoot)
          this.selected = getJarFile(adaptable);
        else
          this.selected = ((IResource)
            adaptable.getAdapter(IResource.class));
      }
    }
    catch (Throwable localThrowable) {
    }
  }

  protected File getJarFile(IAdaptable adaptable) {
    JarPackageFragmentRoot jpfr = (JarPackageFragmentRoot)adaptable;
    File selected = jpfr.getPath().makeAbsolute().toFile();
    if (!(selected.exists())) {
      File projectFile = new File(
        jpfr.getJavaProject().getProject().getLocation().toOSString());
      selected = new File(projectFile.getParent() + selected.toString());
    }
    return selected;
  }
}