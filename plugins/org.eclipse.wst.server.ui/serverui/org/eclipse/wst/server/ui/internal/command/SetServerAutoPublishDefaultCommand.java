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
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Command to change the server's auto-publish setting.
 */
public class SetServerAutoPublishDefaultCommand extends ServerCommand {
	protected int setting;
	protected int oldSetting;

	/**
	 * SetServerAutoPublishDefaultCommand constructor.
	 * 
	 * @param server a server
	 * @param setting the auto-publish setting
	 */
	public SetServerAutoPublishDefaultCommand(IServerWorkingCopy server, int setting) {
		super(server, Messages.serverEditorOverviewAutoPublishCommand);
		this.setting = setting;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		oldSetting = swc.getAutoPublishSetting();
		swc.setAutoPublishSetting(setting);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		swc.setAutoPublishSetting(oldSetting);
	}
}