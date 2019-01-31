/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.jst.server.tomcat.core.internal.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
/**
 * Command to change the server test mode.  The server instance directory
 * is cleared in conjunction with this command for legacy support.
 */
public class SetTestEnvironmentCommand extends ServerCommand {
	protected boolean te;
	protected boolean oldTe;
	protected String oldInstanceDir;

	/**
	 * SetTestEnvironmentCommand constructor comment.
	 * 
	 * @param server a Tomcat server
	 * @param te <code>true</code> for a test environment.
	 */
	public SetTestEnvironmentCommand(ITomcatServerWorkingCopy server, boolean te) {
		super(server, Messages.serverEditorActionSetServerDirectory);
		this.te = te;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldTe = server.isTestEnvironment();
		// save old instance directory
		oldInstanceDir = server.getInstanceDirectory();
		server.setTestEnvironment(te);
		// ensure instance directory is cleared
		server.setInstanceDirectory(null);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setTestEnvironment(oldTe);
		server.setInstanceDirectory(oldInstanceDir);
	}
}
