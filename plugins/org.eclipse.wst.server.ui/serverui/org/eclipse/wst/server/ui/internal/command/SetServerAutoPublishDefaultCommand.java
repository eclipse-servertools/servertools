/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.command;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Command to change the server's auto-publish setting.
 */
public class SetServerAutoPublishDefaultCommand extends ServerCommand {
	protected boolean time;
	protected boolean oldTime;

	/**
	 * SetServerAutoPublishDefaultCommand constructor comment.
	 */
	public SetServerAutoPublishDefaultCommand(IServerWorkingCopy server, boolean time) {
		super(server);
		this.time = time;
	}

	/**
	 * Execute the command.
	 * @return boolean
	 */
	public boolean execute() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		oldTime = swc.getAutoPublishDefault();
		swc.setAutoPublishDefault(time);
		return true;
	}

	/**
	 * Returns this command's description.
	 * @return java.lang.String
	 */
	public String getDescription() {
		return ServerUIPlugin.getResource("%serverEditorOverviewAutoPublishDescription");
	}

	/**
	 * Returns this command's label.
	 * @return java.lang.String
	 */
	public String getName() {
		return ServerUIPlugin.getResource("%serverEditorOverviewAutoPublishCommand");
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		swc.setAutoPublishDefault(oldTime);
	}
}