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
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ProjectProperties;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerType;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * 
 */
public class WizardTaskUtil {
	private WizardTaskUtil() {
		// do nothing
	}
	
	public static void saveRuntime(TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
		IRuntime runtime = (IRuntime) taskModel.getObject(TaskModel.TASK_RUNTIME);
		if (runtime != null && runtime instanceof IRuntimeWorkingCopy) {
			IRuntimeWorkingCopy workingCopy = (IRuntimeWorkingCopy) runtime;
			if (workingCopy.isDirty())
				taskModel.putObject(TaskModel.TASK_RUNTIME, workingCopy.save(false, monitor));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#doTask()
	 */
	public static void saveServer(TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
		IServer server = (IServer) taskModel.getObject(TaskModel.TASK_SERVER);
		if (server != null && server instanceof IServerWorkingCopy) {
			IServerWorkingCopy workingCopy = (IServerWorkingCopy) server;
			if (workingCopy.isDirty()) {
				IFile file = ((Server)workingCopy).getFile();
				if (file != null) {
					IProject project = file.getProject();
					
					if (!file.getProject().exists())
						EclipseUtil.createNewServerProject(null, project.getName(), null, monitor);
					
					ProjectProperties pp = (ProjectProperties) ServerCore.getProjectProperties(project);
					if (!pp.isServerProject())
						pp.setServerProject(true, monitor);
				}
				taskModel.putObject(TaskModel.TASK_SERVER, workingCopy.save(false, monitor));
			}
		}
	}
	
	public static void tempSaveRuntime(TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
		IRuntime runtime = (IRuntime) taskModel.getObject(TaskModel.TASK_RUNTIME);
		if (runtime != null && runtime instanceof IRuntimeWorkingCopy) {
			IRuntimeWorkingCopy workingCopy = (IRuntimeWorkingCopy) runtime;
			if (!workingCopy.isDirty())
				return;
		
			runtime = workingCopy.save(false, monitor);
			taskModel.putObject(TaskModel.TASK_RUNTIME, runtime.createWorkingCopy());
		}
	}
	
	public static void tempSaveServer(TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
		IServer server = (IServer) taskModel.getObject(TaskModel.TASK_SERVER);
		if (server != null && server instanceof IServerWorkingCopy) {
			IServerWorkingCopy workingCopy = (IServerWorkingCopy) server;
			if (!workingCopy.isDirty())
				return;
			
			IFile file = ((Server)workingCopy).getFile();
			if (file != null) {
				IProject project = file.getProject();
				
				if (!file.getProject().exists())
					EclipseUtil.createNewServerProject(null, project.getName(), null, monitor);
				
				ProjectProperties pp = (ProjectProperties) ServerCore.getProjectProperties(project);
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
			taskModel.putObject(TaskModel.TASK_SERVER, workingCopy);
		}
	}
	
	public static void addModule(IModule module, TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
		if (module == null)
			return;

		IServer server = (IServer) taskModel.getObject(TaskModel.TASK_SERVER);
		IModule parentModule = null;
		try {
			IModule[] parents = server.getRootModules(module, monitor);
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

		IModule[] modules = server.getModules();
		int size = modules.length;
		for (int i = 0; i < size; i++) {
			if (parentModule.equals(modules[i]))
				return;
		}

		IServerWorkingCopy workingCopy = server.createWorkingCopy();
		workingCopy.modifyModules(new IModule[] { parentModule }, new IModule[0], monitor);
		taskModel.putObject(TaskModel.TASK_SERVER, workingCopy.save(false, monitor));
	}
	
	public static void modifyModules(List add, List remove, TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
		if ((add == null || add.isEmpty()) && (remove == null || remove.isEmpty()))
			return;

		IServerWorkingCopy workingCopy = (IServerWorkingCopy) taskModel.getObject(TaskModel.TASK_SERVER);
		
		boolean sbp = ((ServerType) workingCopy.getServerType()).startBeforePublish();
		if (sbp) {
			IServer server = workingCopy.getOriginal();
			int state = server.getServerState();
			if (state == IServer.STATE_STOPPED || state == IServer.STATE_UNKNOWN) {
				String mode = (String) taskModel.getObject(TaskModel.TASK_LAUNCH_MODE);
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
		
		IFile file = ((Server)workingCopy).getFile();
		if (file != null) {
			IProject project = file.getProject();
			
			if (!file.getProject().exists())
				EclipseUtil.createNewServerProject(null, project.getName(), null, monitor);
			
			ProjectProperties pp = (ProjectProperties) ServerCore.getProjectProperties(project);
			if (!pp.isServerProject())
				pp.setServerProject(true, monitor);
		}
		
		workingCopy.modifyModules(add2, remove2, monitor);
	}
}