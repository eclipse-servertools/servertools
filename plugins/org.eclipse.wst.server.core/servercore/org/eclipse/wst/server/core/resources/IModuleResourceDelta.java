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

import org.eclipse.core.runtime.IPath;
/**
 * 
 */
public interface IModuleResourceDelta {
	public static final int NO_CHANGE = 0;
	public static final int ADDED = 0x1;
	public static final int REMOVED = 0x2;
	public static final int CHANGED = 0x4;

	/**
	 * Returns the path to this resource.
	 */
	public IPath getFullPath();
	
	/**
	 * Returns the resource at this location.
	 */
	public IModuleResource getResource();
	
	/**
	 * Returns the flags for this change, as defined
	 * in the class header.
	 */
	public int getKind();
	
	/**
	 * 
	 * 
	 */
	public IModuleResourceDelta[] getAffectedChildren();
	
	/**
	 * 
	 */
	public IModuleResourceDelta findMember(IPath path);
	
	//public void accept(IResourceDeltaVisitor visitor) throws CoreException;
}