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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.resources.*;
/**
 * "Smart" publisher that only publishes files that have been
 * modified or changed on the server. Deletes files that are no
 * longer in the workbench.
 */
public class SmartPublisher extends AbstractPublisher {
	/**
	 * SmartPublisher constructor comment.
	 */
	public SmartPublisher() {
		super();
	}

	/**
	 * Returns true if the remote resource should be deleted, and false
	 * if it should be left on the server.
	 *
	 * @return boolean
	 * @param option int
	 */
	public boolean shouldDelete(IModuleResource resource, IPath path, IRemoteResource remote, long resourceTimestamp, long remoteTimestamp) {
		Trace.trace(Trace.FINEST, "shouldDelete: " + resource + " " + path + " " + remote);
		Trace.trace(Trace.FINEST, "  " + (resource instanceof IModuleFolder) + " " + (remote instanceof IRemoteFolder));
		if (resource == null)
			return true;
		
		if (resource instanceof IModuleFolder && !(remote instanceof IRemoteFolder))
			return true;
		else if (!(resource instanceof IModuleFolder) && remote instanceof IRemoteFolder)
			return true;
		
		return false;
	}

	/**
	 * Returns true if the resource should be published, and false
	 * if it should be left on the server.
	 *
	 * @return boolean
	 * @param option int
	 */
	public boolean shouldPublish(IModuleResource resource, IPath path, IRemoteResource remote, long resourceTimestamp, long remoteTimestamp) {
		Trace.trace(Trace.FINEST, "shouldPublish: " + resource + " " + path + " " + remote);
		Trace.trace(Trace.FINEST, "  " + (resource instanceof IModuleFolder) + " " + (remote instanceof IRemoteFolder));
		Trace.trace(Trace.FINEST, "  " + resourceTimestamp + " " + remoteTimestamp);
		// publish if the file doesn't exist on the remote side
		if (remote == null)
			return true;
		
		// publish if the file has changed in the IDE
		if (resourceTimestamp == IRemoteResource.TIMESTAMP_UNKNOWN || resource.getTimestamp() != resourceTimestamp)
			return true;
		
		// publish if the file has changed on the server
		if (remoteTimestamp == IRemoteResource.TIMESTAMP_UNKNOWN || remote.getTimestamp() != remoteTimestamp)
			return true;
		
		if (resource instanceof IModuleFolder && !(remote instanceof IRemoteFolder))
			return true;
		else if (!(resource instanceof IModuleFolder) && remote instanceof IRemoteFolder)
			return true;
		
		return false;
	}
}
