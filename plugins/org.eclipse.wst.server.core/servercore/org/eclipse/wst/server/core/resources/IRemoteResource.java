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
 * A remote resource that has a name, parent, path, and timestamp.
 */
public interface IRemoteResource {
	public static final long TIMESTAMP_UNKNOWN = -1;

	/**
	 * Returns the name of the remote resource.
	 *
	 * @return java.lang.String
	 */
	public String getName();

	/**
	 * Returns the parent folder, or null if we're at the root.
	 *
	 * @return org.eclipse.wst.server.model.IRemoteFolder
	 */
	public IRemoteFolder getParent();

	/**
	 * Return the path to this resource.
	 *
	 * @return org.eclipse.core.runtime.IPath
	 */
	public IPath getPath();

	/**
	 * Returns the timestamp of the remote resource. This timestamp
	 * does not need to match the local machine's time, but it
	 * must be constantly increasing with time. (i.e. files with a
	 * larger timestamp were created later)
	 *
	 * @return long
	 */
	public long getTimestamp();
}
