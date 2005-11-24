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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.ui.internal.editor.IOrdered;
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
			tasks2[j].setTaskModel(getTaskModel());
			
			boolean selected = true;
			if (kind == PublishOperation.OPTIONAL) {
				if (!enabledTasks.contains(id))
					selected = false;
			} else if (kind == PublishOperation.PREFERRED) {
				if (disabledTasks.contains(id))
					selected = false;
			}
			addServerTask(server, tasks2[j], selected);
		}
	}

	public void addServerTask(IServer server, PublishOperation task2, boolean selected) {
		TaskInfo sti = new TaskInfo();
		sti.task2 = task2;
		sti.kind = task2.getKind();
		sti.id = ((Server)server).getPublishOperationId(task2);
		sti.setDefaultSelected(selected);
		
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