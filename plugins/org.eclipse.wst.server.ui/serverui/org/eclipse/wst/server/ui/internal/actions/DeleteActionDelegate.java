/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.view.servers.DeleteAction;
import org.eclipse.wst.server.ui.internal.view.tree.ServerElementAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
/**
 * 
 */
public class DeleteActionDelegate implements IWorkbenchWindowActionDelegate {
	protected Object[] resources;
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
		if (action != null && action.isEnabled() && resources != null)
			return;
		DeleteAction delete = new DeleteAction(shell, resources);
		delete.run();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		resources = null;
		if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			action.setEnabled(false);
			return;
		}

		IStructuredSelection select = (IStructuredSelection) selection;
		Iterator iterator = select.iterator();
		
		List list = new ArrayList();
		
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof IRuntime)
				list.add(obj);
			else if (obj instanceof IServer)
				list.add(obj);
			else if (obj instanceof ServerElementAdapter)
				list.add(((ServerElementAdapter) obj).getObject());
			else {
				action.setEnabled(false);
				return;
			}
		}
		
		resources = new Object[list.size()];
		list.toArray(resources);
		action.setEnabled(true);
	}
}