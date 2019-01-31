/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
/**
 * 
 */
public abstract class AbstractServerActionDelegate implements IActionDelegate {
	protected List<IServer> servers;

	/**
	 * Return true if this server can currently be acted on.
	 *
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public abstract boolean accept(IServer server);

	/**
	 * Perform action on this server.
	 * 
	 * @param shell a shell
	 * @param server a server
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
		servers = new ArrayList<IServer>();
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
