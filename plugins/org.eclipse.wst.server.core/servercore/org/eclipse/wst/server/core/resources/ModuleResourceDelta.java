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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
/**
 * 
 */
public class ModuleResourceDelta implements IModuleResourceDelta {
	protected IModuleResource resource;
	protected int flags;
	
	protected List children;
	
	private static final IModuleResourceDelta[] NO_CHILDREN = new ModuleResourceDelta[0];
	
	public ModuleResourceDelta(IModuleResource resource, int flags) {
		this.resource = resource;
		this.flags = flags;
	}

	/**
	 * @see IModuleResourceDelta#getPath()
	 */
	public IPath getFullPath() {
		return resource.getPath();
	}

	/**
	 * @see IModuleResourceDelta#getResource()
	 */
	public IModuleResource getResource() {
		return resource;
	}

	/**
	 * @see IModuleResourceDelta#getFlags()
	 */
	public int getKind() {
		return flags;
	}
	
	/**
	 * 
	 */
	public void addChild(IModuleResourceDelta delta) {
		if (children == null)
			children = new ArrayList(4);
		children.add(delta);
	}

	/**
	 * 
	 * 
	 */
	public IModuleResourceDelta[] getAffectedChildren() {
		if (children == null || children.size() == 0)
			return NO_CHILDREN;
		
		ModuleResourceDelta[] delta = new ModuleResourceDelta[children.size()];
		children.toArray(delta);
		return delta;
	}
	
	/**
	 * 
	 */
	public IModuleResourceDelta findMember(IPath path) {
		return null;
	}
}