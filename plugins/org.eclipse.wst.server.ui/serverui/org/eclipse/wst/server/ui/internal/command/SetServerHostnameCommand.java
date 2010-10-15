/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.command;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Command to change the server hostname.
 */
public class SetServerHostnameCommand extends ServerCommand {
	protected String name;
	protected String oldName;

	/**
	 * SetServerHostnameCommand constructor.
	 * 
	 * @param server a server
	 * @param name a hostname or IP address
	 */
	public SetServerHostnameCommand(IServerWorkingCopy server, String name) {
		super(server, Messages.serverEditorOverviewServerHostnameCommand);
		this.name = name;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldName = server.getHost();
		server.setHost(name);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setHost(oldName);
	}
}