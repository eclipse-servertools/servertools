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
package org.eclipse.wst.server.ui;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.editor.IServerEditorInput;
import org.eclipse.wst.server.ui.internal.PublishDialog;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.ServerUIPreferences;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorInput;
import org.eclipse.wst.server.ui.internal.task.FinishWizardFragment;
import org.eclipse.wst.server.ui.internal.task.InputWizardFragment;
import org.eclipse.wst.server.ui.internal.task.SaveRuntimeTask;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewRuntimeWizardFragment;
import org.eclipse.wst.server.ui.wizard.*;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
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
	 * Open the passed server in the server editor.
	 *
	 * @param server
	 */
	public static void editServer(IServer server) {
		if (server == null)
			return;

		String serverId = null;
		if (server != null)
			serverId = server.getId();
		editServer(serverId);
	}

	/**
	 * Open the passed server id into the server editor.
	 *
	 * @param serverId
	 */
	public static void editServer(String serverId) {
		if (serverId == null)
			return;

		IWorkbenchWindow workbenchWindow = ServerUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();

		try {
			IServerEditorInput input = new ServerEditorInput(serverId);
			page.openEditor(input, IServerEditorInput.EDITOR_ID);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error opening server editor", e);
		}
	}

	/**
	 * Publish the given server, and display the publishing in a dialog.
	 *
	 * @param server
	 * @return IStatus
	 */
	public static IStatus publishWithDialog(Shell shell, IServer server) {
		return PublishDialog.publish(shell, server);
	}

	/**
	 * Prompts the user if the server is dirty. Returns true if the server was
	 * not dirty or if the user decided to continue anyway. Returns false if
	 * the server is dirty and the user chose to cancel the operation.
	 *
	 * @return boolean
	 */
	public static boolean promptIfDirty(Shell shell, IServer server) {
		if (server == null)
			return false;
		
		if (!(server instanceof IServerWorkingCopy))
			return true;

		String title = ServerUIPlugin.getResource("%resourceDirtyDialogTitle");
		
		IServerWorkingCopy wc = (IServerWorkingCopy) server;
		if (wc.isDirty()) {
			String message = ServerUIPlugin.getResource("%resourceDirtyDialogMessage", server.getName());
			String[] labels = new String[] {ServerUIPlugin.getResource("%resourceDirtyDialogContinue"), IDialogConstants.CANCEL_LABEL};
			MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.INFORMATION, labels, 0);
	
			if (dialog.open() != 0)
				return false;
		}
	
		return true;
	}

	/**
	 * Use the preference to prompt the user to save dirty editors, if applicable.
	 * 
	 * @return boolean  - Returns false if the user cancelled the operation
	 */
	public static boolean saveEditors() {
		byte b = ServerUIPlugin.getPreferences().getSaveEditors();
		if (b == ServerUIPreferences.SAVE_EDITORS_NEVER)
			return true;
		return ServerUIPlugin.getInstance().getWorkbench().saveAllEditors(b == ServerUIPreferences.SAVE_EDITORS_PROMPT);			
	}

	/**
	 * Publishes to the given server, if the preference is set and publishing was required.
	 * 
	 * @return boolean - Returns false if the user cancelled the operation.
	 */
	public static boolean publish(Shell shell, IServer server) {
		if (ServerCore.getServerPreferences().isAutoPublishing() && server.shouldPublish()) {
			// publish first
			IStatus status = publishWithDialog(shell, server);

			if (status == null || status.getSeverity() == IStatus.ERROR) // user cancelled
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @param shell
	 * @return
	 */
	public static boolean showNewRuntimeWizard(Shell shell) {
		return showNewRuntimeWizard(shell, null, null);
	}

	/**
	 * Shows the new runtime wizard.
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