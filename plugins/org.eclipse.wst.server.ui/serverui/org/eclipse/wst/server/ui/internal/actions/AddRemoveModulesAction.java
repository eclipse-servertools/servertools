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
package org.eclipse.wst.server.ui.internal.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.actions.IServerAction;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.ModifyModulesWizard;
import org.eclipse.swt.widgets.Shell;
/**
 * 
 */
public class AddRemoveModulesAction implements IServerAction {
	public boolean supports(IServer server) {
		if (server == null)
			return false;
		IPath configuration = server.getServerConfiguration();
		return (!server.getServerType().hasServerConfiguration() || configuration != null);
	}

	public void run(Shell shell, IServer server) {
		//if (!ServerUIUtil.promptIfDirty(shell, server))
		//	return;
		
		// check if there are any projects first
		// get currently deployed modules
		List deployed = new ArrayList();
		List modules = new ArrayList();
		IModule[] currentModules = server.getModules(null);
		if (currentModules != null) {
			int size = currentModules.length;
			for (int i = 0; i < size; i++) {
				deployed.add(currentModules[i]);
			}
		}

		// get remaining modules
		IModule[] modules2 = ServerUtil.getModules(server.getServerType().getRuntimeType().getModuleTypes());
		if (modules != null) {
			int size = modules2.length;
			for (int i = 0; i < size; i++) {
				IModule module = modules2[i];
				if (!deployed.contains(module)) {
					IStatus status = server.canModifyModules(new IModule[] { module }, null, null);
					if (status != null && status.isOK())
						modules.add(module);
				}
			}
		}
		
		if (deployed.isEmpty() && modules.isEmpty()) {
			MessageDialog.openInformation(shell, ServerUIPlugin.getResource("%defaultDialogTitle"), ServerUIPlugin.getResource("%dialogAddRemoveModulesNone"));
			return;
		}

		ModifyModulesWizard wizard = new ModifyModulesWizard(server);
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
		dialog.open();
	}
}