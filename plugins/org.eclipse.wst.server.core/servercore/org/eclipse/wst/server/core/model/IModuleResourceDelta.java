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
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IPath;
/**
 * 
 */
public interface IModuleResourceDelta {
	public static final int NO_CHANGE = 0;
	public static final int ADDED = 1;
	public static final int CHANGED = 2;
	public static final int REMOVED = 3;

	/**
	 * 
	 * @return
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
	 * @see #getAffectedChildren(int)
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
	
	/**
	 * Finds and returns the descendent delta identified by the given path in
	 * this delta, or <code>null</code> if no such descendent exists.
	 * The supplied path may be absolute or relative; in either case, it is
	 * interpreted as relative to this delta.   Trailing separators are ignored.
	 * If the path is empty this delta is returned.
	 * <p>
	 * This is a convenience method to avoid manual traversal of the delta
	 * tree in cases where the listener is only interested in changes to
	 * particular resources.  Calling this method will generally be
	 * faster than manually traversing the delta to a particular descendent.
	 * </p>
	 * @param path the path of the desired descendent delta
	 * @return the descendent delta, or <code>null</code> if no such
	 * 		descendent exists in the delta
	 * @since 2.0
	 */
	//public IModuleResourceDelta findMember(IPath path);
	
	/**
	 * Returns resource deltas for all children of this resource 
	 * whose kind is included in the given mask. Kind masks are formed
	 * by the bitwise or of <code>IResourceDelta</code> kind constants.
	 * Returns an empty array if there are no affected children.
	 * <p>
	 * This is a convenience method, fully equivalent to:
	 * <pre>
	 *   getAffectedChildren(kindMask, IResource.NONE);
	 * </pre>
	 * Team-private member resources are <b>not</b> included in the result.
	 * </p>
	 *
	 * @param kindMask a mask formed by the bitwise or of <code>IResourceDelta </code> 
	 *    delta kind constants
	 * @return the resource deltas for all affected children
	 * @see IResourceDelta#ADDED
	 * @see IResourceDelta#REMOVED
	 * @see IResourceDelta#CHANGED
	 * @see #getAffectedChildren(int)
	 */
	//public IModuleResourceDelta[] getAffectedChildren(int kindMask);
	
	/**
	 * Accepts the given visitor.
	 * The only kinds of resource deltas visited 
	 * are <code>ADDED</code>, <code>REMOVED</code>, 
	 * and <code>CHANGED</code>.
	 * The visitor's <code>visit</code> method is called with this
	 * resource delta if applicable. If the visitor returns <code>true</code>,
	 * the resource delta's children are also visited.
	 * <p>
	 * This is a convenience method, fully equivalent to 
	 * <code>accept(visitor, IResource.NONE)</code>.
	 * Although the visitor will be invoked for this resource delta, it will not be
	 * invoked for any team-private member resources.
	 * </p>
	 *
	 * @param visitor the visitor
	 * @exception CoreException if the visitor failed with this exception.
	 * @see IResourceDeltaVisitor#visit(IResourceDelta)
	 */
	//public void accept(IModuleResourceDeltaVisitor visitor) throws CoreException;
}