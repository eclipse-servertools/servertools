package org.eclipse.wst.server.ui.internal.view.servers;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Stop (terminate) a server.
 */
public class StopAction extends AbstractServerAction {
	protected int serverStateSet;

	public StopAction(Shell shell, ISelectionProvider selectionProvider, String name, int serverStateSet) {
		super(shell, selectionProvider, name);
		this.serverStateSet = serverStateSet;
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public boolean accept(IServer server) {
		if (server.getServerType() == null || server.getServerType().getServerStateSet() != serverStateSet)
			return false;
		return server.getServerType() != null && server.canStop();
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public void perform(final IServer server) {
		ServerTableViewer.removeStartupListener(server);
		
		ServerUIPlugin.addTerminationWatch(shell, server, ServerUIPlugin.STOP);
	
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog dialog = new MessageDialog(shell, ServerUIPlugin.getResource("%defaultDialogTitle"), null,
					ServerUIPlugin.getResource("%dialogStoppingServer", server.getName()), MessageDialog.INFORMATION, new String[0], 0);
				dialog.setBlockOnOpen(false);
				dialog.open();
	
				server.stop();
				dialog.close();
			}
		});
	}
}
