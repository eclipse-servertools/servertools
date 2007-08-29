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
package org.eclipse.wst.server.ui.internal.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.view.servers.DeleteAction;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
/**
 * 
 */
public class DeleteActionDelegate implements IWorkbenchWindowActionDelegate {
	protected IServer[] servers;
	protected Shell shell;

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		if (window != null)
			shell = window.getShell();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (action != null && action.isEnabled() && servers != null)
			return;
		DeleteAction delete = new DeleteAction(shell, servers);
		delete.run();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		servers = null;
		if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			action.setEnabled(false);
			return;
		}

		IStructuredSelection select = (IStructuredSelection) selection;
		Iterator iterator = select.iterator();
		
		List<IServer> list = new ArrayList<IServer>();
		
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof IServer)
				list.add((IServer)obj);
			else {
				action.setEnabled(false);
				return;
			}
		}
		
		servers = new IServer[list.size()];
		list.toArray(servers);
		action.setEnabled(true);
	}
}