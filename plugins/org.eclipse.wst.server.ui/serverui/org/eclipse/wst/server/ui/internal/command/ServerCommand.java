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
package org.eclipse.wst.server.ui.internal.command;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.util.Task;
/**
 * A command on a server.
 */
public abstract class ServerCommand extends Task {
	protected IServerWorkingCopy server;

	/**
	 * ServerCommand constructor.
	 * 
	 * @param server a server
	 */
	public ServerCommand(IServerWorkingCopy server) {
		super();
		this.server = server;
	}

	/**
	 * Returns true if this command can be undone.
	 * @return boolean
	 */
	public boolean canUndo() {
		return true;
	}
	
	public abstract boolean execute();
	
	public void execute(IProgressMonitor monitor) {
		execute();
	}
}