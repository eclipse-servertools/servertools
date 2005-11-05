/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
/**
 * Action to update a server's status.
 */
public class UpdateServerJob extends ChainedJob {
	/**
	 * An action to update the status of a server.
	 * 
	 * @param server a server
	 */
	public UpdateServerJob(IServer server) {
		super(NLS.bind(Messages.jobUpdateServer, server.getName()), server);
	}

	public IStatus run(IProgressMonitor monitor) {
		getServer().loadAdapter(ServerBehaviourDelegate.class, monitor);
		
		return new Status(IStatus.OK, ServerPlugin.PLUGIN_ID, 0, "", null);
	}
}