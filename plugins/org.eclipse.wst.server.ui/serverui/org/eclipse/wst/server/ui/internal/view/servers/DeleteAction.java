/*******************************************************************************
 * Copyright (c) 2003, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.internal.DeleteServerDialog;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.swt.widgets.Shell;
/**
 * Action for deleting server resources.
 * @deprecated Instead use {@link GlobalDeleteAction}
 */
public class DeleteAction extends AbstractServerAction {
	protected IServer[] servers;
	protected IFolder[] configs;

	/**
	 * DeleteAction constructor.
	 * 
	 * @param shell a shell
	 * @param sp a selection provider
	 */
	public DeleteAction(Shell shell, ISelectionProvider sp) {
		super(shell, sp, Messages.actionDelete);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
	}

	public boolean accept(IServer server) {
		servers = new IServer[] { server };
		List<IFolder> list = new ArrayList<IFolder>();
		
		int size = servers.length;
		for (int i = 0; i < size; i++) {
			if (servers[i].isReadOnly())
				return false;
			
			if (servers[i].getServerConfiguration() != null)
				list.add(servers[i].getServerConfiguration());
		}
		
		// remove configurations that are still referenced by other servers
		IServer[] servers2 = ServerCore.getServers();
		if (servers2 != null) {
			int size2 = servers2.length;
			for (int j = 0; j < size2; j++) {
				boolean found = false;
				for (int i = 0; i < size; i++) {
					if (servers[i].equals(servers2[j]))
						found = true;
				}
				if (!found) {
					IFolder folder = servers2[j].getServerConfiguration();
					if (folder != null && list.contains(folder))
						list.remove(folder);
				}
			}
		}
		
		configs = new IFolder[list.size()];
		list.toArray(configs);
		return true;
	}

	public void perform(IServer server) {
		DeleteServerDialog dsd = new DeleteServerDialog(shell, servers, configs);
		dsd.open();
	}
}