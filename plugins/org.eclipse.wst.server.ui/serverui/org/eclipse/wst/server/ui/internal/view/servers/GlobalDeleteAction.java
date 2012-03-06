/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
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
import org.eclipse.wst.server.ui.internal.Trace;
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
		IStructuredSelection sel = getStructuredSelection();
		// filter the selection
		if (!sel.isEmpty()) {
			Object firstElement = sel.getFirstElement();
			if( sel.size() == 1 && firstElement instanceof IServer) {
				deleteServer((IServer)firstElement);
			} else {
				ArrayList<IModule> moduleList = getRemovableModuleList(sel);
				if( moduleList != null ) {
					IServer s = ((ModuleServer)firstElement).getServer();
					IModule[] asArray = moduleList.toArray(new IModule[moduleList.size()]);
					new RemoveModuleAction(shell, s, asArray).run();
				}
			}
		}
	}
	
	/*
	 * Return an arraylist of all IModules from this selection that 
	 * should be deleted, or null if the selection is invalid or requires
	 * no action be taken. 
	 */
	public static ArrayList<IModule> getRemovableModuleList(IStructuredSelection sel) {
		Iterator i = sel.iterator();
		IServer s = null;
		Object next = null;
		ArrayList<IModule> moduleList = new ArrayList<IModule>();
		while(i.hasNext()) {
			next = i.next();
			// If there is anything *not* a ModuleServer in the selection, do nothing
			if( !(next instanceof ModuleServer) || ((ModuleServer)next).getServer() == null) {
				return null;
			}
			if( s == null )
				s = ((ModuleServer)next).getServer();
			else if( !s.getId().equals(((ModuleServer)next).getServer().getId()))
				// Requests to remove modules under different servers should be ignored
				return null;  
			
			IModule[] nextMod = ((ModuleServer)next).getModule();
			if( nextMod == null || nextMod.length != 1 || nextMod[0] == null) 
				// If the module is a child module (ejb / war underneath an ear) ignore this request
				return null; 
			
			// Add the item to the list of removable modules
			moduleList.add(((ModuleServer)next).getModule()[0]);
		}
		// All modules are under the same server and may be removed. 
		return moduleList;
	}
	
	protected void deleteServer(IServer server){
		// It is possible that the server is created and added to the server view on workbench
		// startup. As a result, when the user switches to the server view, the server is 
		// selected, but the selectionChanged event is not called, which results in servers
		// being null. When servers is null the server will not be deleted and the error log
		// will have an IllegalArgumentException.
		//
		// To handle the case where servers is null, the selectionChanged method is called
		// to ensure servers will be populated.
		if (servers == null){
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Delete server called when servers is null");
			}				
			
			IStructuredSelection sel = getStructuredSelection();
			if (sel != null){
				selectionChanged(sel);
			}
		}
				
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Opening delete server dialog with parameters shell="
					+ shell + " servers=" + servers + " configs=" + configs);
		}		
		
		// No check is made for valid parameters at this point, since if there is a failure, it
		// should be output to the error log instead of failing silently.
		DeleteServerDialog dsd = new DeleteServerDialog(shell, servers, configs);
		dsd.open();
	}
}
