/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.util.Task;
/**
 * 
 */
public class SaveServerConfigurationTask extends Task {
	public SaveServerConfigurationTask() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IServerConfiguration sc = (IServerConfiguration) getTaskModel().getObject(ITaskModel.TASK_SERVER_CONFIGURATION);
		if (sc != null && sc instanceof IServerConfigurationWorkingCopy) {
			IServerConfigurationWorkingCopy workingCopy = (IServerConfigurationWorkingCopy) sc;
			if (workingCopy.isDirty()) {
				IFile file = workingCopy.getFile();
				if (file != null && !file.getProject().exists()) {
					IProject project = file.getProject();
					ServerCore.createServerProject(project.getName(), null, monitor);
				}
				getTaskModel().putObject(ITaskModel.TASK_SERVER_CONFIGURATION, workingCopy.save(false, monitor));
			}
		}
	}
}