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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.ModuleFile;
import org.eclipse.wst.server.core.internal.ModuleFolder;
import org.eclipse.wst.server.core.model.*;
/**
 * A simple IModuleProject that maps a folder within a project
 * (or the root of the project itself) to the module.
 * 
 * @since 1.0
 */
public abstract class ProjectModule extends ModuleDelegate {
	private IProject project;

	/**
	 * Create a new project module.
	 */
	public ProjectModule() {
		// do nothing
	}

	/**
	 * Create a new project module in the given project.
	 * 
	 * @param project the project containing the module
	 */
	public ProjectModule(IProject project) {
		this.project = project;
	}

	/**
	 * Returns the project that the module is contained in.
	 * 
	 * @return the project that the module is contained in
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Helper method - returns the module's id.
	 * 
	 * @return the module id
	 */
	public String getId() {
		return getProject().getName();
		//return getModule().getId();
	}

	/**
	 * @see ModuleDelegate#validate()
	 */
	public IStatus validate() {
		return null;
	}

	/**
	 * Helper method - returns the module's name.
	 * 
	 * @return the module name
	 */
	public String getName() {
		return getProject().getName();
		//return getModule().getName();
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
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ProjectModule))
			return false;
		
		ProjectModule dp = (ProjectModule) obj;
		//if (getFactoryId() != null && !getFactoryId().equals(dp.getFactoryId()))
		//	return false;
		
		if (project != null && project.exists() && !project.equals(dp.getProject()))
			return false;
		
		if (getId() != null && !getId().equals(dp.getId()))
			return false;
		
		return true;
	}

	/**
	 * Returns the child modules of this module.
	 *
	 * @return an array of child modules
	 */
	public IModule[] getChildModules() {
		return null;
	}

	/**
	 * Returns <code>true</code> if this module has a simple structure based on a
	 * single root folder, and <code>false</code> otherwise.
	 * <p>
	 * In a single root structure, all files that are contained within the root folder
	 * are part of the module, and are already in the correct module structure. No
	 * module resources exist outside of this single folder.
	 * </p>
	 * 
	 * @return <code>true</code> if this module has a single root structure, and
	 *    <code>false</code> otherwise
	 */
	public boolean isSingleRootStructure() {
		return false;
	}

	/**
	 * Basic implementation of members() method. Assumes that the entire project should
	 * be published to a server.
	 * 
	 * @see ModuleDelegate#members()
	 */
	public IModuleResource[] members() throws CoreException {
		return getModuleResources(Path.EMPTY, getProject());
	}

	/**
	 * Return the module resources for a given path.
	 * 
	 * @param path a path
	 * @param container a container
	 * @return an array of module resources
	 * @throws CoreException
	 */
	protected IModuleResource[] getModuleResources(IPath path, IContainer container) throws CoreException {
		IResource[] resources = container.members();
		if (resources != null) {
			int size = resources.length;
			List list = new ArrayList(size);
			for (int i = 0; i < size; i++) {
				IResource resource = resources[i];
				if (resource instanceof IContainer) {
					IContainer container2 = (IContainer) resource;
					if (container2 != null && container2.exists()) {
						ModuleFolder mf = new ModuleFolder(container, container2.getName(), path);
						mf.setMembers(getModuleResources(path.append(container2.getName()), container2));
						list.add(mf);
					}
				} else if (resource instanceof IFile) {
					IFile file = (IFile) resource;
					if (file != null && file.exists())
						list.add(new ModuleFile(file, file.getName(), path));
				}
			}
			IModuleResource[] moduleResources = new IModuleResource[list.size()];
			list.toArray(moduleResources);
			return moduleResources;
		}
		return new IModuleResource[0];
	}
}