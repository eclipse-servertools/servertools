/*******************************************************************************
 * Copyright (c) 2007 SAS Institute, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Larry Isaacs - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatWebModule;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServerBehaviour;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServer.IOperationListener;

/**
 * Dialog to confirm deletion of the work directory for a module on a
 * server, or the work directory for the entire server.  Handling
 * includes stopping and restarting the server if it is running at
 * the time of the deletion. 
 *
 */
public class CleanWorkDirDialog extends Dialog {
	/**
	 * Error code when error occurs prior to deletion
	 */
	public static final int ERROR_PREDELETE = 0;
	
	/**
	 * Error code when error occurs during the deletion
	 */
	public static final int ERROR_DURINGDELETE = 1;

	/**
	 * Error code when error occurs after deletion
	 */
	public static final int ERROR_POSTDELETE = 2;
	
	protected IServer server;
	protected IModule module;
	protected int state;
	protected String mode;
	protected IStatus resultStatus = Status.OK_STATUS;
	
	/**
	 * Creates a dialog instance confirm deletion of the work directory for a
	 * module on a server, or the work directory for the entire server.
	 * 
	 * @param parentShell the parent shell, or <code>null</code> to create a
	 *  top-level shell
	 * @param server server on which to delete the work directory
	 * @param module module whose work directory is to be deleted, or <code>null</code> if
	 *  if these server's entire work directory is to be deleted.
	 */
	public CleanWorkDirDialog(Shell parentShell, IServer server, IModule module) {
		super(parentShell);
		
		if (server == null)
			throw new IllegalArgumentException();

		this.server = server;
		this.module = module;
		
	}
	
	/**
	 * Get the IStatus result from the stop, deletion, and start operations.
	 * 
	 * @return returns operation result.  Will be Status.OK_STATUS if
	 * all operations complete successfully.  The error status if not.
	 */
	public IStatus getResultStatus() {
		return resultStatus;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.confirmCleanWorkDirTitle);
	}

	protected Control createDialogArea(Composite parent) {
		if (state < 0 || state == IServer.STATE_UNKNOWN) 
			captureServerState();

		// create a composite with standard margins and spacing
		Composite composite = (Composite)super.createDialogArea(parent);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.DELETE_SERVER_DIALOG);

		Label label = new Label(composite, SWT.WRAP);
		if (state == IServer.STATE_STARTING || state == IServer.STATE_STOPPING || state == IServer.STATE_UNKNOWN) {
			label.setText(NLS.bind(Messages.cleanServerStateChanging, server.getName()));
		}
		else {
			if (module != null)
				label.setText(NLS.bind(Messages.cleanModuleWorkDir, module.getName(), server.getName()));
			else
				label.setText(NLS.bind(Messages.cleanServerWorkDir, server.getName()));
			GridData data = new GridData();
			data.widthHint = 300;
			label.setLayoutData(data);
			
			if (state == IServer.STATE_STARTED) {
				label = new Label(composite, SWT.WRAP);
				label.setText(Messages.cleanServerRunning);
				data = new GridData();
				data.widthHint = 300;
				label.setLayoutData(data);
			}
		}
		
		applyDialogFont(composite);
		return composite;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		if (state < 0 || state == IServer.STATE_UNKNOWN) 
			captureServerState();

		// If server is transitioning, only allow Cancel
		if (state == IServer.STATE_STARTING || state == IServer.STATE_STOPPING) {
			Button button = getButton(IDialogConstants.OK_ID);
			if (button != null)
				button.setEnabled(false);
		}
	}

	protected void okPressed() {
		// If state has changed since dialog was open, abort
		if (server.getServerState() != state) {
			newResultStatus(ERROR_PREDELETE, Messages.errorCouldNotCleanStateChange, null);
			super.cancelPressed();
			return;
		}
		
		IOperationListener listener = new IOperationListener() {
			public void done(IStatus result) {
				resultStatus = result;
			}
		};
		boolean restart = false;
		// If server isn't stopped, try to stop, clean, and restart
		if (state != IServer.STATE_STOPPED) {
			resultStatus = server.canStop();
			if (!resultStatus.isOK()) {
				wrapResultStatus(ERROR_PREDELETE, Messages.errorCouldNotCleanCantStop);
				super.cancelPressed();
				return;
			}

			server.stop(false, listener);

			if (!resultStatus.isOK()) {
				wrapResultStatus(ERROR_PREDELETE, Messages.errorCouldNotCleanStopFailed);
				super.cancelPressed();
				return;
			}
			if (server.getServerState() != IServer.STATE_STOPPED) {
				newResultStatus(ERROR_PREDELETE, Messages.errorCouldNotCleanStopFailed, null);
				super.cancelPressed();
				return;
			}
			restart = true;
		}
			
		// Delete the work directory
		TomcatServerBehaviour tsb = (TomcatServerBehaviour)server.loadAdapter(
				TomcatServerBehaviour.class, null);
		try {
			if (module != null) {
				IWebModule webModule = (IWebModule)module.loadAdapter(IWebModule.class, null);
				if (webModule != null) {
					ITomcatWebModule tcWebModule = new WebModule(webModule.getContextRoot(), "", "", true);
					resultStatus = tsb.cleanContextWorkDir(tcWebModule, null);
				}
				else {
					newResultStatus(ERROR_DURINGDELETE, 
							restart ? Messages.errorCantIdentifyWebAppWasRunning : Messages.errorCantIdentifyWebApp, null);
					super.cancelPressed();
					return;
				}
			}
			else
				resultStatus = tsb.cleanServerWorkDir(null);
		} catch (CoreException ce) {
			resultStatus = ce.getStatus();
		}
		if (!resultStatus.isOK()) {
			wrapResultStatus(ERROR_DURINGDELETE,
					restart ? Messages.errorErrorDuringCleanWasRunning : Messages.errorErrorDuringClean);
			super.cancelPressed();
			return;
		}
			
		if (restart) {
			resultStatus = server.canStart(mode);
			if (!resultStatus.isOK()) {
				wrapResultStatus(ERROR_POSTDELETE, Messages.errorCleanCantRestart);
				super.cancelPressed();
				return;
			}
			server.start(mode, listener);
			if (!resultStatus.isOK()) {
				wrapResultStatus(ERROR_POSTDELETE, Messages.errorCleanRestartFailed);
				super.cancelPressed();
				return;
			}
		}

		super.okPressed();
	}
	
	private void captureServerState() {
		state = server.getServerState();
		if (state != IServer.STATE_STOPPED) {
			mode = server.getMode();
		}
	}

	private void newResultStatus(int errorCode, String message, Throwable throwable) {
		resultStatus = new Status(IStatus.ERROR, TomcatUIPlugin.PLUGIN_ID, errorCode,
				message, throwable);
	}
	
	private void wrapResultStatus(int errorCode, String message) {
		MultiStatus ms = new MultiStatus(TomcatUIPlugin.PLUGIN_ID, errorCode, message, null);
		ms.add(resultStatus);
		resultStatus = ms;
	}
}