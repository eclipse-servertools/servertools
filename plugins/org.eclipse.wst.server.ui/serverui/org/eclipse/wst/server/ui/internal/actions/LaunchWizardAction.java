/*******************************************************************************
 * Copyright (c) 2003, 2017 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
/**
 * An abstract action that opens up a workbench wizard when run.
 */
abstract class LaunchWizardAction extends Action {
	/**
	 * LaunchWizardAction
	 */
	public LaunchWizardAction() {
		super();
	}

	/**
	 * Return the workbench wizard that should be opened.
	 *
	 * @return the wizard to open
	 */
	protected abstract IWorkbenchWizard getWizard();

	/*
	 * @see IAction.run()
	 */
	public void run() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		ISelection selection = workbenchWindow.getSelectionService().getSelection();
	
		IStructuredSelection selectionToPass = null;
		if (selection instanceof IStructuredSelection)
			selectionToPass = (IStructuredSelection) selection;
		else
			selectionToPass = StructuredSelection.EMPTY;
	
		IWorkbenchWizard wizard = getWizard();
		wizard.init(workbench, selectionToPass);
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setMinimumPageSize(300, 480);
		dialog.open();
	}
}