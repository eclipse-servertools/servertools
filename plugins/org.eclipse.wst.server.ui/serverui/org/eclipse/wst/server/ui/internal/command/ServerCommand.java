/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
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

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
		return Status.OK_STATUS;
	}

	public abstract void undo();

	public IStatus undo(IProgressMonitor monitor, IAdaptable adapt) {
		undo();
		return Status.OK_STATUS;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable adapt) {
		return execute(monitor, adapt);
	}
}