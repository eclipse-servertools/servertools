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
package org.eclipse.wst.server.http.core.internal.command;

import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.http.core.internal.HttpServer;
import org.eclipse.wst.server.http.core.internal.Messages;
/**
 * Command to change the port.
 */
public class ModifyPortCommand extends ServerCommand {
	protected int port;
	protected int oldPort;

	/**
	 * ModifyPortCommand constructor.
	 * 
	 * @param server a server
	 * @param port new port number
	 */
	public ModifyPortCommand(HttpServer server, int port) {
		super(server, Messages.actionModifyPort);
		this.port = port;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		// find old port number
		ServerPort temp = server.getServerPorts()[0];
		oldPort = temp.getPort();
		
		// make the change
		server.setPort(port);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setPort(oldPort);
	}
}