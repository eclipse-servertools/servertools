/**********************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.swt.widgets.Shell;
/**
 * Clean publish to a server.
 */
public class PublishCleanAction extends AbstractServerAction {
	public PublishCleanAction(Shell shell, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, Messages.actionPublishClean);
		setToolTipText(Messages.actionPublishCleanToolTip);
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server a server
	 */
	public boolean accept(IServer server) {
		return server.canPublish().isOK();
	}

	/**
	 * Perform action on this server.
	 * @param server a server
	 */
	public void perform(IServer server) {
		if (!ServerUIPlugin.promptIfDirty(shell, server))
			return;
		
		if (!ServerUIPlugin.saveEditors())
			return;
		
		if (MessageDialog.openConfirm(shell, Messages.defaultDialogTitle, Messages.dialogPublishClean)) {
			IAdaptable info = new IAdaptable() {
				public Object getAdapter(Class adapter) {
					if (Shell.class.equals(adapter))
						return shell;
					return null;
				}
			};
			
			server.publish(IServer.PUBLISH_CLEAN, null, info, null);
		}
	}
}