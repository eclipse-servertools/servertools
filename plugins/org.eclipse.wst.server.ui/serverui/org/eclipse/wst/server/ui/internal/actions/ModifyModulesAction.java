/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.ModifyModulesWizard;
/**
 * Action to modify the modules of a server.
 */
public class ModifyModulesAction implements IObjectActionDelegate {
	protected IWorkbenchPart part;
	protected IServer server;

	/**
	 * ModifyModulesAction constructor comment.
	 */
	public ModifyModulesAction() {
		super();
	}

	/*
	 * Notifies this action delegate that the selection in the workbench has changed. 
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		server = null;
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object obj = sel.getFirstElement();
			if (obj instanceof IServer)
				server = (IServer) obj;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

	/*
	 * Performs this action.
	 */
	public void run(IAction action) {
		if (server == null)
			return;
		
		Shell shell = part.getSite().getShell();
		//if (!ServerUIUtil.promptIfDirty(shell, server))
		//	return;
		
		// check if there are any projects first
		// get currently deployed modules
		List deployed = new ArrayList();
		List modules = new ArrayList();
		IModule[] currentModules = server.getModules();
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
					if (status != null && status.getSeverity() != IStatus.ERROR)
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