/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.util.Task;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * 
 */
public class AddModuleTask extends Task {
	protected IModule module;

	public AddModuleTask(IModule module) {
		this.module = module;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		if (module == null)
			return;

		IServer server = (IServer) getTaskModel().getObject(ITaskModel.TASK_SERVER);
		IModule parentModule = null;
		try {
			IModule[] parents = server.getParentModules(module, monitor);
			if (parents != null && parents.length > 0) {
				parentModule = parents[0];
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not find parent module", e);
		}
		
		if (parentModule == null) {
			// Use the original module since no parent module is available.
			parentModule = module;
		}

		IModule[] modules = server.getModules(monitor);
		int size = modules.length;
		for (int i = 0; i < size; i++) {
			if (parentModule.equals(modules[i]))
				return;
		}

		IServerWorkingCopy workingCopy = server.createWorkingCopy();
		workingCopy.modifyModules(new IModule[] { parentModule }, new IModule[0], monitor);
		getTaskModel().putObject(ITaskModel.TASK_SERVER, workingCopy.save(false, monitor));
	}
}