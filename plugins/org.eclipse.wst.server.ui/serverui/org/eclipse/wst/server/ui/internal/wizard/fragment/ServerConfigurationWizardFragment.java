/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.wizard.page.WizardUtil;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class ServerConfigurationWizardFragment extends WizardFragment {
	public ServerConfigurationWizardFragment() {
		// do nothing
	}
	
	public void enter() {
		ITaskModel model = getTaskModel();
		IRuntime runtime = (IRuntime) model.getObject(ITaskModel.TASK_RUNTIME);
		IServerWorkingCopy server = (IServerWorkingCopy) model.getObject(ITaskModel.TASK_SERVER);
		
		IServerType type = server.getServerType();
		if (type.hasServerConfiguration() && server.getServerConfiguration() == null) {
			try {
				IFile file = null;
				if (ServerCore.getServerPreferences().isCreateResourcesInWorkspace())
					file = ServerUtil.getUnusedServerConfigurationFile(WizardUtil.getServerProject(), type.getServerConfigurationType());
				
				IServerConfigurationWorkingCopy serverConfiguration = type.getServerConfigurationType().importFromRuntime(null, file, runtime, new NullProgressMonitor());
				ServerUtil.setServerConfigurationDefaultName(serverConfiguration);
				model.putObject(ITaskModel.TASK_SERVER_CONFIGURATION, serverConfiguration);
				server.setServerConfiguration(serverConfiguration);
				updateChildFragments();
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create configuration", e);
			}
		}
	}

	protected void createChildFragments(List list) {
		IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
		IServerConfiguration serverConfiguration = null;
		if (server != null)
			serverConfiguration = server.getServerConfiguration();
		if (serverConfiguration != null) {
			WizardFragment sub = ServerUICore.getWizardFragment(serverConfiguration.getServerConfigurationType().getId());
			if (sub != null)
				list.add(sub);
		}
	}
}