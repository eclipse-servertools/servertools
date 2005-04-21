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
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.util.Task;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.task.*;
import org.eclipse.wst.server.ui.internal.wizard.fragment.ModifyModulesWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewServerWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard used to select a server from various lists.
 */
public class RunOnServerWizard extends TaskWizard {
	protected static NewServerWizardFragment task;

	/**
	 * RunOnServerWizard constructor comment.
	 * 
	 * @param module a module
	 * @param launchMode a launch mode
	 */
	public RunOnServerWizard(final IModule module, final String launchMode) {
		super(ServerUIPlugin.getResource("%wizRunOnServerTitle"), new WizardFragment() {
			protected void createChildFragments(List list) {
				task = new NewServerWizardFragment(module, launchMode);
				list.add(task);
				list.add(new FinishWizardFragment(new TempSaveRuntimeTask()));
				list.add(new FinishWizardFragment(new TempSaveServerTask()));
				list.add(new ModifyModulesWizardFragment(module));
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
			protected boolean useJob() {
				return true;
			}
		});
	
		setNeedsProgressMonitor(true);
		if (ILaunchManager.DEBUG_MODE.equals(launchMode))
			setWindowTitle(ServerUIPlugin.getResource("%wizDebugOnServerTitle"));
		else if (ILaunchManager.PROFILE_MODE.equals(launchMode))
			setWindowTitle(ServerUIPlugin.getResource("%wizProfileOnServerTitle"));
	}

	/**
	 * Return the server.
	 * @return org.eclipse.wst.server.core.IServer
	 */
	public IServer getServer() {
		try {
			return (IServer) getRootFragment().getTaskModel().getObject(TaskModel.TASK_SERVER);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isPreferredServer() {
		if (task == null)
			return false;
		return task.isPreferredServer();
	}
}