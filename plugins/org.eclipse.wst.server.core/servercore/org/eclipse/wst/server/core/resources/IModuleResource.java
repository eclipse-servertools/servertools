/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
import org.eclipse.wst.server.core.model.IModule;
/**
 * A module resource is a workspace file or folder contained
 * in a module.
 * <p>
 * Every module resource is an instance of either
 * {@link IModuleFile} or {@link IModuleFolder}.
 * </p>
 * <p>
 * [issue: There is currently no way for a client to
 * distinguish files from folders except by an instanceof
 * check.]
 * </p>
 * <p>
 * Each module resource belongs to exactly one module.
 * </p>
 * <p>
 * This interface is not intended to be implemented directly.
 * Module factories may implement one of the subinterfaces 
 * <code>IModuleFile</code> and <code>IModuleFolder</code>.
 * </p>
 * <p>
 * [issue: It is not clear that there is any need for module
 * factories to implement IModuleFile and IModuleFolder.
 * All that seems to be needed is an IResource paired with
 * an IModule. The fact that all these interfaces are in
 * org.eclipse.wst.server.core.resources suggests that this
 * is the intent. If that's the case, one fixed implementation
 * will do. The only API methods needed are:
 *   public IResource getResource();
 *   public IModule getModule();
 *   public IContainer getModuleRoot();
 * Names, paths, parents, and timestamps can all be handled
 * by existing IResource API.]
 * </p>
 * <p>
 * [issue: Equality/identify for module resources?]
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IModuleResource {
	
	/** 
	 * Time stamp constant (value -1) indicating the time stamp is
	 * unknown.
	 *
	 * @see #getTimestamp()
	 */
	public static final long TIMESTAMP_UNKNOWN = -1;

	/**
	 * Returns the name of this module resource.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns the module that contains this module resource.
	 * 
	 * @return the module 
	 */
	public IModule getModule();

	/**
	 * Returns the parent folder in the same module, if any.
	 * <p>
	 * Note that a root resource is one that appears among
	 * the list <code>getModule().members()</code>.
	 * </p>
	 *
	 * @returns the parent module folder, or <code>null</code>
	 * this is a module root
	 */
	public IModuleFolder getParent();

	/**
	 * Return the path to this module resource.
	 * <p>
	 * Paths are relative to the module. That is, a root
	 * resource named "foo" has path "foo"; a resource 
	 * named "bar" insider it has path "foo/bar", and so on.
	 * </p>
	 *
	 * @return a module-relative resource path
	 */
	public IPath getPath();

	/**
	 * Returns the time stamp of the module resource.
	 * <p>
	 * This time stamp need not match the local machine's time, but it
	 * must be constantly increasing with time (i.e., files with a
	 * larger time stamp must have been created later).
	 * </p>
	 * <p>
	 * [issue: Rename to "getTimeStamp" for consistency with
	 * IResource.getLocalTimeStamp().]
	 * </p>
	 *
	 * @return a time stamp, or {@link #TIMESTAMP_UNKNOWN}
	 */
	public long getTimestamp();
}
