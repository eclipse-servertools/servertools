/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.view.servers;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

import org.eclipse.wst.server.core.IServer;
/**
 * An abstract class for an action on a server.
 */
public abstract class AbstractServerAction extends SelectionProviderAction {
	protected Shell shell;

	public AbstractServerAction(ISelectionProvider selectionProvider, String text) {
		this(null, selectionProvider, text);
	}

	public AbstractServerAction(Shell shell, ISelectionProvider selectionProvider, String text) {
		super(selectionProvider, text);
		this.shell = shell;
		setEnabled(false);
	}

	/**
	 * Return true if this server can currently be acted on.
	 *
	 * @return boolean
	 * @param server a server
	 */
	public boolean accept(IServer server) {
		return true;
	}

	/**
	 * Perform action on this server.
	 * 
	 * @param server a server
	 */
	public abstract void perform(IServer server);

	public void run() {
		Iterator iterator = getStructuredSelection().iterator();
		
		if (!iterator.hasNext())
			return;		
		
		Object obj = iterator.next();
		if (obj instanceof IServer) {
			IServer server = (IServer) obj;
			if (accept(server))
				perform(server);
			selectionChanged(getStructuredSelection());
		}
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
		boolean enabled = false;
		Iterator iterator = sel.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof IServer) {
				IServer server = (IServer) obj;
				if (accept(server))
					enabled = true;
			} else {
				setEnabled(false);
				return;
			}
		}
		setEnabled(enabled);
	}
}