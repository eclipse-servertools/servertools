/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.ModuleFactory;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;
/**
 * A helper class for defining a module factory that provides modules
 * based on projects.
 * 
 * @since 1.0
 */
public abstract class ProjectModuleFactoryDelegate extends ModuleFactoryDelegate {
	private static List factories = new ArrayList();

	// list of IModules
	private List modules;

	/**
	 * Construct a new ProjectModuleFactoryDelegate.
	 */
	public ProjectModuleFactoryDelegate() {
		super();
		
		factories.add(this);
	}

	/**
	 * Cache any preexisting modules.
	 */
	private final void cacheModules() {
		if (modules != null)
			return;
		
		try {
			clearCache();
			IProject[] projects2 = getWorkspaceRoot().getProjects();
			int size = projects2.length;
			modules = new ArrayList(size);
			for (int i = 0; i < size; i++) {
				//Trace.trace("caching: " + this + " " + projects[i] + " " + isValidModule(projects[i]));
				if (projects2[i].isAccessible()) {
					try {
						IModule[] modules2 = createModules(projects2[i]);
						if (modules2 != null) {
							int size2 = modules2.length;
							for (int j = 0; j < size2; j++)
								if (modules2[j] != null)
									modules.add(modules2[j]);
						}
					} catch (Throwable t) {
						Trace.trace(Trace.SEVERE, "Error creating module", t);
					}
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error caching modules", e);
		}
	}

	/**
	 * Return the workspace root.
	 * 
	 * @return the workspace root
	 */
	private static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Return the modules provided by this factory.
	 * 
	 * @return a possibly-empty array of modules
	 */
	public final IModule[] getModules() {
		cacheModules();
		
		IModule[] modules2 = new IModule[modules.size()];
		modules.toArray(modules2);
		return modules2;
	}

	/*
	 * Temporary method to patch 204165. Do not use, see equivalent API in 2.0.
	 */
	public IModule[] getModules204165(IProject project) {
		return createModules(project);
	}

	/**
	 * Handle changes to a project.
	 * 
	 * @param project a project
	 * @param delta a resource delta
	 */
	public final static void handleGlobalProjectChange(final IProject project, IResourceDelta delta) {
		ModuleFactory[] factories2 = ServerPlugin.getModuleFactories();
		int size = factories2.length;
		for (int i = 0; i < size; i++) {
			if (factories2[i].delegate != null && factories2[i].delegate instanceof ProjectModuleFactoryDelegate) {
				ProjectModuleFactoryDelegate pmfd = (ProjectModuleFactoryDelegate) factories2[i].delegate;
				if (pmfd.deltaAffectsModules(delta)) {
					pmfd.modules = null;
					factories2[i].clearModuleCache();
				}
			}
		}
	}

	/**
	 * Returns <code>true</code> if the delta may have changed modules,
	 * and <code>false</code> otherwise.
	 * 
	 * @param delta a resource delta
	 * @return <code>true</code> if the delta may have changed modules,
	 *    and <code>false</code> otherwise
	 */
	private final boolean deltaAffectsModules(IResourceDelta delta) {
		final boolean[] b = new boolean[1];
		
		final IPath[] listenerPaths = getListenerPaths();
		if (listenerPaths == null || listenerPaths.length == 0)
			return false;
		final int size = listenerPaths.length;
		
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta2) throws CoreException {
					if (b[0])
						return false;
					//Trace.trace(Trace.FINEST, delta2.getResource() + "  " + delta2.getKind() + " " + delta2.getFlags());
					boolean ok = false;
					IPath path = delta2.getProjectRelativePath();
					for (int i = 0; i < size; i++) {
						if (listenerPaths[i].equals(path)) {
							b[0] = true;
							return false;
						} else if (path.isPrefixOf(listenerPaths[i])) {
							ok = true;
						}
					}
					return ok;
				}
			});
		} catch (Exception e) {
			// ignore
		}
		//Trace.trace(Trace.FINEST, "Delta contains change: " + t.b);
		return b[0];
	}

	/**
	 * Clear and cached metadata.
	 */
	protected void clearCache() {
		// ignore
	}

	/**
	 * Creates the module for a given project.
	 * 
	 * @param project a project to create modules for
	 * @return a module, or <code>null</code> if there was no module in the project
	 * @see #createModules(IProject)
	 * @deprecated Use createModules(IProject) instead, which supports multiple modules
	 *    per project
	 */
	protected IModule createModule(IProject project) {
		return null;
	}

	/**
	 * Creates the modules that are contained within a given project.
	 * 
	 * @param project a project to create modules for
	 * @return a possibly-empty array of modules
	 */
	protected IModule[] createModules(IProject project) {
		IModule module = createModule(project);
		if (module == null)
			return new IModule[0];
		
		return new IModule[] { module };
	}

	/**
	 * Returns the list of resources that the module should listen to
	 * for state changes. The paths should be project relative paths.
	 * Subclasses can override this method to provide the paths.
	 *
	 * @return a possibly empty array of paths
	 */
	protected IPath[] getListenerPaths() {
		return null;
	}
}