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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard used to select server and module tasks.
 */
public class SelectTasksWizard extends TaskWizard {
	protected static TasksWizardFragment fragment;

	/**
	 * SelectTasksWizard constructor.
	 * 
	 * @param server a server
	 */
	public SelectTasksWizard(final IServer server) {
		super(Messages.wizTaskWizardTitle, new WizardFragment() {
			protected void createChildFragments(List list) {
				fragment = new TasksWizardFragment();
				list.add(fragment);
				list.add(new WizardFragment() {
					public void performFinish(IProgressMonitor monitor) throws CoreException {
						WizardTaskUtil.saveServer(getTaskModel(), monitor);
					}
				});
			}
		});
		getTaskModel().putObject(TaskModel.TASK_SERVER, server);
		addPages();
	}

	/**
	 * Return <code>true</code> if this wizard has tasks.
	 * 
	 * @return <code>true</code> if this wizard has tasks, and <code>false</code>
	 *    otherwise
	 */
	public boolean hasTasks() {
		return fragment.hasTasks();
	}

	/**
	 * Return <code>true</code> if this wizard has optional tasks.
	 * 
	 * @return <code>true</code> if this wizard has optional tasks, and
	 *    <code>false</code> otherwise
	 */
	public boolean hasOptionalTasks() {
		return fragment.hasOptionalTasks();
	}

	/**
	 * Return <code>true</code> if this wizard has preferred tasks.
	 * 
	 * @return <code>true</code> if this wizard has preferred tasks, and
	 *    <code>false</code> otherwise
	 */
	public boolean hasPreferredTasks() {
		return fragment.hasPreferredTasks();
	}
}