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
 * PublishManager that publishes everything and deletes nothing.
 */
public class FullPublisher extends AbstractPublisher {
	/**
	 * FullPublisher constructor comment.
	 */
	public FullPublisher() {
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
		return true;
	}
}
