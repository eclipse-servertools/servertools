/*******************************************************************************
 * Copyright (c) 2007,2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * "Paste" menu action.
 */
public class PasteAction extends SelectionProviderAction {
	private Clipboard clipboard;

	/**
	 * PasteAction constructor.
	 * 
	 * @param shell a shell;
	 * @param sp a selection provider
	 * @param clipboard the clipboard
	 */
	public PasteAction(Shell shell, ISelectionProvider sp, Clipboard clipboard) {
		super(sp, Messages.actionPaste);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		setActionDefinitionId(IWorkbenchActionDefinitionIds.PASTE);
		
		this.clipboard = clipboard;
	}

	/**
	 * Update the enabled state.
	 * 
	 * @param sel a selection
	 */
	public void selectionChanged(IStructuredSelection sel) {
		ServerTransfer serverTransfer = ServerTransfer.getInstance();
		IServer[] servers = (IServer[]) clipboard.getContents(serverTransfer);
		setEnabled(servers != null && servers.length > 0);
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		ServerTransfer serverTransfer = ServerTransfer.getInstance();
		IServer[] servers = (IServer[]) clipboard.getContents(serverTransfer);
		
		if (servers == null)
			return;
		
		int size = servers.length;
		for (int i = 0; i < size; i++) {
			try {
				IServerWorkingCopy wc = servers[i].createWorkingCopy();
				((ServerWorkingCopy)wc).disassociate();
				wc.setName("Temp"); // sets the name from the current one so that the
						// default name generation will work
				ServerUtil.setServerDefaultName(wc);
				wc.save(false, null);
			} catch (CoreException ce) {
				Trace.trace(Trace.SEVERE, "Failure to copy server", ce);
			}
		}
	}
}