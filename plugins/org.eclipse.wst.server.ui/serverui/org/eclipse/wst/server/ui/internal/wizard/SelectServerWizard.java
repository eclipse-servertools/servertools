package org.eclipse.wst.server.ui.internal.wizard;
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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.util.Task;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.ServerUIPreferences;
import org.eclipse.wst.server.ui.internal.task.*;
import org.eclipse.wst.server.ui.internal.wizard.fragment.ModifyModulesWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewServerWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.TaskWizard;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * A wizard used to select a server from various lists.
 */
public class SelectServerWizard extends TaskWizard {
	protected static NewServerWizardFragment task;

	/**
	 * SelectServerWizard constructor comment.
	 */
	public SelectServerWizard(final IModule module, final String launchMode) {
		super(ServerUIPlugin.getResource("%wizSelectServerWizardTitle"), new WizardFragment() {
			public void createSubFragments(List list) {
				task = new NewServerWizardFragment(module, launchMode);
				list.add(task);
				list.add(new FinishWizardFragment(new TempSaveRuntimeTask()));
				list.add(new FinishWizardFragment(new TempSaveServerTask()));
				list.add(new FinishWizardFragment(new TempSaveServerConfigurationTask()));
				list.add(new ModifyModulesWizardFragment(module));
				list.add(new TasksWizardFragment());
				list.add(new FinishWizardFragment(new SaveRuntimeTask()));
				list.add(new FinishWizardFragment(new SaveServerTask()));
				list.add(new FinishWizardFragment(new SaveServerConfigurationTask()));
				list.add(new FinishWizardFragment(new Task() {
					public void execute(IProgressMonitor monitor) throws CoreException {
						
						try {
							IServer server = (IServer) getTaskModel().getObject(ITaskModel.TASK_SERVER);
							((ServerUIPreferences)ServerUICore.getPreferences()).addHostname(server.getHostname());
						} catch (Exception e) { }
					}
				}));
			}
		});
	
		setNeedsProgressMonitor(true);
	}

	/**
	 * Return the server.
	 * @return org.eclipse.wst.server.core.model.IServer
	 */
	public IServer getServer() {
		try {
			return (IServer) getRootFragment().getTaskModel().getObject(ITaskModel.TASK_SERVER);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean isPreferredServer() {
		if (task == null)
			return false;
		else
			return task.isPreferredServer();
	}
}