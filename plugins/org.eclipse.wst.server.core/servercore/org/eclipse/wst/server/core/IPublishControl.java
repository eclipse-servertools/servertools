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
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.resources.*;
/**
 * The publish controller that caches and provides information
 * about the current publishing to the publisher.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IPublishControl {
	/**
	 * Returns the mapping of this file on the remote system. Returns
	 * null if this file should not be copied to the remote server.
	 *
	 * @param resource org.eclipse.wst.server.core.publish.IModuleResource
	 * @return org.eclipse.core.resources.IPath
	 */
	public IPath getMappedLocation(IModuleResource resource);

	/**
	 * Returns the root level remote resources on the remote system.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return java.util.List
	 */
	public IRemoteResource[] getRemoteResources();

	/**
	 * Returns true if there may be any files or folders within
	 * this container that should be mapped to the remote system.
	 * Returns false if files within this folder are never copied
	 * to the remote system.
	 *
	 * @param folder org.eclipse.wst.server.core.publish.IModuleFolder
	 * @return boolean
	 */
	public boolean shouldMapMembers(IModuleFolder folder);
	
	/**
	 * Returns the timestamp of the remote resource on the remote
	 * machine after it was last published.
	 *
	 * @param resource org.eclipse.wst.server.publish.IRemoteResource
	 * @return long
	 */
	public long getPublishedTimestamp(IRemoteResource resource);

	/**
	 * Returns the timestamp that the resource was last published.
	 *
	 * @param resource org.eclipse.wst.server.publish.IModuleResource
	 * @return long
	 */
	public long getPublishedTimestamp(IModuleResource resource);
}
