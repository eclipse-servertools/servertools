/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.swt.widgets.Shell;
/**
 * Restart a server.
 */
public class RestartAction extends AbstractServerAction {
	protected String mode;

	public RestartAction(Shell shell, ISelectionProvider selectionProvider, String name) {
		this(shell, selectionProvider, name, null);
	}

	public RestartAction(Shell shell, ISelectionProvider selectionProvider, String name, String mode) {
		super(shell, selectionProvider, name);
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) { }
		this.mode = mode;
	}

	/**
	 * Return true if this server can currently be acted on.
	 *
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public boolean accept(IServer server) {
		if (mode == null)
			mode = server.getMode();
		
		return server.getServerType() != null && server.getServerType().getServerStateSet() == IServerType.SERVER_STATE_SET_MANAGED && server.canRestart(mode);
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public void perform(IServer server) {
		if (!ServerUIUtil.promptIfDirty(shell, server))
			return;
	
		if (ServerCore.getServerPreferences().isAutoPublishing() && server.shouldPublish()) {
			// publish first
			IStatus status = status = ServerUIUtil.publishWithDialog(server, false);
	
			if (status == null || status.getSeverity() == IStatus.ERROR) // user cancelled
				return;
		}
		
		server.restart(mode);
	}
}