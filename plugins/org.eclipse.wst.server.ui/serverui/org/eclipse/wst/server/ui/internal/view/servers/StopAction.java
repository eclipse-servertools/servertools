/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
/**
 * Stop (terminate) a server.
 */
public class StopAction extends AbstractServerAction {
	public StopAction(Shell shell, ISelectionProvider selectionProvider, String name) {
		super(shell, selectionProvider, name);
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public boolean accept(IServer server) {
		if (server.getServerType() == null)
			return false;
		return server.getServerType() != null && server.canStop().isOK();
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void perform(final IServer server) {
		ServerUIPlugin.addTerminationWatch(shell, server, ServerUIPlugin.STOP);
	
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog dialog = new MessageDialog(shell, Messages.defaultDialogTitle, null,
						NLS.bind(Messages.dialogStoppingServer, server.getName()), MessageDialog.INFORMATION, new String[0], 0);
				dialog.setBlockOnOpen(false);
				dialog.open();
	
				server.stop(false);
				dialog.close();
			}
		});
	}
}