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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
/**
 * 
 */
public abstract class AbstractServerActionDelegate implements IActionDelegate {
	protected List servers;

	/**
	 * Return true if this server can currently be acted on.
	 *
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public abstract boolean accept(IServer server);

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public abstract void perform(Shell shell, IServer server);

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		Shell shell = EclipseUtil.getShell();
		Iterator iterator = servers.iterator();
		Object obj = iterator.next();
		if (obj instanceof IServer) {
			IServer server = (IServer) obj;
			if (accept(server))
				perform(shell, server);
			selectionChanged(action, new StructuredSelection(servers));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		servers = new ArrayList();
		if (selection.isEmpty() || !(selection instanceof StructuredSelection)) {
			action.setEnabled(false);
			return;
		}
		
		boolean enabled = false;
		Iterator iterator = ((StructuredSelection) selection).iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof IServer) {
				IServer server = (IServer) obj;
				if (accept(server)) {
					servers.add(server);
					enabled = true;
				}
			} else {
				action.setEnabled(false);
				return;
			}
		}
		action.setEnabled(enabled);
	}
}