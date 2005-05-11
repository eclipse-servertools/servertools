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
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
/**
 * Command to change the server debug mode.
 */
public class SetDebugModeCommand extends ServerCommand {
	protected boolean debug;
	protected boolean oldDebug;

	/**
	 * SetDebugModeCommand constructor comment.
	 * 
	 * @param server a Tomcat server
	 * @param debug <code>true</code> for debug mode
	 */
	public SetDebugModeCommand(ITomcatServerWorkingCopy server, boolean debug) {
		super(server, Messages.serverEditorActionSetDebugMode);
		this.debug = debug;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldDebug = server.isDebug();
		server.setDebug(debug);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setDebug(oldDebug);
	}
}