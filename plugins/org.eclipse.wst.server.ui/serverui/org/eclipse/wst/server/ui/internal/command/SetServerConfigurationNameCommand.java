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
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Command to change the server configuration name.
 */
public class SetServerConfigurationNameCommand extends ConfigurationCommand {
	protected String name;
	protected String oldName;

	/**
	 * SetServerConfigurationNameCommand constructor comment.
	 */
	public SetServerConfigurationNameCommand(IServerConfigurationWorkingCopy config, String name) {
		super(config);
		this.name = name;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		oldName = configuration.getName();
		configuration.setName(name);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return ServerUIPlugin.getResource("%serverEditorOverviewServerConfigurationNameDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return ServerUIPlugin.getResource("%serverEditorOverviewServerConfigurationNameCommand");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.setName(oldName);
	}
}