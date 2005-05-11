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
	protected boolean time;
	protected boolean oldTime;

	/**
	 * SetServerAutoPublishDefaultCommand constructor.
	 * 
	 * @param server a server
	 * @param time <code>true</code> to use the default time
	 */
	public SetServerAutoPublishDefaultCommand(IServerWorkingCopy server, boolean time) {
		super(server, Messages.serverEditorOverviewAutoPublishCommand);
		this.time = time;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		oldTime = swc.getAutoPublishDefault();
		swc.setAutoPublishDefault(time);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		swc.setAutoPublishDefault(oldTime);
	}
}