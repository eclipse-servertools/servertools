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
import org.eclipse.core.runtime.Path;
/**
 * An implementation of a remote resource. The path is the
 * path of the parent with the resource name appended.
 */
public class RemoteResource implements IRemoteResource {
	protected IRemoteFolder parent;
	protected String name;
	protected long timestamp = TIMESTAMP_UNKNOWN;

	/**
	 * RemoteResource constructor comment.
	 */
	public RemoteResource(IRemoteFolder parent, String name, long timestamp) {
		super();
		this.parent = parent;
		this.name = name;
		this.timestamp = timestamp;
	}

	/**
	 * 
	 * @return boolean
	 * @param obj java.lang.Object
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof RemoteResource))
			return false;

		RemoteResource remote = (RemoteResource) obj;
		if (name != null && !name.equals(remote.getName()))
			return false;

		if (parent == null && remote.getParent() != null)
			return false;

		if (parent != null && !parent.equals(remote.getParent()))
			return false;

		if (timestamp != remote.getTimestamp())
			return false;

		return true;
	}

	/**
	 * Returns the name of the remote resource.
	 *
	 * @return java.lang.String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the parent folder, or null if we're at the root.
	 *
	 * @return org.eclipse.wst.server.core.model.IRemoteFolder
	 */
	public IRemoteFolder getParent() {
		return parent;
	}

	/**
	 * Return the path to this resource.
	 *
	 * @return org.eclipse.core.runtime.IPath
	 */
	public IPath getPath() {
		if (parent == null)
			return new Path(getName());
		IPath path = parent.getPath();
		return path.append(getName());
	}

	/**
	 * Returns the timestamp of the remote resource. This timestamp
	 * does not need to match the local machine's time, but it
	 * must be constantly increasing with time. (i.e. files with a
	 * larger timestamp were created later)
	 *
	 * @return long
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns this remote resource as a string.
	 *
	 * @return java.lang.String
	 */
	public String toString() {
		return getPath().toString() + " (" + getTimestamp() + ")";
	}
}
