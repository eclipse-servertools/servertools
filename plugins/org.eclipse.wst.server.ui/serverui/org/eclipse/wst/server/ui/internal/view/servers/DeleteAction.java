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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.core.IElement;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.internal.DeleteServerDialog;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.swt.widgets.Shell;
/**
 * Action for deleting server resources.
 */
public class DeleteAction extends Action {
	protected List deleteList = new ArrayList();
	protected List deleteExtraList = new ArrayList();
	protected Shell shell;

	/**
	 * DeleteAction constructor comment.
	 */
	public DeleteAction(Shell shell, IElement serverResource) {
		this(shell, new IElement[] { serverResource });
	}

	/**
	 * DeleteAction constructor comment.
	 */
	public DeleteAction(Shell shell, IElement[] serverResources) {
		super(ServerUIPlugin.getResource("%actionDelete"));
		this.shell = shell;
		
		int size = serverResources.length;
		for (int i = 0; i < size; i++) {
			deleteList.add(serverResources[i]);
		}
		
		for (int i = 0; i < size; i++) {
			if (serverResources[i] instanceof IServer) {
				IServer server = (IServer) serverResources[i];
				IServerConfiguration config = server.getServerConfiguration();
				if (config != null && !deleteList.contains(config))
					deleteExtraList.add(config);
			}
		}
		
		// remove configurations that are still referenced
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size2 = servers.length;
			for (int i = 0; i < size2; i++) {
				if (!deleteList.contains(servers[i])) {
					IServerConfiguration config = servers[i].getServerConfiguration();
					if (deleteExtraList.contains(config))
						deleteExtraList.remove(config);
				}
			}
		}
	}

	/**
	 * Invoked when an action occurs. 
	 */
	public void run() {
		DeleteServerDialog dsd = new DeleteServerDialog(shell, deleteList, deleteExtraList);
		dsd.open();
	}
}