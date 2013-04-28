package com.dynamic.model.project.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract interface  IProjectBuilder {
	public abstract void build(IResource paramIResource, IProgressMonitor paramIProgressMonitor);
}
