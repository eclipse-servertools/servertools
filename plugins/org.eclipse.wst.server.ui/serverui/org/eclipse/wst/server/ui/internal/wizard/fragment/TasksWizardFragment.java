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
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.util.Task;
import org.eclipse.wst.server.ui.editor.IOrdered;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ProgressUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorCore;
import org.eclipse.wst.server.ui.internal.wizard.page.TasksComposite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class TasksWizardFragment extends WizardFragment {
	public class TaskInfo implements IOrdered {
		public int status;
		public String id;
		public IOptionalTask task2;
		
		private static final String DEFAULT = "default:";
		
		public boolean isSelected() {
			try {
				Boolean b = (Boolean) selectedTaskMap.get(getId());
				return b.booleanValue();
			} catch (Exception e) {
				// ignore
			}
			
			try {
				Boolean b = (Boolean) selectedTaskMap.get(DEFAULT + getId());
				return b.booleanValue();
			} catch (Exception e) {
				// ignore
			}
			return false;
		}
		
		public void setDefaultSelected(boolean sel) {
			selectedTaskMap.put(DEFAULT + getId(), new Boolean(sel));
		}
		
		public void setSelected(boolean sel) {
			selectedTaskMap.put(getId(), new Boolean(sel));
		}
		
		public int getOrder() {
			return task2.getOrder();
		}
		
		protected String getId() {
			return id;
		}
	}

	protected TasksComposite comp;

	protected List tasks;
	protected boolean hasOptionalTasks;
	
	protected Map selectedTaskMap = new HashMap();
	
	public TasksWizardFragment() {
		// do nothing
	}
	
	public void enter() {
		updateTasks();
	}
	
	public List getChildFragments() {
		updateTasks();
		return super.getChildFragments();
	}

	public void setTaskModel(ITaskModel taskModel) {
		super.setTaskModel(taskModel);
		updateTasks();
	}

	public void updateTasks() {
		tasks = null;
		if (getTaskModel() == null)
			return;

		IServer server = (IServer) getTaskModel().getObject(ITaskModel.TASK_SERVER);
		
		List[] parents = (List[]) getTaskModel().getObject(ITaskModel.TASK_MODULE_PARENTS);
		IModule[] modules = (IModule[]) getTaskModel().getObject(ITaskModel.TASK_MODULES);
		
		if (server != null && (parents == null || modules == null)) {
			class Helper {
				List parentList = new ArrayList();
				List moduleList = new ArrayList();
			}
			final Helper help = new Helper();
			ServerUtil.visit(server, new IModuleVisitor() {
				public boolean visit(IModule[] parents2, IModule module2) {
					int size = parents2.length;
					List list = new ArrayList(size);
					for (int i = 0; i < size; i++)
						list.add(parents2[i]);
					
					help.parentList.add(list);
					help.moduleList.add(module2);
					return true;
				}
			}, null);

			int size = help.parentList.size();
			parents = new List[size];
			help.parentList.toArray(parents);
			modules = new IModule[size];
			help.moduleList.toArray(modules);
		}
		
		if (server != null && parents != null || modules != null) {
			tasks = new ArrayList();
			createTasks(server, parents, modules);
		}
		
		if (comp != null)
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					comp.setTasks(tasks);
				}
			});
	}
	
	protected void createTasks(IServer server, List[] parents, IModule[] modules) {
		String serverTypeId = null;
		if (server != null)
			serverTypeId = server.getServerType().getId();
		
		// server tasks
		IServerTask[] serverTasks = ServerCore.getServerTasks();
		if (serverTasks != null) {
			int size = serverTasks.length;
			for (int i = 0; i < size; i++) {
				IServerTask task = serverTasks[i];
				if (serverTypeId != null && task.supportsType(serverTypeId)) {
					IOptionalTask[] tasks2 = task.getTasks(server, parents, modules);
					if (tasks2 != null) {
						int size2 = tasks2.length;
						for (int j = 0; j < size2; j++) {
							int status = tasks2[j].getStatus(); 
							if (status != IOptionalTask.TASK_UNNECESSARY) {
								if (status == IOptionalTask.TASK_READY || status == IOptionalTask.TASK_PREFERRED)
									hasOptionalTasks = true;
								tasks2[i].setTaskModel(getTaskModel());
								addServerTask(server, tasks2[j]);
							}
						}
					}
				}
			}
		}
	}

	public void addServerTask(IServer server, IOptionalTask task2) {
		TaskInfo sti = new TaskInfo();
		sti.task2 = task2;
		sti.status = task2.getStatus();
		String id = server.getId();
		sti.id = id + "|" + task2.getName();
		if (sti.status == IOptionalTask.TASK_PREFERRED || sti.status == IOptionalTask.TASK_MANDATORY)
			sti.setDefaultSelected(true);
		
		tasks.add(sti);
	}

	public boolean hasComposite() {
		return hasTasks();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new TasksComposite(parent, wizard);
		return comp;
	}

	public ITask createFinishTask() {
		return new Task() {
			public void execute(IProgressMonitor monitor) throws CoreException {
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
	protected void performFinish(IProgressMonitor monitor) throws CoreException {
		List performTasks = new ArrayList();

		if (tasks == null)
			return;
		int size = tasks.size();
		for (int i = 0; i < size; i++) {
			TaskInfo ti = (TaskInfo) tasks.get(i);
			if (ti.isSelected())
				performTasks.add(tasks.get(i));
		}
		
		Trace.trace(Trace.FINEST, "Performing wizard tasks: " + performTasks.size());
		
		if (performTasks.size() == 0)
			return;
		
		// get most recent server/configuration
		boolean createdServerWC = false;
		ITaskModel taskModel = getTaskModel();
		IServer server = (IServer) taskModel.getObject(ITaskModel.TASK_SERVER);
		if (server == null)
			return;

		// get working copies
		IServerWorkingCopy serverWC = null;
		if (server instanceof IServerWorkingCopy)
			serverWC = (IServerWorkingCopy) server;
		else {
			serverWC = server.createWorkingCopy();
			createdServerWC = true;
		}
		
		taskModel.putObject(ITaskModel.TASK_SERVER, serverWC);
		
		// begin task
		monitor.beginTask(ServerUIPlugin.getResource("%performingTasks"), performTasks.size() * 1000);
		
		ServerEditorCore.sortOrderedList(performTasks);

		Iterator iterator = performTasks.iterator();
		while (iterator.hasNext()) {
			IProgressMonitor subMonitor = ProgressUtil.getSubMonitorFor(monitor, 1000);
			Object obj = iterator.next();
			if (obj instanceof TasksWizardFragment.TaskInfo) {
				TasksWizardFragment.TaskInfo sti = (TasksWizardFragment.TaskInfo) obj;
				try {
					Trace.trace(Trace.FINER, "Executing task: " + sti.task2.getName());
					sti.task2.setTaskModel(taskModel);
					sti.task2.execute(subMonitor);
				} catch (final CoreException ce) {
					Trace.trace(Trace.SEVERE, "Error executing task " + sti.task2.getName(), ce);
					throw ce;
				}
			}
			subMonitor.done();
		}
		
		if (createdServerWC) {
			if (serverWC.isDirty()) {
				IFile file = serverWC.getFile();
				if (file != null) {
					IProject project = file.getProject();
					
					if (!file.getProject().exists())
						EclipseUtil.createNewServerProject(null, project.getName(), null, monitor);
					
					IProjectProperties pp = ServerCore.getProjectProperties(project);
					if (!pp.isServerProject())
						pp.setServerProject(true, monitor);
				}
				taskModel.putObject(ITaskModel.TASK_SERVER, serverWC.save(false, monitor));
			} else
				taskModel.putObject(ITaskModel.TASK_SERVER, serverWC.getOriginal());
		}
				
		monitor.done();
	}

	/**
	 * 
	 */
	public boolean hasTasks() {
		return tasks == null || !tasks.isEmpty();
	}
	
	public boolean hasOptionalTasks() {
		return hasOptionalTasks;
	}
}