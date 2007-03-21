/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
/**
 * 
 */
public class ModuleFolder implements IModuleFolder {
	private static final IModuleResource[] EMPTY_RESOURCE_ARRAY = new IModuleResource[0];
	protected IContainer container;
	protected String name;
	protected IPath path;
	protected IModuleResource[] members;

	/**
	 * Creates a workspace module folder.
	 * 
	 * @param container
	 * @param name
	 * @param path
	 */
	public ModuleFolder(IContainer container, String name, IPath path) {
		this.container = container;
		this.name = name;
		this.path = path;
	}

	/**
	 * Creates a workspace module folder with no folder reference.
	 * 
	 * @param name
	 * @param path
	 */
	public ModuleFolder(String name, IPath path) {
		this.name = name;
		this.path = path;
	}

	/**
	 * Sets the members (contents) of this folder.
	 * 
	 * @param members
	 */
	public void setMembers(IModuleResource[] members) {
		this.members = members;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResource#getModuleRelativePath()
	 */
	public IPath getModuleRelativePath() {
		return path;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResource#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleFolder#members()
	 */
	public IModuleResource[] members() {
		if (members == null)
			return EMPTY_RESOURCE_ARRAY;
		return members;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (!(obj instanceof ModuleFolder))
			return false;
		
		ModuleFolder mf = (ModuleFolder) obj;
		if (!name.equals(mf.name))
			return false;
		if (!path.equals(mf.path))
			return false;
		return true;
	}

	public int hashCode() {
		return name.hashCode() * 37 + path.hashCode();
	}

	public Object getAdapter(Class cl) {
		if (IContainer.class.equals(cl) || IFolder.class.equals(cl))
			return container;
		return null;
	}

	public String toString() {
		return "ModuleFolder [" + name + ", " + path + "]";
	}
}