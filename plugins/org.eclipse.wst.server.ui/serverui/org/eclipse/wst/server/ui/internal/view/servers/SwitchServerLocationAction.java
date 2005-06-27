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
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Action to switch a server's location between the workspace and metadata.
 */
public class SwitchServerLocationAction extends Action {
	protected Server server;

	/**
	 * An action to switch a server's location between the workspace and metadata.
	 * 
	 * @param server a servers
	 */
	public SwitchServerLocationAction(IServer server) {
		super();
		this.server = (Server) server;
		if (this.server.getFile() != null)
			setText(Messages.actionMoveServerToMetadata);
		else
			setText(Messages.actionMoveServerToWorkspace);
	}

	/**
	 * Invoked when an action occurs. 
	 */
	public void run() {
		try {
			Server.switchLocation(server, null);
		} catch (CoreException ce) {
			// ignore for now
		}
	}
}