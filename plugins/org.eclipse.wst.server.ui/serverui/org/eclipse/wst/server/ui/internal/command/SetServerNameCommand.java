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
 * Command to change the server name.
 */
public class SetServerNameCommand extends ServerCommand {
	protected String name;
	protected String oldName;

	/**
	 * SetServerNameCommand constructor.
	 * 
	 * @param server a server
	 * @param name a name for the server
	 */
	public SetServerNameCommand(IServerWorkingCopy server, String name) {
		super(server);
		this.name = name;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldName = server.getName();
		server.setName(name);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return Messages.serverEditorOverviewServerNameDescription;
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return Messages.serverEditorOverviewServerNameCommand;
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setName(oldName);
	}
}