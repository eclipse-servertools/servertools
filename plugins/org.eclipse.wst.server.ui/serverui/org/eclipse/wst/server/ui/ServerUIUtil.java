/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.task.FinishWizardFragment;
import org.eclipse.wst.server.ui.internal.task.InputWizardFragment;
import org.eclipse.wst.server.ui.internal.task.SaveRuntimeTask;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewRuntimeWizardFragment;
import org.eclipse.wst.server.ui.wizard.*;

import org.eclipse.swt.widgets.Shell;
/**
 * Server UI utility methods.
 */
public class ServerUIUtil {
	/**
	 * ServerUIUtil constructor comment.
	 */
	private ServerUIUtil() {
		super();
	}

	/**
	 * Open the new runtime wizard.
	 * 
	 * @param shell
	 * @param runtimeTypeId
	 * @return
	 */
	public static boolean showNewRuntimeWizard(Shell shell, final String runtimeTypeId) {
		IRuntimeType runtimeType = null;
		if (runtimeTypeId != null)
			runtimeType = ServerCore.findRuntimeType(runtimeTypeId);
		if (runtimeType != null) {
			try {
				final IRuntimeWorkingCopy runtime = runtimeType.createRuntime(null, null);
				WizardFragment fragment = new WizardFragment() {
					protected void createChildFragments(List list) {
						list.add(new InputWizardFragment(ITaskModel.TASK_RUNTIME, runtime));
						list.add(ServerUICore.getWizardFragment(runtimeTypeId));
						list.add(new FinishWizardFragment(new SaveRuntimeTask()));
					}
				};
				TaskWizard wizard = new TaskWizard(ServerUIPlugin.getResource("%wizNewRuntimeWizardTitle"), fragment);
				wizard.setForcePreviousAndNextButtons(true);
				ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
				return (dialog.open() == IDialogConstants.OK_ID);
			} catch (Exception e) {
				return false;
			}
		}
		return showNewRuntimeWizard(shell, null, null, runtimeTypeId);
	}
	
	/**
	 * Open the new runtime wizard.
	 * @param shell
	 * @return
	 */
	public static boolean showNewRuntimeWizard(Shell shell) {
		return showNewRuntimeWizard(shell, null, null);
	}

	/**
	 * Open the new runtime wizard.
	 * 
	 * @param shell
	 * @param type
	 * @param version
	 * @return
	 */
	public static boolean showNewRuntimeWizard(Shell shell, final String type, final String version) {
		return showNewRuntimeWizard(shell, type, version, null);
	}

	/**
	 * Open the new runtime wizard.
	 * 
	 * @param shell
	 * @param type
	 * @param version
	 * @param runtimeTypeId
	 * @return
	 */
	public static boolean showNewRuntimeWizard(Shell shell, final String type, final String version, final String runtimeTypeId) {
		WizardFragment fragment = new WizardFragment() {
			protected void createChildFragments(List list) {
				list.add(new NewRuntimeWizardFragment(type, version, runtimeTypeId));
				list.add(new FinishWizardFragment(new SaveRuntimeTask()));
			}
		};
		TaskWizard wizard = new TaskWizard(ServerUIPlugin.getResource("%wizNewRuntimeWizardTitle"), fragment);
		wizard.setForcePreviousAndNextButtons(true);
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wizard);
		return (dialog.open() == IDialogConstants.OK_ID);
	}
}