/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * Command to remove a web module from a server.
 */
public class RemoveModuleCommand extends AbstractOperation {
	protected IServerWorkingCopy server;
	protected IModule module;

	/**
	 * AddModuleCommand constructor comment.
	 * 
	 * @param server a server
	 * @param module a web module
	 */
	public RemoveModuleCommand(IServerWorkingCopy server, IModule module) {
		super(Messages.configurationEditorActionRemoveWebModule);
		this.server = server;
		this.module = module;
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			server.modifyModules(null, new IModule[] { module }, monitor);
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
			server.modifyModules(new IModule[] { module }, null, monitor);
		} catch (Exception e) {
			// ignore
		}
		return Status.OK_STATUS;
	}
}
