/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.resources;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.model.IModule;
/**
 * 
 */
public class ProjectModuleResource implements IModuleResource {
	protected IModule module;
	protected IModuleFolder parent;
	protected IResource resource;

	public ProjectModuleResource(IModule module, IModuleFolder parent, IResource resource) {
		this.module = module;
		this.parent = parent;
		this.resource = resource;
	}

	/*
	 * @see IModuleResource#getName()
	 */
	public String getName() {
		return resource.getName();
	}

	/*
	 * @see IModuleResource#getModule()
	 */
	public IModule getModule() {
		return module;
	}

	/*
	 * @see IModuleResource#getParent()
	 */
	public IModuleFolder getParent() {
		return parent;
	}

	/*
	 * @see IModuleResource#getPath()
	 */
	public IPath getPath() {
		if (parent == null)
			return new Path(getName());
		else {
			IPath path = parent.getPath();
			return path.append(getName());
		}
	}

	public IResource getResource() {
		return resource;
	}

	/*
	 * @see IModuleResource#getTimestamp()
	 */
	public long getTimestamp() {
		return resource.getModificationStamp();
	}

	/**
	 * 
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ProjectModuleResource))
			return false;
		
		ProjectModuleResource ppr = (ProjectModuleResource) obj;
		if (ppr.getModule() != module)
			return false;
		if (parent == null && ppr.getParent() != null)
			return false;
		if (parent != null && !parent.equals(ppr.getParent()))
			return false;
		if (resource != null && !resource.equals(ppr.getResource()))
			return false;
		return true;
	}
	
	public String toString() {
		return getPath() + "(" + getTimestamp() + ")";
	}
}