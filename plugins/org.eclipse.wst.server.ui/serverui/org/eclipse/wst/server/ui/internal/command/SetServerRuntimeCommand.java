/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Command to change the server runtime.
 */
public class SetServerRuntimeCommand extends ServerCommand {
	protected IRuntime runtime;
	protected IRuntime oldRuntime;

	/**
	 * SetServerRuntimeCommand constructor.
	 * 
	 * @param server a server
	 * @param runtime a server runtime
	 */
	public SetServerRuntimeCommand(IServerWorkingCopy server, IRuntime runtime) {
		super(server, Messages.serverEditorOverviewRuntimeCommand);
		this.runtime = runtime;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		oldRuntime = server.getRuntime();
		server.setRuntime(runtime);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		server.setRuntime(oldRuntime);
	}
}