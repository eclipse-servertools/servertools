/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.tree;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.swt.widgets.Shell;
/**
 * Action to add or remove configuration to/from a server.
 */
public class SwitchConfigurationAction extends Action {
	protected IServer server;
	protected IFolder config;
	protected Shell shell;
	protected IStatus status;

	/**
	 * SwitchConfigurationAction constructor comment.
	 */
	public SwitchConfigurationAction(Shell shell, String label, IServer server, IFolder config) {
		super(label);
		this.shell = shell;
		this.server = server;
		this.config = config;

		IFolder tempConfig = server.getServerConfiguration();
		if ((tempConfig == null && config == null) || (tempConfig != null && tempConfig.equals(config)))
			setChecked(true);

		if (config == null)
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_CONFIGURATION_NONE));
		else
			setImageDescriptor(((ServerLabelProvider)ServerUICore.getLabelProvider()).getImageDescriptor(config));
		
		IServerType type = server.getServerType();
		if (type.getServerStateSet() == IServerType.SERVER_STATE_SET_MANAGED &&
				server.getServerState() != IServer.STATE_STOPPED)
			setEnabled(false);
	}
	
	public void run() {
		IFolder tempConfig = server.getServerConfiguration();
		if ((tempConfig == null && config == null) || (tempConfig != null && tempConfig.equals(config)))
			return;
			
		if (!EclipseUtil.validateEdit(shell, server))
			return;

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				try {
					monitor = ProgressUtil.getMonitorFor(monitor);
					IServerWorkingCopy workingCopy = server.createWorkingCopy();
					workingCopy.setServerConfiguration(config);
					workingCopy.save(false, monitor);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not save configuration", e);
				}
			}
		};

		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			dialog.run(true, true, runnable);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error switching server configuration", e);
		}
	}
}
