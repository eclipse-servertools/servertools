/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	protected String id;
	protected int port;
	protected int oldPort;

	/**
	 * ModifyPortCommand constructor.
	 * 
	 * @param server a server
	 * @param id a port id
	 * @param port new port number
	 */
	public ModifyPortCommand(HttpServer server, String id, int port) {
		super(server, Messages.configurationEditorActionModifyPort);
		this.id = id;
		this.port = port;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		// find old port number
		ServerPort temp = server.getServerPorts()[0];
		if (id.equals(temp.getId()))
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