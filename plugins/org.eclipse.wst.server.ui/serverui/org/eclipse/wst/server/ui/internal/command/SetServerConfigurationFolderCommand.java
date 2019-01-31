/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.command;

import org.eclipse.core.resources.IFolder;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Command to change the server configuration folder.
 */
public class SetServerConfigurationFolderCommand extends ServerCommand {
	protected IFolder folder;
	protected IFolder oldFolder;

	/**
	 * SetServerConfigurationFolderCommand constructor.
	 * 
	 * @param server a server
	 * @param folder a new server configuration location
	 */
	public SetServerConfigurationFolderCommand(IServerWorkingCopy server, IFolder folder) {
		super(server, Messages.serverEditorOverviewServerHostnameCommand);
		this.folder = folder;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldFolder = server.getServerConfiguration();
		server.setServerConfiguration(folder);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setServerConfiguration(oldFolder);
	}
}