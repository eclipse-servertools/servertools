/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.util.Task;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
/**
 * 
 */
public class TempSaveServerTask extends Task {
	public TempSaveServerTask() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		IServer server = (IServer) getTaskModel().getObject(ITaskModel.TASK_SERVER);
		if (server != null && server instanceof IServerWorkingCopy) {
			IServerWorkingCopy workingCopy = (IServerWorkingCopy) server;
			if (!workingCopy.isDirty())
				return;
			
			IFile file = workingCopy.getFile();
			if (file != null) {
				IProject project = file.getProject();
				
				if (!file.getProject().exists())
					EclipseUtil.createNewServerProject(null, project.getName(), null, monitor);
				
				IProjectProperties pp = ServerCore.getProjectProperties(project);
				if (!pp.isServerProject())
					pp.setServerProject(true, monitor);
			}
			IRuntime runtime = workingCopy.getRuntime();
			
			server = workingCopy.save(false, monitor);
			workingCopy = server.createWorkingCopy();
			
			workingCopy.setRuntime(runtime);
			if (workingCopy.getServerType().hasServerConfiguration()) {
				((Server)workingCopy).importConfiguration(runtime, null);
			}
			getTaskModel().putObject(ITaskModel.TASK_SERVER, workingCopy);
		}
	}
}