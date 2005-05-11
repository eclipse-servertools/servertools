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

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * A command on a server.
 */
public abstract class ServerCommand extends AbstractOperation {
	protected IServerWorkingCopy server;

	/**
	 * ServerCommand constructor.
	 * 
	 * @param server a server
	 * @param name a label
	 */
	public ServerCommand(IServerWorkingCopy server, String name) {
		super(name);
		this.server = server;
	}

	public abstract void execute();

	public IStatus execute(IProgressMonitor monitor, IAdaptable adapt) {
		execute();
		return null;
	}

	public abstract void undo();

	public IStatus undo(IProgressMonitor monitor, IAdaptable adapt) {
		undo();
		return null;
	}
	
	public IStatus redo(IProgressMonitor monitor, IAdaptable adapt) {
		return execute(monitor, adapt);
	}
}