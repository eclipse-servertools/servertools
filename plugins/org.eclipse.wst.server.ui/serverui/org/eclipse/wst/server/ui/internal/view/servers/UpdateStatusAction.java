/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Action to update a server's status.
 */
public class UpdateStatusAction extends Action {
	protected IServer server;

	/**
	 * An action to update the status of a server.
	 * 
	 * @param server a server
	 */
	public UpdateStatusAction(IServer server) {
		super(Messages.actionUpdateStatus);
		this.server = server;
		if (server.getServerType() == null || server.getServerState() != IServer.STATE_UNKNOWN)
			setEnabled(false);
	}

	/**
	 * Invoked when an action occurs. 
	 */
	public void run() {
		UpdateServerJob job = new UpdateServerJob(server);
		job.schedule();
	}
}