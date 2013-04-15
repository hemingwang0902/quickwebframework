package com.dynamic.model.project.builder;

import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;

public class DynamicWebBuilder extends IncrementalProjectBuilder {


	public static final String BUILDER_ID = "com.dynamic.model.project.dynamicWebBuilder";

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		// TODO Auto-generated method stub
		super.setInitializationData(config, propertyName, data);
	}

	/*
	 * 资源发生变化时调用
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			getProject().accept(new DynamicVisitor(monitor));
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				getProject().accept(new DynamicVisitor(monitor));
			} else {
				delta.accept(new DynamicDeltaVisitor(monitor));
			}
		}
		return null;
	}
	/***
	 * 打包
	 * @param resource
	 * @param monitor
	 */
	private void build(IResource resource, IProgressMonitor monitor) {
		//PluginExportOperation
		
	}

	private class DynamicDeltaVisitor implements IResourceDeltaVisitor {
		private IProgressMonitor monitor;

		public DynamicDeltaVisitor(IProgressMonitor paramIProgressMonitor) {
			this.monitor = paramIProgressMonitor;
		}

		public boolean visit(IResourceDelta delta) {
			DynamicWebBuilder.this.build(delta.getResource(), this.monitor);
			return false;
		}
	}

	private class DynamicVisitor implements IResourceVisitor {
		private IProgressMonitor monitor;

		public DynamicVisitor(IProgressMonitor paramIProgressMonitor) {
			this.monitor = paramIProgressMonitor;
		}

		public boolean visit(IResource resource) {
			DynamicWebBuilder.this.build(resource, this.monitor);
			return false;
		}
	}
}
