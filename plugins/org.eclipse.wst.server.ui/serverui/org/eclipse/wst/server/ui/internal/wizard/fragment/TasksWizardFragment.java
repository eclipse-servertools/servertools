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
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ProgressUtil;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.editor.IOrdered;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorCore;
import org.eclipse.wst.server.ui.internal.wizard.page.TasksComposite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class TasksWizardFragment extends WizardFragment {
	public class TaskInfo implements IOrdered {
		public int kind;
		public String id;
		public PublishOperation task2;
		
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
		
		if (comp != null)
			comp.createControl();
	}
	
	public List getChildFragments() {
		updateTasks();
		return super.getChildFragments();
	}

	public void setTaskModel(TaskModel taskModel) {
		super.setTaskModel(taskModel);
		updateTasks();
	}

	public void updateTasks() {
		tasks = null;
		if (getTaskModel() == null)
			return;

		IServer server = (IServer) getTaskModel().getObject(TaskModel.TASK_SERVER);
		List modules = (List) getTaskModel().getObject(TaskModel.TASK_MODULES);
		
		if (server != null && modules == null) {
			final List moduleList = new ArrayList();
			((Server) server).visit(new IModuleVisitor() {
				public boolean visit(IModule[] module2) {
					moduleList.add(module2);
					return true;
				}
			}, null);

			modules = moduleList;
		}
		
		if (server != null && modules != null) {
			tasks = new ArrayList();
			createTasks(server, modules);
		}
		
		if (comp != null)
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					comp.setTasks(tasks);
				}
			});
	}
	
	protected void createTasks(IServer server, List modules) {
		String serverTypeId = null;
		if (server != null)
			serverTypeId = server.getServerType().getId();
		
		// server tasks
		IPublishTask[] publishTasks = ServerPlugin.getPublishTasks();
		if (publishTasks != null) {
			int size = publishTasks.length;
			for (int i = 0; i < size; i++) {
				IPublishTask task = publishTasks[i];
				if (serverTypeId != null && task.supportsType(serverTypeId)) {
					PublishOperation[] tasks2 = task.getTasks(server, modules);
					if (tasks2 != null) {
						int size2 = tasks2.length;
						for (int j = 0; j < size2; j++) {
							int kind = tasks2[j].getKind(); 
							if (kind == PublishOperation.OPTIONAL || kind == PublishOperation.PREFERRED)
								hasOptionalTasks = true;
							tasks2[i].setTaskModel(getTaskModel());
							addServerTask(server, tasks2[j]);
						}
					}
				}
			}
		}
	}

	public void addServerTask(IServer server, PublishOperation task2) {
		TaskInfo sti = new TaskInfo();
		sti.task2 = task2;
		sti.kind = task2.getKind();
		String id = server.getId();
		sti.id = id + "|" + task2.getLabel();
		if (sti.kind == PublishOperation.PREFERRED || sti.kind == PublishOperation.REQUIRED)
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

	/**
	 * @see WizardFragment#performFinish(IProgressMonitor)
	 */
	public void performFinish(IProgressMonitor monitor) throws CoreException {
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
		TaskModel taskModel = getTaskModel();
		IServer server = (IServer) taskModel.getObject(TaskModel.TASK_SERVER);
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
		
		taskModel.putObject(TaskModel.TASK_SERVER, serverWC);
		
		// begin task
		monitor.beginTask(Messages.performingTasks, performTasks.size() * 1000);
		
		ServerEditorCore.sortOrderedList(performTasks);

		Iterator iterator = performTasks.iterator();
		while (iterator.hasNext()) {
			IProgressMonitor subMonitor = ProgressUtil.getSubMonitorFor(monitor, 1000);
			Object obj = iterator.next();
			if (obj instanceof TasksWizardFragment.TaskInfo) {
				TasksWizardFragment.TaskInfo sti = (TasksWizardFragment.TaskInfo) obj;
				try {
					Trace.trace(Trace.FINER, "Executing task: " + sti.task2.getLabel());
					sti.task2.setTaskModel(taskModel);
					sti.task2.execute(subMonitor, null);
				} catch (final CoreException ce) {
					Trace.trace(Trace.SEVERE, "Error executing task " + sti.task2.getLabel(), ce);
					throw ce;
				}
			}
			subMonitor.done();
		}
		
		if (createdServerWC) {
			if (serverWC.isDirty()) {
				IFile file = ((Server)serverWC).getFile();
				if (file != null) {
					IProject project = file.getProject();
					
					if (!file.getProject().exists())
						EclipseUtil.createNewServerProject(null, project.getName(), null, monitor);
					
					ProjectProperties pp = (ProjectProperties) ServerCore.getProjectProperties(project);
					if (!pp.isServerProject())
						pp.setServerProject(true, monitor);
				}
				taskModel.putObject(TaskModel.TASK_SERVER, serverWC.save(false, monitor));
			} else
				taskModel.putObject(TaskModel.TASK_SERVER, serverWC.getOriginal());
		}
				
		monitor.done();
	}

	/**
	 * Return <code>true</code> if this wizard has tasks.
	 * 
	 * @return <code>true</code> if this wizard has tasks, and <code>false</code>
	 *    otherwise
	 */
	public boolean hasTasks() {
		return tasks == null || !tasks.isEmpty();
	}
	
	/**
	 * Return <code>true</code> if this wizard has optional tasks.
	 * 
	 * @return <code>true</code> if this wizard has optional tasks, and
	 *    <code>false</code> otherwise
	 */
	public boolean hasOptionalTasks() {
		return hasOptionalTasks;
	}
}