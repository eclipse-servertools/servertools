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
import org.eclipse.core.runtime.IPath;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.ResourceManager;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;
/**
 * 
 */
public abstract class ProjectModuleFactoryDelegate extends ModuleFactoryDelegate {
	protected static IResourceChangeListener listener;
	
	protected static List factories = new ArrayList();

	protected List added;
	protected List removed;

	// map from IProject to IModuleProject
	protected Map projects;

	/**
	 * Construct a new ProjectModuleFactoryDelegate.
	 */
	public ProjectModuleFactoryDelegate() {
		super();
		
		factories.add(this);
		
		addListener();
	}

	/**
	 * Cache any preexisting module.
	 */
	protected void cacheModules() {
		projects = new HashMap();
		try {
			IProject[] projects2 = getWorkspaceRoot().getProjects();
			int size = projects2.length;
			for (int i = 0; i < size; i++) {
				//Trace.trace("caching: " + this + " " + projects[i] + " " + isValidModule(projects[i]));
				if (isValidModule(projects2[i])) {
					addModuleProject(projects2[i]);
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error caching modules", e);
		}
		fireEvents();
	}

	/**
	 * Return the workspace root.
	 * 
	 * @return org.eclipse.core.resources.IWorkspaceRoot
	 */
	protected static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Returns the module project for the given project, or null
	 * if this factory does not have a module for the given project.
	 * 
	 * @param project org.eclipse.core.resources.IProject
	 * @return org.eclipse.wst.server.core.model.IModuleProject
	 */
	public IModule getModuleProject(IProject project) {
		try {
			return (IModule) projects.get(project);
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	/**
	 * Add a resource listener to the workspace.
	 */
	protected static void addListener() {
		if (listener != null)
			return;

		listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				Trace.trace(Trace.FINEST, "->- ProjectModuleFactoryDelegate listener responding to resource change: " + event.getType() + " ->-");
				try {
					IResourceDelta delta = event.getDelta();
					
					//if (delta.getFlags() == IResourceDelta.MARKERS || delta.getFlags() == IResourceDelta.NO_CHANGE)
					//	return;
				
					delta.accept(new IResourceDeltaVisitor() {
						public boolean visit(IResourceDelta visitorDelta) {
							IResource resource = visitorDelta.getResource();
							//Trace.trace(Trace.FINEST, "resource: " + resource);
	
							// only respond changes within projects
							if (resource != null && resource instanceof IProject) {
								IProject project = (IProject) resource;
								handleGlobalProjectChange(project, visitorDelta);
								return true;
							} else if (resource != null && resource.getProject() != null) {
								return false;
							} else
								return true;
						}
					});
				} catch (Exception e) {
					//Trace.trace(Trace.SEVERE, "Error responding to resource change", e);
				}
				fireGlobalEvents();
				Trace.trace(Trace.FINEST, "-<- Done ProjectModuleFactoryDelegate responding to resource change -<-");
			}
		};
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}
	
	/**
	 * Handle changes to a project.
	 * 
	 * @param project org.eclipse.core.resources.IProject
	 * @param delta org.eclipse.core.resources.IResourceDelta
	 */
	protected static void handleGlobalProjectChange(final IProject project, IResourceDelta delta) {
		// handle project level changes
		Iterator iterator = factories.iterator();
		while (iterator.hasNext()) {
			ProjectModuleFactoryDelegate factory = (ProjectModuleFactoryDelegate) iterator.next();
			//Trace.trace("Firing to: " + factory);
			factory.handleProjectChange(project, delta);
		}
		
		// handle internal updates
		iterator = factories.iterator();
		while (iterator.hasNext()) {
			ProjectModuleFactoryDelegate factory = (ProjectModuleFactoryDelegate) iterator.next();
			//Trace.trace("Firing to: " + factory);
			factory.handleProjectInternalChange(project, delta);
		}
	}
	
	/**
	 * Fire the accumulated module factory events.
	 */
	protected static void fireGlobalEvents() {
		Trace.trace(Trace.FINEST, "Firing global module event");
		Iterator iterator = factories.iterator();
		while (iterator.hasNext()) {
			ProjectModuleFactoryDelegate factory = (ProjectModuleFactoryDelegate) iterator.next();
			factory.updateProjects();
		}
		
		iterator = factories.iterator();
		while (iterator.hasNext()) {
			ProjectModuleFactoryDelegate factory = (ProjectModuleFactoryDelegate) iterator.next();
			factory.fireEvents();
		}
		
		ResourceManager.getInstance().syncModuleEvents();
	}

	/**
	 * Temporary to make sure that all project modules are updated.
	 */
	private void updateProjects() {
		IModule[] modules2 = getModules();
		if (modules2 != null) {
			int size = modules2.length;
			for (int i = 0; i < size; i++) {
				if (modules2[i] instanceof ProjectModule)
					((ProjectModule) modules2[i]).update();
			}
		}
	}

	/**
	 * Handle changes to a project.
	 * 
	 * @param project org.eclipse.core.resources.IProject
	 * @param delta org.eclipse.core.resources.IResourceDelta
	 */
	protected void handleProjectChange(final IProject project, IResourceDelta delta) {
		if (projects.containsKey(project)) {
			// already a module
			if (((delta.getKind() &  IResourceDelta.REMOVED) != 0) || !isValidModule(project)) {
				removeModuleProject(project);
			}
		} else {
			// not a module
			if (isValidModule(project)) {
				addModuleProject(project);
			}
		}
	}
	
	/**
	 * Handle changes to a project.
	 * 
	 * @param project org.eclipse.core.resources.IProject
	 * @param delta org.eclipse.core.resources.IResourceDelta
	 */
	protected void handleProjectInternalChange(final IProject project, IResourceDelta delta) {
		final IPath[] paths = getListenerPaths();
		if (paths != null) {
			final IModule module = getModuleProject(project);
			if (module != null && module instanceof ProjectModule) {
				// check for any changes to the module
				final IPath root = ((ProjectModule) module).getRootFolder();
				IResourceDelta rootDelta = delta.findMember(root);
				if (rootDelta != null)
					((ProjectModule) module).fireModuleChangeEvent(true, null, null, null);
				
				// check for listener paths
				final int size = paths.length;
				class Temp {
					boolean found = false;
				}
				final Temp temp = new Temp();
				try {
					delta.accept(new IResourceDeltaVisitor() {
						public boolean visit(IResourceDelta visitorDelta) {
							if (temp.found)
								return false;
							IPath path = visitorDelta.getProjectRelativePath();
							
							boolean prefix = false;
							for (int i = 0; i < size && !temp.found; i++) {
								if (paths[i].equals(path))
									temp.found = true;
								else if (path.isPrefixOf(paths[i]))
									prefix = true;
							}
							if (temp.found) {
								((ProjectModule) module).update();
								return false;
							} else if (prefix)
								return true;
							else
								return false;
						}
					});
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error searching for listening paths", e);
				}
			}
		}
	}

	/**
	 * Add a module for the given project.
	 * 
	 * @param project org.eclipse.core.resources.IProject
	 */
	protected void addModuleProject(IProject project) {
		IModule module = createModule(project);
		if (module == null)
			return;
		projects.put(project, module);
		//modules.put(module.getId(), module);
		if (added == null)
			added = new ArrayList(2);
		added.add(module);
	}

	/**
	 * Remove the module that represents the given project.
	 * 
	 * @param project org.eclipse.core.resources.IProject
	 */
	protected void removeModuleProject(IProject project) {
		try {
			IModule module = (IModule) projects.get(project);
			projects.remove(project);
			//modules.remove(module.getId());
			if (removed == null)
				removed = new ArrayList(2);
			removed.add(module);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error removing module project", e);
		}
	}

	/**
	 * Fire the accumulated module factory events.
	 */
	protected void fireEvents() {
		if ((added == null || added.isEmpty()) && (removed == null || removed.isEmpty()))
			return;

		IModule[] add = null;
		if (added != null) {
			add = new IModule[added.size()];
			added.toArray(add);
		}
		IModule[] remove = null;
		if (removed != null) {
			remove = new IModule[removed.size()];
			removed.toArray(remove);
		}
		
		fireModuleFactoryEvent(add, remove);
		added = new ArrayList(2);
		removed = new ArrayList(2);
	}

	/**
	 * Returns true if the project represents a module project
	 * of this type.
	 * 
	 * @param project org.eclipse.core.resources.IProject
	 * @return boolean
	 */
	protected abstract boolean isValidModule(IProject project);

	/**
	 * Creates the module project for the given project.
	 * 
	 * @param project org.eclipse.core.resources.IProject
	 * @return org.eclipse.wst.server.core.model.IModuleProject
	 */
	protected abstract IModule createModule(IProject project);

	/**
	 * Returns the list of resources that the module should listen to
	 * for state changes. The paths should be project relative paths.
	 * Subclasses can override this method to provide the paths.
	 *
	 * @return org.eclipse.core.runtime.IPath[]
	 */
	protected IPath[] getListenerPaths() {
		return null;
	}
}