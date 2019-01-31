/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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
 * Command to change the server's stop timeout setting.
 */
public class SetServerStopTimeoutCommand extends ServerCommand {
	protected int time;
	protected int oldTime;

	/**
	 * SetServerStopTimeoutCommand constructor.
	 * 
	 * @param server a server
	 * @param time a publish time
	 */
	public SetServerStopTimeoutCommand(IServerWorkingCopy server, int time) {
		super(server, Messages.serverEditorOverviewTimeoutCommand);
		this.time = time;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		oldTime = swc.getStopTimeout();
		swc.setStopTimeout(time);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		ServerWorkingCopy swc = (ServerWorkingCopy) server;
		swc.setStopTimeout(oldTime);
	}
}
