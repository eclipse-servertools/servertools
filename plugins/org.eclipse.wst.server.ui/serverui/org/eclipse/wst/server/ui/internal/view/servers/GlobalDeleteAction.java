/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.internal.DeleteServerDialog;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * This global delete action handles both the server and module deletion.
 */
public class GlobalDeleteAction extends SelectionProviderAction {
	protected IServer[] servers;
	protected IFolder[] configs;
	private Shell shell;

	public GlobalDeleteAction(Shell shell, ISelectionProvider selectionProvider) {
		super(selectionProvider, Messages.actionDelete);
		this.shell = shell;
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));		
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
	}
	
	@Override
	public void selectionChanged(IStructuredSelection sel) {
		if (sel.isEmpty()) {
			setEnabled(false);
			return;
		}
		boolean enabled = false;
		Iterator iterator = sel.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof IServer) {
				IServer server = (IServer) obj;
				if (accept(server))
					enabled = true;
			} 
			else if (obj instanceof ModuleServer){
				ModuleServer ms = (ModuleServer) obj;
				if (accept(ms))
					enabled = true;
			}
			else {
				setEnabled(false);
				return;
			}
		}
		setEnabled(enabled);
	}

	public boolean accept(ModuleServer ms){
		if (ms.getServer() == null)
			return false;

		IStatus status = ms.getServer().canModifyModules(null,ms.module, null);  
		if (status.isOK())
			return true;

		return false;
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

	@Override
	public void run() {		
		IServer server = null;
		IModule[] moduleArray = null;
		
		IStructuredSelection sel = getStructuredSelection();
		// filter the selection
		if (!sel.isEmpty()) {
			Iterator iterator = sel.iterator();
			Object obj = iterator.next();
			if (obj instanceof IServer)
				server = (IServer) obj;
			if (obj instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) obj;
				server = ms.server; 
				moduleArray = ms.module;
			}
			// avoid no selection or multiple selection
			if (iterator.hasNext()) {
				server = null;
				moduleArray = null;
			}
		}
		
		// Perform actions
		if (server != null && moduleArray == null)
			deleteServer(server);
		
		if (moduleArray != null && moduleArray.length == 1) 
			new RemoveModuleAction(shell, server, moduleArray[0]).run();
		
	}
	
	protected void deleteServer(IServer server){
		DeleteServerDialog dsd = new DeleteServerDialog(shell, servers, configs);
		dsd.open();
	}
}
