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

import java.util.*;

import org.eclipse.wst.server.core.internal.Trace;
/**
 * An implementation of a remote folder.
 */
public class RemoteFolder extends RemoteResource implements IRemoteFolder {
	protected List children;

	/**
	 * RemoteFolder constructor comment.
	 */
	public RemoteFolder(IRemoteFolder parent, String name, long timestamp) {
		super(parent, name, timestamp);
	}

	/**
	 * 
	 * @param resource org.eclipse.wst.server.core.model.IRemoteResource
	 */
	public void addChild(IRemoteResource resource) {
		List children2 = getContents();
		if (!children2.contains(resource))
			children2.add(resource);
	}

	/**
	 * Returns a list of IRemoteResources directly contained
	 * within this folder.
	 *
	 * @return java.util.List
	 */
	public List getContents() {
		if (children == null)
			children = new ArrayList();
		return children;
	}
	
	/**
	 * Output the remote folder to a string
	 * @return java.lang.String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getPath().toString());
		sb.append("[");
	
		boolean first = true;
		Iterator iterator = getContents().iterator();
		while (iterator.hasNext()) {
			IRemoteResource remote = (IRemoteResource) iterator.next();
			if (!first)
				sb.append(", ");
			first = false;
			sb.append(remote.toString());
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Trace the remote folder's output.
	 */
	public void trace(String indent) {
		Trace.trace(indent + getPath().toString() + " (" + getTimestamp() + ") [");
	
		Iterator iterator = getContents().iterator();
		while (iterator.hasNext()) {
			IRemoteResource remote = (IRemoteResource) iterator.next();
			if (remote instanceof RemoteFolder) {
				((RemoteFolder) remote).trace(indent + "  ");
			} else
				Trace.trace(indent + "  " + remote.toString());
		}
		Trace.trace(indent + "]");
	}
}
