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
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.ui.internal.editor.IOrdered;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
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
			if (id == null)
				return false;
			
			if (selectedTaskMap.containsKey(id))
				return ((Boolean) selectedTaskMap.get(id)).booleanValue();
			
			if (selectedTaskMap.containsKey(DEFAULT + id))
				return ((Boolean) selectedTaskMap.get(DEFAULT + id)).booleanValue();

			return false;
		}
		
		public void setDefaultSelected(boolean sel) {
			selectedTaskMap.put(DEFAULT + getId(), new Boolean(sel));
		}

		public boolean getDefaultSelected() {
			return ((Boolean) selectedTaskMap.get(DEFAULT + id)).booleanValue();
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
		
		public boolean equals(Object obj) {
			if (!(obj instanceof TaskInfo))
				return false;
			
			TaskInfo ti = (TaskInfo) obj;
			if (kind != ti.kind)
				return false;
			
			if (id == null || !id.equals(ti.id))
				return false;
			
			if (task2 == null && ti.task2 != null)
				return false;
			
			if (task2 != null && ti.task2 == null)
				return false;
			
			try {
				if (task2 != null && ti.task2 != null) {
					if (task2.getKind() != ti.task2.getKind())
						return false;
					if (task2.getOrder() != ti.task2.getOrder())
						return false;
					if (!task2.getLabel().equals(ti.task2.getLabel()))
						return false;
					if (!task2.getDescription().equals(ti.task2.getDescription()))
						return false;
				}
			} catch (Exception e) {
				// ignore
			}
			return true;
		}
	}

	protected List tasks;
	protected boolean hasOptionalTasks;
	protected boolean hasPreferredTasks;

	protected Map selectedTaskMap = new HashMap();

	public TasksWizardFragment() {
		// do nothing
	}

	protected void createChildFragments(List<WizardFragment> list) {
		if (tasks == null || tasks.isEmpty())
			return;
		
		int TASKS_PER_PAGE = 5;
		int size = tasks.size();
		int pages = (size - 1) / TASKS_PER_PAGE + 1;
		for (int i = 0; i < pages; i++) {
			SubTasksWizardFragment fragment = new SubTasksWizardFragment();
			List list2 = tasks.subList(TASKS_PER_PAGE * i, Math.min(size, TASKS_PER_PAGE * (i+1)));
			fragment.updateTasks(list2);
			list.add(fragment);
		}
	}

	public void enter() {
		updateTasks();
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
		if (getTaskModel() == null) {
			tasks = null;
			return;
		}
		
		IServerAttributes server = (IServerAttributes) getTaskModel().getObject(TaskModel.TASK_SERVER);
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
			hasOptionalTasks = false;
			hasPreferredTasks = false;
			List taskList = new ArrayList(5);
			createTasks(taskList, server, modules);
			
			if (tasks == null || !tasks.equals(taskList)) {
				tasks = taskList;
				updateChildFragments();
				
				boolean b = tasks == null || !tasks.isEmpty();
				if (hasOptionalTasks)
					b = true;
				getTaskModel().putObject(WizardTaskUtil.TASK_HAS_TASKS, new Boolean(b));
			}
		}
	}

	protected void createTasks(List taskList, IServerAttributes server, List modules) {
		if (server == null)
			return;
		
		List enabledTasks = ((Server)server).getEnabledOptionalPublishOperationIds();
		List disabledTasks = ((Server)server).getDisabledPreferredPublishOperationIds();
		PublishOperation[] tasks2 = ((Server)server).getAllTasks(modules);
		for (int j = 0; j < tasks2.length; j++) {
			int kind = tasks2[j].getKind();
			String id = ((Server)server).getPublishOperationId(tasks2[j]);
			if (kind == PublishOperation.OPTIONAL || kind == PublishOperation.PREFERRED)
				hasOptionalTasks = true;
			if (kind == PublishOperation.PREFERRED)
				hasPreferredTasks = true;
			tasks2[j].setTaskModel(getTaskModel());
			
			boolean selected = true;
			if (kind == PublishOperation.OPTIONAL) {
				if (!enabledTasks.contains(id))
					selected = false;
			} else if (kind == PublishOperation.PREFERRED) {
				if (disabledTasks.contains(id))
					selected = false;
			}
			taskList.add(getServerTask(server, tasks2[j], selected));
		}
	}

	public TaskInfo getServerTask(IServerAttributes server, PublishOperation task2, boolean selected) {
		TaskInfo sti = new TaskInfo();
		sti.task2 = task2;
		sti.kind = task2.getKind();
		sti.id = ((Server)server).getPublishOperationId(task2);
		sti.setDefaultSelected(selected);
		
		return sti;
	}

	/**
	 * @see WizardFragment#performFinish(IProgressMonitor)
	 */
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		if (!hasOptionalTasks)
			return;
		
		if (tasks == null || tasks.isEmpty())
			return;
		
		TaskModel taskModel = getTaskModel();
		IServer server = (IServer)taskModel.getObject(TaskModel.TASK_SERVER);
		if (server == null)
			return;
		
		boolean createdWC = false;
		ServerWorkingCopy wc = null;
		if (server instanceof ServerWorkingCopy)
			wc = (ServerWorkingCopy)server;
		else {
			wc = (ServerWorkingCopy)server.createWorkingCopy();
			createdWC = true;
		}
		wc.resetPreferredPublishOperations();
		wc.resetOptionalPublishOperations();
		
		Iterator iterator = tasks.iterator();
		while (iterator.hasNext()) {
			TaskInfo task = (TaskInfo)iterator.next();
			if (PublishOperation.REQUIRED == task.kind)
				continue;
			
			if (PublishOperation.PREFERRED == task.kind) {
				if (!task.isSelected())
					wc.disablePreferredPublishOperations(task.task2);
			} else if (PublishOperation.OPTIONAL == task.kind) {
				if (task.isSelected())
					wc.enableOptionalPublishOperations(task.task2);
			}
		}
		
		if (createdWC && wc.isDirty())
			wc.save(true, monitor);
		monitor.done();
	}
}