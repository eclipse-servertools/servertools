/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IPath;
/**
 * A module resource delta.
 * 
 * @since 1.0
 */
public interface IModuleResourceDelta {
	/**
	 * Kind constant (value 0) for no change.
	 * 
	 * @see #getKind()
	 */
	public static final int NO_CHANGE = 0;

	/**
	 * Kind constant (value 1) for added resources.
	 * 
	 * @see #getKind()
	 */
	public static final int ADDED = 1;

	/**
	 * Kind constant (value 2) for changed resources.
	 * 
	 * @see #getKind()
	 */
	public static final int CHANGED = 2;

	/**
	 * Kind constant (value 3) for removed resources.
	 * 
	 * @see #getKind()
	 */
	public static final int REMOVED = 3;

	/**
	 * Returns the module resource represented by this delta.
	 * 
	 * @return the corresponding module resource
	 */
	public IModuleResource getModuleResource();

	/**
	 * Returns the kind of this resource delta.
	 * Normally, one of <code>ADDED</code>, 
	 * <code>REMOVED</code>, <code>CHANGED</code>.
	 * 
	 * @return the kind of this resource delta
	 * 
	 * @see IModuleResourceDelta#ADDED
	 * @see IModuleResourceDelta#REMOVED
	 * @see IModuleResourceDelta#CHANGED
	 */
	public int getKind();

	/**
	 * Returns module resource deltas for all children of this resource 
	 * which were added, removed, or changed. Returns an empty
	 * array if there are no affected children.
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * <pre>
	 *   getAffectedChildren(ADDED | REMOVED | CHANGED, IResource.NONE);
	 * </pre>
	 * </p>
	 *
	 * @return the resource deltas for all affected children
	 * @see IModuleResourceDelta#ADDED
	 * @see IModuleResourceDelta#REMOVED
	 * @see IModuleResourceDelta#CHANGED
	 */
	public IModuleResourceDelta[] getAffectedChildren();
	
	/**
	 * Returns the module-relative path of this resource delta.
	 * Returns the empty path for resources in the module root.
	 * <p>
	 * A resource's module-relative path indicates the route from the module
	 * to the resource. Within a module, there is exactly one such path
	 * for any given resource. The returned path never has a trailing separator.
	 * </p>
	 * @return the module-relative path of this resource delta
	 * @see IModuleResource#getModuleRelativePath()
	 */
	public IPath getModuleRelativePath();
}