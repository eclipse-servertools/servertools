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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IProjectProperties;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IRunningActionServer;
import org.eclipse.wst.server.core.util.Task;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
/**
 * 
 */
public class ModifyModulesTask extends Task {
	protected List add;
	protected List remove;
	
	public ModifyModulesTask() {
		// do nothing
	}
	
	public void setAddModules(List add) {
		this.add = add;
	}
	
	public void setRemoveModules(List remove) {
		this.remove = remove;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public void execute(IProgressMonitor monitor) throws CoreException {
		if ((add == null || add.isEmpty()) && (remove == null || remove.isEmpty()))
			return;

		IServerWorkingCopy workingCopy = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
		
		IRunningActionServer ras = (IRunningActionServer) workingCopy.getAdapter(IRunningActionServer.class);
		if (ras != null) {
			IServer server = workingCopy.getOriginal();
			int state = server.getServerState();
			if (state == IServer.STATE_STOPPED || state == IServer.STATE_UNKNOWN) {
				String mode = (String) getTaskModel().getObject(ITaskModel.TASK_LAUNCH_MODE);
				if (mode == null || mode.length() == 0)
					mode = ILaunchManager.DEBUG_MODE;
				
				server.synchronousStart(mode, monitor);
			}
		}

		// modify modules
		IModule[] remove2 = new IModule[0];
		if (remove != null) {
			remove2 = new IModule[remove.size()];
			remove.toArray(remove2);
		}
		
		IModule[] add2 = new IModule[0];
		if (add != null) {
			add2 = new IModule[add.size()];
			add.toArray(add2);
		}
		
		IFile file = workingCopy.getFile();
		if (file != null) {
			IProject project = file.getProject();
			
			if (!file.getProject().exists())
				EclipseUtil.createNewServerProject(null, project.getName(), null, monitor);
			
			IProjectProperties pp = ServerCore.getProjectProperties(project);
			if (!pp.isServerProject())
				pp.setServerProject(true, monitor);
		}
		
		workingCopy.modifyModules(add2, remove2, monitor);
	}
}