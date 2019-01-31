/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A fragment used to select a client.
 */
public class OptionalClientWizardFragment extends WizardFragment {
	protected IClient[] clients;

	protected IModuleArtifact moduleArtifact;
	protected IServer lastServer;
	protected Object lastLaunchable;

	public OptionalClientWizardFragment(IModuleArtifact moduleArtifact) {
		super();
		this.moduleArtifact = moduleArtifact;
	}

	protected void createChildFragments(List<WizardFragment> list) {
		if (clients != null && clients.length > 1)
			list.add(new SelectClientWizardFragment());
	}

	protected void updateClients() {
		if (getTaskModel() == null)
			return;
		
		try {
			IServer server = (IServer) getTaskModel().getObject(TaskModel.TASK_SERVER);
			
			if (server == null) {
				getTaskModel().putObject(WizardTaskUtil.TASK_LAUNCHABLE_ADAPTER, null);
				getTaskModel().putObject(WizardTaskUtil.TASK_LAUNCHABLE, null);
				getTaskModel().putObject(WizardTaskUtil.TASK_CLIENTS, null);
				getTaskModel().putObject(WizardTaskUtil.TASK_HAS_CLIENTS, new Boolean(false));
				return;
			}
			
			//if (lastServer == null)
			//	return;
			
			// get the launchable adapter and module object
			Object launchable = null;
			try {
				Object[] obj = ServerUIPlugin.getLaunchableAdapter(server, moduleArtifact);
				getTaskModel().putObject(WizardTaskUtil.TASK_LAUNCHABLE_ADAPTER, obj[0]);
				getTaskModel().putObject(WizardTaskUtil.TASK_LAUNCHABLE, obj[1]);
				launchable = obj[1];
			} catch (CoreException ce) {
				getTaskModel().putObject(WizardTaskUtil.TASK_LAUNCHABLE_ADAPTER, null);
				getTaskModel().putObject(WizardTaskUtil.TASK_LAUNCHABLE, null);
				getTaskModel().putObject(WizardTaskUtil.TASK_CLIENTS, null);
				getTaskModel().putObject(WizardTaskUtil.TASK_HAS_CLIENTS, new Boolean(false));
				EclipseUtil.openError(null, ce.getStatus());
				return;
			}
			
			// stop here if the server and launchable haven't changed
			if (lastServer != null && lastServer.equals(server)) {
				if (lastLaunchable == null && launchable == null)
					return;
				if (lastLaunchable != null && lastLaunchable.equals(launchable)) {
					return;
				}
			}
			lastServer = server;
			lastLaunchable = launchable;
			
			String launchMode = (String) getTaskModel().getObject(TaskModel.TASK_LAUNCH_MODE);
			clients = ServerUIPlugin.getClients(server, launchable, launchMode);
			
			getTaskModel().putObject(WizardTaskUtil.TASK_CLIENTS, null);
			getTaskModel().putObject(WizardTaskUtil.TASK_HAS_CLIENTS, new Boolean(false));
			if (clients != null) {
				if (clients.length > 1) {
					getTaskModel().putObject(WizardTaskUtil.TASK_CLIENTS, clients);
					getTaskModel().putObject(WizardTaskUtil.TASK_HAS_CLIENTS, new Boolean(true));
				} else
					getTaskModel().putObject(WizardTaskUtil.TASK_CLIENT, clients[0]);
			}
			
			updateChildFragments();
		} catch (Exception e) {
			// ignore
		}
	}

	public void enter() {
		updateClients();
	}

	public List getChildFragments() {
		updateClients();
		return super.getChildFragments();
	}

	public void setTaskModel(TaskModel taskModel) {
		super.setTaskModel(taskModel);
		updateClients();
	}
}