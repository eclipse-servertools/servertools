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
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.wst.server.core.IModuleTask;
import org.eclipse.wst.server.core.IOrdered;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.IServerTask;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IModuleTaskDelegate;
import org.eclipse.wst.server.core.model.IServerTaskDelegate;
import org.eclipse.wst.server.core.util.ProgressUtil;
import org.eclipse.wst.server.core.util.Task;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.wizard.page.TasksComposite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 */
public class TasksWizardFragment extends WizardFragment {
	public abstract class TaskInfo implements IOrdered {
		public IServer server;
		public IServerConfiguration configuration;
		public boolean selected;
		public byte status;
	}

	public class ServerTaskInfo extends TaskInfo {
		public IServerTask task2;
		public List[] parents;
		public IModule[] modules;
		
		public int getOrder() {
			return task2.getOrder();
		}
	}
	
	public class ModuleTaskInfo extends TaskInfo {
		public IModuleTask task2;
		public List parents;
		public IModule module;
		
		public int getOrder() {
			return task2.getOrder();
		}
	}

	protected TasksComposite comp;

	protected List tasks = new ArrayList();
	protected boolean hasOptionalTasks;
	
	public TasksWizardFragment(IServer server, IServerConfiguration configuration, List[] parents, IModule[] modules) {
		createTasks(server, configuration, parents, modules);
	}
	
	protected void createTasks(IServer server, IServerConfiguration configuration, List[] parents, IModule[] modules) {
		// server tasks
		Iterator iterator = ServerCore.getServerTasks().iterator();
		while (iterator.hasNext()) {
			IServerTask task = (IServerTask) iterator.next();
			task.init(server, configuration, parents, modules);
			byte status = task.getTaskStatus();
			if (status != IServerTaskDelegate.TASK_UNNECESSARY) {
				if (status == IServerTaskDelegate.TASK_READY || status == IServerTaskDelegate.TASK_PREFERRED)
					hasOptionalTasks = true;
				addServerTask(server, configuration, parents, modules, task);
			}
		}
		
		// module tasks
		int size = modules.length;
		for (int i = 0; i < size; i++) {
			iterator = ServerCore.getModuleTasks().iterator();
			while (iterator.hasNext()) {
				IModuleTask task = (IModuleTask) iterator.next();
				task.init(server, configuration, parents[i], modules[i]);
				byte status = task.getTaskStatus();
				if (status != IServerTaskDelegate.TASK_UNNECESSARY) {
					if (status == IServerTaskDelegate.TASK_READY || status == IServerTaskDelegate.TASK_PREFERRED)
						hasOptionalTasks = true;
					addModuleTask(server, configuration, parents[i], modules[i], task);
				}
			}
		}
	}
	
	public void addServerTask(IServer server, IServerConfiguration configuration, List[] parents, IModule[] modules, IServerTask task2) {
		ServerTaskInfo sti = new ServerTaskInfo();
		sti.server = server;
		sti.configuration = configuration;
		sti.parents = parents;
		sti.modules = modules;
		sti.task2 = task2;
		sti.status = task2.getTaskStatus();
		if (sti.status == IModuleTaskDelegate.TASK_PREFERRED || sti.status == IModuleTaskDelegate.TASK_MANDATORY)
			sti.selected = true;
		
		tasks.add(sti);
	}
	
	public void addModuleTask(IServer server, IServerConfiguration configuration, List parents, IModule module, IModuleTask task2) {
		ModuleTaskInfo dti = new ModuleTaskInfo();
		dti.server = server;
		dti.configuration = configuration;
		dti.parents = parents;
		dti.module = module;
		dti.task2 = task2;
		dti.status = task2.getTaskStatus();
		if (dti.status == IModuleTaskDelegate.TASK_PREFERRED || dti.status == IModuleTaskDelegate.TASK_MANDATORY)
			dti.selected = true;
		tasks.add(dti);
	}
	
	public boolean hasComposite() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new TasksComposite(parent, wizard, tasks);
		return comp;
	}

	public ITask createFinishTask() {
		return new Task() {
			public void execute(IProgressMonitor monitor) {
				performFinish(monitor);
			}
		};
	}
	
	/**
	 * Returns the number of tasks run, or -1 if there was an error.
	 *
	 * @param monitor
	 * @return int
	 */
	public void performFinish(IProgressMonitor monitor) {
		List performTasks = new ArrayList();

		int size = tasks.size();
		int count = 0;
		for (int i = 0; i < size; i++) {
			TaskInfo ti = (TaskInfo) tasks.get(i);
			if (ti.selected)
				performTasks.add(tasks.get(i));
		}
		
		monitor.beginTask(ServerUIPlugin.getResource("%performingTasks"), count * 1000);
		
		ServerUtil.sortOrderedList(performTasks);

		Iterator iterator = performTasks.iterator();
		while (iterator.hasNext()) {
			count++;
			IProgressMonitor subMonitor = ProgressUtil.getSubMonitorFor(monitor, 1000);
			Object obj = iterator.next();
			if (obj instanceof TasksWizardFragment.ServerTaskInfo) {
				TasksWizardFragment.ServerTaskInfo sti = (TasksWizardFragment.ServerTaskInfo) obj;
				try {
					Trace.trace(Trace.FINER, "Executing task: " + sti.task2.getId());
					sti.task2.execute(subMonitor);
				} catch (final CoreException ce) {
					Trace.trace(Trace.SEVERE, "Error executing task " + sti.task2.getId(), ce);
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							Shell shell = Display.getDefault().getActiveShell();
							MessageDialog.openError(shell, ServerUIPlugin.getResource("%defaultDialogTitle"), ce.getMessage());
						}
					});
					return;
				}
			} else if (obj instanceof ModuleTaskInfo) {
				ModuleTaskInfo dti = (ModuleTaskInfo) obj;
				try {
					Trace.trace(Trace.FINER, "Executing task: " + dti.task2.getId());
					dti.task2.execute(subMonitor);
				} catch (final CoreException ce) {
					Trace.trace(Trace.SEVERE, "Error executing task " + dti.task2.getId(), ce);
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							Shell shell = Display.getDefault().getActiveShell();
							MessageDialog.openError(shell, ServerUIPlugin.getResource("%defaultDialogTitle"), ce.getMessage());
						}
					});
					return;
				}
			}
			subMonitor.done();
		}
		
		monitor.done();
	}

	/**
	 * 
	 */
	public boolean hasTasks() {
		return !tasks.isEmpty();
	}
	
	public boolean hasOptionalTasks() {
		return hasOptionalTasks;
	}
}
