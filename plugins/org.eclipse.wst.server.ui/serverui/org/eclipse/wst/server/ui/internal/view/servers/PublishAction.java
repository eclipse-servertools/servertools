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
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.swt.widgets.Shell;
/**
 * Publish to a server.
 */
public class PublishAction extends AbstractServerAction {
	public PublishAction(Shell shell, ISelectionProvider selectionProvider, String name) {
		super(shell, selectionProvider, name);
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public boolean accept(IServer server) {
		return server.canPublish();
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public void perform(final IServer server) {
		if (!ServerUIUtil.promptIfDirty(shell, server))
			return;
		
		if (!ServerUIUtil.saveEditors())
			return;

		ServerUIUtil.publishWithDialog(shell, server);
	}
}