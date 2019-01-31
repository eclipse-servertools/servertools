/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
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

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Command to change the server's auto-publish setting.
 */
public class SetServerAutoPublishTimeCommand extends ServerCommand {
	protected int time;
	protected int oldTime;

	/**
	 * SetServerAutoPublishDefaultCommand constructor.
	 * 
	 * @param server a server
	 * @param time a publish time
	 */
	public SetServerAutoPublishTimeCommand(IServerWorkingCopy server, int time) {
		super(server, Messages.serverEditorOverviewPublishCommand);
		this.time = time;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		oldTime = swc.getAutoPublishTime();
		swc.setAutoPublishTime(time);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		swc.setAutoPublishTime(oldTime);
	}
}