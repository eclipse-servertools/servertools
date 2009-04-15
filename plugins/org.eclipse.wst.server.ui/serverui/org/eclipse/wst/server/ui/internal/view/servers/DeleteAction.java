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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.editor.ServerEditor;
/**
 * Action for deleting server resources.
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
		boolean deleteUnsaved = false;
		ServerEditor serverEditor = null;
		IWorkbenchPage page = null;
				
		// Find the editor and prompt if it is dirty
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbench != null){
			page = workbench.getActivePage();
			IEditorPart editor = page.getActiveEditor();
			if (editor != null && editor instanceof ServerEditor) {
				serverEditor = (ServerEditor) editor;
				IServerWorkingCopy server2 = serverEditor.getServerWorkingCopy();
				deleteUnsaved = ServerUIPlugin.promptIfDirty(shell,(IServer)server2);			
			}
		}
		
		// delete and close the editor
		if (deleteUnsaved){ 
			DeleteServerDialog dsd = new DeleteServerDialog(shell, servers, configs);
			dsd.open();
			if (serverEditor != null && page != null)
				page.closeEditor(serverEditor, false);
		}
	}
}