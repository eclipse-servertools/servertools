/**********************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
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
	private Map<IProject, IModule[]> modules = new HashMap<IProject, IModule[]>();

	/**
	 * Construct a new ProjectModuleFactoryDelegate.
	 */
	public ProjectModuleFactoryDelegate() {
		super();
	}

	/**
	 * Cache modules that exist in the given project.
	 * 
	 * @param project a project to cache
	 * @since 2.0
	 */
	private final IModule[] cacheModules(IProject project) {
		if (project == null || !project.isAccessible())
			return null;
		
		IModule[] m = null;
		try {
			m = modules.get(project);
			if (m != null)
				return m;
		} catch (Exception e) {
			// ignore
		}
		
		try {
			m = createModules(project);
			if (m != null) {
				modules.put(project, m);
				return m;
			}
		} catch (Throwable t) {
			Trace.trace(Trace.SEVERE, "Error creating module", t);
		}
		return new IModule[0];
	}

	/**
	 * Cache all existing modules.
	 */
	private final void cacheModules() {
		try {
			IProject[] projects = getWorkspaceRoot().getProjects();
			int size = projects.length;
			for (int i = 0; i < size; i++) {
				if (projects[i].isAccessible()) {
					boolean cache = true;
					try {
						Object o = modules.get(projects[i]);
						if (o != null)
							cache = false;
					} catch (Exception e) {
						// ignore
					}
					
					if (cache) {
						try {
							IModule[] modules2 = createModules(projects[i]);
							if (modules2 != null)
								modules.put(projects[i], modules2);
						} catch (Throwable t) {
							Trace.trace(Trace.SEVERE, "Error creating module for " + projects[i].getName(), t);
						}
					}
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error caching modules", e);
		}
	}

	/**
	 * Returns the workspace root.
	 * 
	 * @return the workspace root
	 */
	private static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/*
	 * @see ModuleFactoryDelegate#getModules()
	 */
	public final IModule[] getModules() {
		cacheModules();
		
		List<IModule> list = new ArrayList<IModule>();
		Iterator iter = modules.values().iterator();
		while (iter.hasNext()) {
			IModule[] m = (IModule[]) iter.next();
			if (m != null)
				list.addAll(Arrays.asList(m));
		}
		
		IModule[] modules2 = new IModule[list.size()];
		list.toArray(modules2);
		return modules2;
	}

	/**
	 * Handle changes to a project.
	 * 
	 * @param project a project
	 * @param delta a resource delta
	 */
	public final static void handleGlobalProjectChange(IProject project, IResourceDelta delta) {
		ModuleFactory[] factories = ServerPlugin.getModuleFactories();
		int size = factories.length;
		for (int i = 0; i < size; i++) {
			if (factories[i].delegate != null && factories[i].delegate instanceof ProjectModuleFactoryDelegate) {
				ProjectModuleFactoryDelegate pmfd = (ProjectModuleFactoryDelegate) factories[i].delegate;
				if (pmfd.deltaAffectsModules(delta)) {
					pmfd.clearCache(project);
					pmfd.clearCache();
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
	 * Clear cached metadata.
	 * 
	 * @deprecated use {@link #clearCache(IProject)} instead
	 */
	protected void clearCache() {
		// ignore
	}

	/**
	 * Clear cached metadata.
	 * 
	 * @since 2.0
	 */
	protected void clearCache(IProject project) {
		modules = new HashMap<IProject, IModule[]>();
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

	/*
	 * @see ModuleFactoryDelegate#getModules(IProject)
	 * @since 2.0
	 */
	public IModule[] getModules(IProject project) {
		return cacheModules(project);
	}

	/*
	 * @see ModuleFactoryDelegate#findModule(String)
	 * @since 2.0
	 */
	public IModule findModule(String id) {
		try {
			// first assume that the id is a project name
			IProject project = getWorkspaceRoot().getProject(id);
			if (project != null) {
				IModule[] m = cacheModules(project);
				if (m != null) {
					int size = m.length;
					for (int i = 0; i < size; i++) {
						String id2 = m[i].getId();
						int index = id2.indexOf(":");
						if (index >= 0)
							id2 = id2.substring(index+1);
						
						if (id.equals(id2))
							return m[i];
					}
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.FINER, "Could not find " + id + ". Reverting to default behaviour", e);
		}
		
		// otherwise default to searching all modules
		return super.findModule(id);
	}
}