/*******************************************************************************
 * Copyright (c) 2009, 2013 IBM Corporation and others.
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
import org.eclipse.jface.viewers.TreeViewer;
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
	private TreeViewer viewer;

	public GlobalDeleteAction(TreeViewer viewer, ISelectionProvider selectionProvider) {
		super(selectionProvider, Messages.actionDelete);
		this.viewer = viewer;
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

		IServer[] servers = getAcceptedServers(sel);
		if (servers != null && servers.length > 0) {
			setEnabled(true);
			return;
		}
		// selection isn't servers. If selection is only module-servers, then
		// its acceptable
		setEnabled(allSelectionAreModuleServer(sel));
	}

	private boolean allSelectionAreModuleServer(IStructuredSelection sel) {
		Iterator iterator = sel.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (!(obj instanceof ModuleServer))
				return false;
			ModuleServer ms = (ModuleServer) obj;
			if (!accept(ms))
				return false;
		}
		return true;
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
		return !server.isReadOnly();
	}

	@Override
	public void run() {	
		IStructuredSelection sel = getStructuredSelection();
		// filter the selection
		if (!sel.isEmpty()) {
			IServer[] selAsServers = getAcceptedServers(sel);
			if( selAsServers != null) {
				deleteServers(selAsServers);
			} else {
				ArrayList<IModule> moduleList = getRemovableModuleList(sel);
				if( moduleList != null ) {
					IServer s = ((ModuleServer)sel.getFirstElement()).getServer();
					IModule[] asArray = moduleList.toArray(new IModule[moduleList.size()]);
					new RemoveModuleAction(getShell(), s, asArray).run();
				}
			}
		}
	}
	
	private Shell getShell() {
		if (viewer != null && !viewer.getControl().isDisposed()) {
			return viewer.getTree().getShell();
		}
		return null;
	}
	
	private IFolder[] getConfigurationsFor(IServer[] serverArr) {
		List<IFolder> list = new ArrayList<IFolder>();
		for( int i = 0; i < serverArr.length; i++ ) {
			if (serverArr[i].getServerConfiguration() != null) {
				list.add(serverArr[i].getServerConfiguration());
			}
		}
		
		// remove configurations that are still referenced by other servers
		IServer[] servers2 = ServerCore.getServers();
		if (servers2 != null) {
			int size2 = servers2.length;
			for (int j = 0; j < size2; j++) {
				boolean found = false;
				for (int i = 0; i < serverArr.length; i++) {
					if (serverArr[i].equals(servers2[j]))
						found = true;
				}
				if (!found) {
					IFolder folder = servers2[j].getServerConfiguration();
					if (folder != null && list.contains(folder))
						list.remove(folder);
				}
			}
		}
		return list.toArray(new IFolder[list.size()]);
	}

	/*
	 * get an array of IServer if all elements in the selection are IServer.
	 * Otherwise, return null.
	 */
	private IServer[] getAcceptedServers(IStructuredSelection sel) {
		ArrayList<IServer> l = new ArrayList<IServer>();
		Iterator i = sel.iterator();
		Object o = null;
		while (i.hasNext()) {
			o = i.next();
			if (!(o instanceof IServer))
				return null;
			// Do not add it if its read only
			if (!((IServer) o).isReadOnly())
				l.add((IServer) o);
		}
		return l.toArray(new IServer[l.size()]);
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
		deleteServers(new IServer[]{server});
	}
	
	protected void deleteServers(IServer[] servers){
		
		// No check is made for valid parameters at this point, since if there is a failure, it
		// should be output to the error log instead of failing silently.
		DeleteServerDialog dsd = new DeleteServerDialog(getShell(), servers, getConfigurationsFor(servers));
		dsd.open();
	}
}
