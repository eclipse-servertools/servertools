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
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.util.Task;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.task.*;
import org.eclipse.wst.server.ui.internal.wizard.fragment.ModifyModulesWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewServerWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
/**
 * A wizard to create a new server and server configuration.
 */
public class NewServerWizard extends TaskWizard implements INewWizard {
	/**
	 * NewServerWizard constructor comment.
	 */
	public NewServerWizard() {
		this(null, null);
	}

	public NewServerWizard(final String[] ids, final String[] values) {
		super(ServerUIPlugin.getResource("%wizNewServerWizardTitle"), new WizardFragment() {
			protected void createChildFragments(List list) {
				if (ids != null)
					list.add(new InputWizardFragment(ids, values));
				list.add(new NewServerWizardFragment());
				list.add(new FinishWizardFragment(new TempSaveRuntimeTask()));
				list.add(new FinishWizardFragment(new TempSaveServerTask()));
				list.add(new ModifyModulesWizardFragment());
				list.add(new TasksWizardFragment());
				list.add(new FinishWizardFragment(new SaveRuntimeTask()));
				list.add(new FinishWizardFragment(new SaveServerTask()));
				list.add(new FinishWizardFragment(new Task() {
					public void execute(IProgressMonitor monitor) throws CoreException {
						try {
							IServer server = (IServer) getTaskModel().getObject(TaskModel.TASK_SERVER);
							ServerUIPlugin.getPreferences().addHostname(server.getHost());
						} catch (Exception e) {
							// ignore
						}
					}
				}));
			}
		});
		
		setForcePreviousAndNextButtons(true);
	}
	
	public void init(IWorkbench newWorkbench, IStructuredSelection newSelection) {
		// do nothing
	}
}