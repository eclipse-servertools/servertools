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
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.resources.*;
/**
 * A publisher for a project within the workbench. This class
 * knows which resources within the workbench should be published
 * to the server, and where they go. In addition, it has the
 * ability to publish files and folders to the server, and delete
 * them as well.
 *
 * <p>The class can assume that the delete(), publish(), and
 * getRemoteResources() methods will only be called between calls
 * to publishStart() and publishStop() on the server.
 * (in other words, they can make use of a shared connection
 * with the server) Any other methods are valid to be
 * called at any time.</p>
 */
public interface IPublisher {
	/**
	 * Returns the mapping of this file on the remote
	 * system. Return null if this file should not be
	 * copied to the remote server.
	 *
	 * @param resource org.eclipse.wst.server.core.resources.IModuleResource
	 * @return org.eclipse.core.resources.IPath
	 */
	public IPath getMappedLocation(IModuleResource resource);

	/**
	 * Returns true if there may be any files or folders within
	 * this container that should be mapped to the remote system.
	 * Returns false if files within this folder are never copied
	 * to the remote system.
	 *
	 * @param folder org.eclipse.wst.server.core.resources.IModuleFolder
	 * @return boolean
	 */
	public boolean shouldMapMembers(IModuleFolder folder);

	/**
	 * Returns a list of the remote resources at the root level
	 * of the remote location. When this method is called, the
	 * full remote resource tree should be built, which can be
	 * found from these root resources.
	 *
	 * <p>The resources returned may be folders or files. This method
	 * should not cache data between calls. (in other words, each
	 * call to this method must result in obtaining the data from
	 * the server)</p>
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclipse.wst.server.core.resources.IRemoteResource[]
	 */
	public IRemoteResource[] getRemoteResources(IProgressMonitor monitor) throws CoreException;

	/**
	 * Delete the following resources from the remote machine. The
	 * remote resource may be a file or folder. This method should
	 * return an array of simple IStatus (should not be MultiStatus)
	 * with the result of every deletion attempt.
	 *
	 * @param resource org.eclipse.wst.server.resources.IRemoteResource[]
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclipse.core.runtime.IStatus[]
	 */
	public IStatus[] delete(IRemoteResource[] resource, IProgressMonitor monitor) throws CoreException;

	/**
	 * Publish the given resources to the remote location. If the
	 * resource is a folder (IFolder), the folder should be
	 * recreated on the remote machine. If the resource is just a
	 * file, it should be copied to the remote machine.  This
	 * method should return an array of simple IStatus (should
	 * not be MultiStatus) with the result of every publish
	 * attempt.
	 *
	 * @param resource org.eclipse.wst.server.resources.IModuleResource[]
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclipse.core.runtime.IStatus[]
	 */
	public IStatus[] publish(IModuleResource[] resource, IProgressMonitor monitor) throws CoreException;

	/**
	 * Delete the entire module from the remote location.
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus deleteAll(IProgressMonitor monitor) throws CoreException;
}
