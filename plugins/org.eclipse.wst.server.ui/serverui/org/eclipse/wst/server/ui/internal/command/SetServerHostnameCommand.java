package org.eclipse.wst.server.ui.internal.command;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Command to change the server hostname.
 */
public class SetServerHostnameCommand extends ServerCommand {
	protected String name;
	protected String oldName;

	/**
	 * SetServerHostnameCommand constructor comment.
	 */
	public SetServerHostnameCommand(IServerWorkingCopy server, String name) {
		super(server);
		this.name = name;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldName = server.getHost();
		server.setHost(name);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return ServerUIPlugin.getResource("%serverEditorOverviewServerHostnameDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return ServerUIPlugin.getResource("%serverEditorOverviewServerHostnameCommand");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setHost(oldName);
	}
}