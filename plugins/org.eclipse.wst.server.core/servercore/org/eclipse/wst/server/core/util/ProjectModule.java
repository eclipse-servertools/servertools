/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.ModuleFile;
import org.eclipse.wst.server.core.internal.ModuleFolder;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.model.*;
/**
 * A simple IModuleProject that maps a folder within a project
 * (or the root of the project itself) to the module.
 */
public abstract class ProjectModule extends ModuleDelegate {
	protected IProject project;
	protected IPath root;
	
	// change listeners
	private transient List listeners;

	public ProjectModule() {
		// do nothing
	}

	public ProjectModule(IProject project) {
		this.project = project;
	}

	/*
	 * @see IModuleProject#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Returns the root folder.
	 */
	public IPath getRootFolder() {
		return root;
	}

	/*
	 * @see IModuleProject#getModuleResourceDelta(IResourceDelta)
	 */
	/*public IModuleResourceDelta getModuleResourceDelta(IResourceDelta delta) {
		Trace.trace(Trace.FINEST, "> getModuleResourceDelta");
		IPath root2 = null;
		try {
			root2 = getRootFolder();
		} catch (Exception e) {
			Trace.trace(Trace.FINEST, "Error getting root2");
		}
		if (root2 == null) {
			return convertChildren(delta, null);
		}
		class Helper {
			boolean found = false;
			IModuleResourceDelta delta2;
		}
		final Helper helper = new Helper();
		final IPath root3 = root2;
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta visitDelta) {
					if (!helper.found && root3.equals(visitDelta.getProjectRelativePath())) {
						helper.delta2 = convertChildren(visitDelta, null);
						helper.found = true;
						return false;
					}
					return true;
				}
			});
			Trace.trace(Trace.FINEST, "< getModuleResourceDelta");
			return helper.delta2;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not get module resource delta");
		}
		Trace.trace(Trace.FINEST, "< getModuleResourceDelta null");
		return null;
	}*/
	
	/**
	 * 
	 */
	/*protected IModuleResourceDelta convertChildren(IResourceDelta delta, IModuleFolder parent) {
		int flags = delta.getKind();
		int kind = IModuleResourceDelta.NO_CHANGE;
		if (flags == IResourceDelta.ADDED)
			kind = IModuleResourceDelta.ADDED;
		else if (flags == IResourceDelta.REMOVED)
			kind = IModuleResourceDelta.REMOVED;
		else if (flags == IResourceDelta.CHANGED)
			kind = IModuleResourceDelta.CHANGED;
		
		IResource resource = delta.getResource();
		
		IModuleResource pubResource = null;
		if (resource instanceof IContainer)
			pubResource = new ProjectModuleFolder(this, parent, (IContainer) resource);	
		else if (resource instanceof IFile) {
			if (delta.getFlags() == IResourceDelta.MARKERS)
				return null;
			pubResource = new ProjectModuleFile(this, parent, (IFile) resource);
		}
		
		ModuleResourceDelta deployDelta = new ModuleResourceDelta(pubResource, kind);
		
		IResourceDelta[] children = delta.getAffectedChildren();
		if (children != null && pubResource instanceof IModuleFolder) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				IModuleResourceDelta childDelta = convertChildren(children[i], (IModuleFolder) pubResource);
				if (childDelta != null)
					deployDelta.addChild(childDelta);
			}
		}
		return deployDelta;
	}*/

	/*
	 * @see IModule#getMemento()
	 */
	public String getId() {
		return getProject().getName();
	}
	
	/*
	 * @see IModule#getPublishStatus()
	 */
	public IStatus validate() {
		return null;
	}

	/*
	 * @see IModule#getPublishStatus()
	 */
	public IStatus canPublish() {
		return null;
	}

	/*
	 * @see IModule#members()
	 */
	public IModuleResource[] members() throws CoreException {
		IPath root2 = null;
		try {
			root2 = getRootFolder();
		} catch (Exception e) {
			// ignore
		}
		try {
			if (root2 == null || root2.isRoot() || root2.equals(new Path("")) || root2.equals(new Path("/")))
				return getModuleResources(Path.EMPTY, getProject());
			
			IFolder folder = project.getFolder(root2);
			return getModuleResources(Path.EMPTY, folder);
		} catch (CoreException e) {
			throw e;
		}
	}

	protected IModuleResource[] getModuleResources(IPath path, IContainer container) throws CoreException {
		List list = new ArrayList();

 		IResource[] resources = container.members();
	 	if (resources != null) {
	 		int size = resources.length;
	 		for (int i = 0; i < size; i++) {
				IResource resource = resources[i];
				if (resource instanceof IContainer) {
					IContainer container2 = (IContainer) resource;
					ModuleFolder mf = new ModuleFolder(container2.getName(), path);
					mf.setMembers(getModuleResources(path.append(container2.getName()), container2));
					list.add(mf);
				} else if (resource instanceof IFile) {
					IFile file = (IFile) resource;
					list.add(new ModuleFile(file.getName(), path, file.getModificationStamp()));
				}
			}
	 	}
	 	
	 	IModuleResource[] moduleResources = new IModuleResource[list.size()];
	 	list.toArray(moduleResources);
	 	return moduleResources;
	}

	/*
	 * @see IModule#getName()
	 */
	public String getName() {
		return getProject().getName();
	}
	
	/**
	 * Returns true if this module currently exists, and false if it has
	 * been deleted or moved and is no longer represented by this module.
	 *
	 * @return boolean
	 */
	public boolean exists() {
		return (getProject() != null && getProject().exists());
	}
	
	/**
	 * 
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ProjectModule))
			return false;

		ProjectModule dp = (ProjectModule) obj;
		//if (getFactoryId() != null && !getFactoryId().equals(dp.getFactoryId()))
		//	return false;
			
		IPath root2 = null;
		try {
			root2 = getRootFolder();
		} catch (Exception e) {
			// ignore
		}
		
		IPath root3 = null;
		try {
			root3 = dp.getRootFolder();
		} catch (Exception e) {
			// ignore
		}
		
		if (project != null && project.exists() && !project.equals(dp.getProject()))
			return false;
		
		if (getId() != null && !getId().equals(dp.getId()))
			return false;

		if (root2 == null && root3 != null)
			return false;
		if (root2 != null && !root2.equals(root3))
			return false;

		return true;
	}

	/**
	 * Add a listener for the module.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void addModuleListener(IModuleListener listener) {
		Trace.trace(Trace.FINEST, "Adding module listener " + listener + " to " + this);
	
		if (listeners == null)
			listeners = new ArrayList();
		else if (listeners.contains(listener))
			return;
		listeners.add(listener);
	}
	
	/**
	 * Add a listener for the module.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void removeModuleListener(IModuleListener listener) {
		Trace.trace(Trace.FINEST, "Removing module listener " + listener + " from " + this);
	
		if (listeners != null)
			listeners.remove(listener);
	}
	
	/**
	 * Fire a module change event.
	 */
	protected void fireModuleChangeEvent(boolean isChange, IModule[] added, IModule[] changed, IModule[] removed) {
		Trace.trace(Trace.FINEST, "->- Firing module change event: " + getName() + " (" + isChange + ") ->-");
	
		if (listeners == null || listeners.isEmpty())
			return;
	
		int size = listeners.size();
		IModuleListener[] dcl = new IModuleListener[size];
		listeners.toArray(dcl);
		
		ModuleEvent event = new ModuleEvent(getModule(), isChange, added, changed, removed);
	
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.FINEST, "  Firing module change event to: " + dcl[i]);
				dcl[i].moduleChanged(event);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing module change event", e);
			}
		}
		Trace.trace(Trace.FINEST, "-<- Done firing module change event -<-");
	}
	
	/**
	 * Called when the listener paths from the module factory change.
	 * Use this method to recache information about the module.
	 */
	protected void update() {
		// do nothing
	}

	/**
	 * Returns the child modules of this module.
	 *
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getChildModules() {
		return null;
	}
}