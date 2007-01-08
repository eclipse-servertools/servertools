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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerAttributes;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.fragment.ModifyModulesWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewServerWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.OptionalClientWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard used to select a server from various lists.
 */
public class RunOnServerWizard extends TaskWizard {
	protected static NewServerWizardFragment task;
	protected static OptionalClientWizardFragment fragment;

	public RunOnServerWizard(IModule module, String launchMode) {
		this(module, launchMode, null);
	}

	/**
	 * RunOnServerWizard constructor comment.
	 * 
	 * @param module a module
	 * @param launchMode a launch mode
	 * @param moduleArtifact a module artifact
	 */
	public RunOnServerWizard(final IModule module, final String launchMode, final IModuleArtifact moduleArtifact) {
		super(Messages.wizRunOnServerTitle, new WizardFragment() {
			protected void createChildFragments(List list) {
				task = new NewServerWizardFragment(module, launchMode);
				list.add(task);
				list.add(new WizardFragment() {
					public void performFinish(IProgressMonitor monitor) throws CoreException {
						WizardTaskUtil.tempSaveRuntime(getTaskModel(), monitor);
						WizardTaskUtil.tempSaveServer(getTaskModel(), monitor);
					}
				});
				list.add(new ModifyModulesWizardFragment(module));
				list.add(new TasksWizardFragment());
				list.add(new WizardFragment() {
					public void performFinish(IProgressMonitor monitor) throws CoreException {
						WizardTaskUtil.saveRuntime(getTaskModel(), monitor);
						WizardTaskUtil.saveServer(getTaskModel(), monitor);
						try {
							IServerAttributes server = (IServerAttributes) getTaskModel().getObject(TaskModel.TASK_SERVER);
							ServerUIPlugin.getPreferences().addHostname(server.getHost());
						} catch (Exception e) {
							// ignore
						}
					}
				});
				//fragment = new OptionalClientWizardFragment(moduleArtifact, launchMode);
				//list.add(fragment);
			}
		});
		
		setNeedsProgressMonitor(true);
		if (ILaunchManager.DEBUG_MODE.equals(launchMode))
			setWindowTitle(Messages.wizDebugOnServerTitle);
		else if (ILaunchManager.PROFILE_MODE.equals(launchMode))
			setWindowTitle(Messages.wizProfileOnServerTitle);
	}

	/**
	 * Return the server.
	 * @return the server
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

	/**
	 * Return the selected client.
	 * @return the client
	 */
	public IClient getSelectedClient() {
		return fragment.getSelectedClient();
	}
}