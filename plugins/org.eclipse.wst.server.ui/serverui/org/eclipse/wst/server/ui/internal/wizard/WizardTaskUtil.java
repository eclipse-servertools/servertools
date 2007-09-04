/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ProjectProperties;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class WizardTaskUtil {
	public static final String TASK_LAUNCHABLE_ADAPTER = "launchableAdapter";
	public static final String TASK_LAUNCHABLE = "launchable";
	public static final String TASK_CLIENT = "client";
	public static final String TASK_CLIENTS = "clients";
	public static final String TASK_DEFAULT_SERVER = "defaultServer";
	public static final String TASK_MODE = "mode";
	public static final String TASK_HAS_TASKS = "hasTasks";
	public static final String TASK_HAS_CLIENTS = "hasClients";
	public static final String TASK_FEATURE = "feature";

	public static final byte MODE_EXISTING = 0;
	public static final byte MODE_DETECT = 1;
	public static final byte MODE_MANUAL = 2;

	public static final WizardFragment SaveRuntimeFragment = new WizardFragment() {
		public void performFinish(IProgressMonitor monitor) throws CoreException {
			WizardTaskUtil.saveRuntime(getTaskModel(), monitor);
		}
	};

	public static final WizardFragment SaveServerFragment = new WizardFragment() {
		public void performFinish(IProgressMonitor monitor) throws CoreException {
			WizardTaskUtil.saveServer(getTaskModel(), monitor);
		}
	};

	public static final WizardFragment TempSaveRuntimeFragment = new WizardFragment() {
		public void performFinish(IProgressMonitor monitor) throws CoreException {
			WizardTaskUtil.tempSaveRuntime(getTaskModel(), monitor);
		}
	};

	public static final WizardFragment TempSaveServerFragment = new WizardFragment() {
		public void performFinish(IProgressMonitor monitor) throws CoreException {
			WizardTaskUtil.tempSaveServer(getTaskModel(), monitor);
		}
	};

	public static final WizardFragment SaveHostnameFragment = new WizardFragment() {
		public void performFinish(IProgressMonitor monitor) throws CoreException {
			try {
				IServerAttributes server2 = (IServerAttributes) getTaskModel().getObject(TaskModel.TASK_SERVER);
				ServerUIPlugin.getPreferences().addHostname(server2.getHost());
			} catch (Exception e) {
				// ignore
			}
		}
	};

	private WizardTaskUtil() {
		// do nothing
	}

	protected static void saveRuntime(TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
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
	protected static void saveServer(TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
		IServer server = (IServer) taskModel.getObject(TaskModel.TASK_SERVER);
		if (server != null && server instanceof IServerWorkingCopy) {
			IServerWorkingCopy workingCopy = (IServerWorkingCopy) server;
			if (workingCopy.isDirty()) {
				IFile file = ((Server)workingCopy).getFile();
				if (file != null) {
					IProject project = file.getProject();
					
					if (!file.getProject().exists())
						EclipseUtil.createNewServerProject(null, project.getName(), null, monitor);
					
					ProjectProperties pp = ServerPlugin.getProjectProperties(project);
					if (!pp.isServerProject())
						pp.setServerProject(true, monitor);
				}
				taskModel.putObject(TaskModel.TASK_SERVER, workingCopy.save(false, monitor));
			}
		}
	}

	protected static void tempSaveRuntime(TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
		IRuntime runtime = (IRuntime) taskModel.getObject(TaskModel.TASK_RUNTIME);
		if (runtime != null && runtime instanceof IRuntimeWorkingCopy) {
			IRuntimeWorkingCopy workingCopy = (IRuntimeWorkingCopy) runtime;
			if (!workingCopy.isDirty())
				return;
			
			runtime = workingCopy.save(false, monitor);
			taskModel.putObject(TaskModel.TASK_RUNTIME, runtime.createWorkingCopy());
		}
	}

	protected static void tempSaveServer(TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
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
				
				ProjectProperties pp = ServerPlugin.getProjectProperties(project);
				if (!pp.isServerProject())
					pp.setServerProject(true, monitor);
			}
			IRuntime runtime = workingCopy.getRuntime();
			
			server = workingCopy.save(false, monitor);
			workingCopy = server.createWorkingCopy();
			
			workingCopy.setRuntime(runtime);
			if (workingCopy.getServerType().hasServerConfiguration())
				((ServerWorkingCopy)workingCopy).importRuntimeConfiguration(runtime, null);
			
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
			if (parents != null && parents.length > 0)
				parentModule = parents[0];
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not find parent module", e);
		}
		
		if (parentModule == null) {
			// use the original module since no parent module is available
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

	public static void modifyModules(List<IModule> add, List<IModule> remove, TaskModel taskModel, IProgressMonitor monitor) throws CoreException {
		if ((add == null || add.isEmpty()) && (remove == null || remove.isEmpty()))
			return;
		
		IServerWorkingCopy workingCopy = (IServerWorkingCopy) taskModel.getObject(TaskModel.TASK_SERVER);
		
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
			
			ProjectProperties pp = ServerPlugin.getProjectProperties(project);
			if (!pp.isServerProject())
				pp.setServerProject(true, monitor);
		}
		
		workingCopy.modifyModules(add2, remove2, monitor);
	}
}