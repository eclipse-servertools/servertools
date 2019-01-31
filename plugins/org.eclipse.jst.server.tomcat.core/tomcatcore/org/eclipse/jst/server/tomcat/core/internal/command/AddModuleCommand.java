/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * Command to add a web module to a server.
 */
public class AddModuleCommand extends AbstractOperation {
	protected IServerWorkingCopy server;
	protected IModule module;
	protected int modules = -1;

	/**
	 * AddModuleCommand constructor comment.
	 * 
	 * @param server a server
	 * @param module a web module
	 */
	public AddModuleCommand(IServerWorkingCopy server, IModule module) {
		super(Messages.configurationEditorActionAddWebModule);
		this.server = server;
		this.module = module;
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			server.modifyModules(new IModule[] { module }, null, monitor);
		} catch (Exception e) {
			// ignore
		}
		return Status.OK_STATUS;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			server.modifyModules(null, new IModule[] { module }, monitor);
		} catch (Exception e) {
			// ignore
		}
		return Status.OK_STATUS;
	}
}
