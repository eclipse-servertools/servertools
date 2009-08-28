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

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * This global delete action handles both the server and module deletion.
 */
public class GlobalDeleteAction extends SelectionProviderAction {
	protected Shell shell;
	private DeleteAction serverDeleteAction = null;
	private boolean isRemoveServer = false;

	public GlobalDeleteAction(Shell shell, ISelectionProvider selectionProvider) {
		super(selectionProvider, Messages.actionDelete);
		this.shell = shell;
		setEnabled(false);
		serverDeleteAction = new DeleteAction(shell, selectionProvider);
	}

	private boolean isRemoveModuleActionEnabled(IStructuredSelection sel) {
		if (sel == null || sel.isEmpty())
			return false;
			
		// get selection but avoid no selection or multiple selection
		IModule[] moduleArray = null;
		Iterator iterator = sel.iterator();
		Object obj = iterator.next();
		if (obj instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) obj;
			moduleArray = ms.module;
		}
		 
		if (iterator.hasNext()) {
			moduleArray = null;
		}
		
		return (moduleArray == null || moduleArray.length == 1);
	}

	/**
	 * Update the enabled state.
	 * 
	 * @param sel a selection
	 */
	public void selectionChanged(IStructuredSelection sel) {
		if (sel.isEmpty()) {
			setEnabled(false);
			return;
		}

		// Check if the delete action is enabled or not.
		serverDeleteAction.selectionChanged(sel);
		
		if (serverDeleteAction.isEnabled()) {
			// The server deletion action is already enabled.
			isRemoveServer = true;
			setEnabled(true);
			return;
		}
		
		// Check the remove module action.
		if (isRemoveModuleActionEnabled(sel)) {
			isRemoveServer = false;
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}
	
	public void run() {
		if (isRemoveServer) {
			// Run the server delete action;
			serverDeleteAction.run();
			return;
		}
		// get selection but avoid no selection or multiple selection
		IServer server = null;
		IModule[] moduleArray = null;
		IStructuredSelection sel = getStructuredSelection();
		if (!sel.isEmpty()) {
			Iterator iterator = sel.iterator();
			Object obj = iterator.next();
			
			// and the method calls serverDeleteAction, then returns
			if (obj instanceof IServer)
				server = (IServer) obj;
			if (obj instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) obj;
				server = ms.server; // even though ms.server is public, let's stick to the getServer call
				moduleArray = ms.module;
			}

			if (iterator.hasNext()) {
				server = null;
				moduleArray = null;
			}
		}
		
		if (moduleArray != null && moduleArray.length == 1) {
			new RemoveModuleAction(shell, server, moduleArray[0]).run();
		}
	}
}
