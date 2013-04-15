package com.dynamic.model.project;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ProjectActivator extends AbstractUIPlugin {

	public static final String GENERATOR_AUTHOR = "generator.author";
	// The plug-in ID
	public static final String PLUGIN_ID = "com.dynamic.model.project"; //$NON-NLS-1$

	// The shared instance
	private static ProjectActivator plugin;

	/**
	 * The constructor
	 */
	public ProjectActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public String getStudioUser() {
		return getPreferenceStore().getString("generator.author");
	}

	public static IProgressMonitor getDefaultProgressMonitor() {
		return new NullProgressMonitor();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ProjectActivator getDefault() {
		return plugin;
	}

}
