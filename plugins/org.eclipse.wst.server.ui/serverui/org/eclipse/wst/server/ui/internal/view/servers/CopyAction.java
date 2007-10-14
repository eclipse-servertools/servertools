/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * "Copy" menu action.
 */
public class CopyAction extends AbstractServerAction {
	protected Action pasteAction;
	private Clipboard clipboard;

	/**
	 * CopyAction constructor.
	 * 
	 * @param sp a selection provider
	 * @param clipboard the clipboard
	 * @param pasteAction the paste action
	 */
	public CopyAction(ISelectionProvider sp, Clipboard clipboard, Action pasteAction) {
		super(sp, Messages.actionCopy);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		setActionDefinitionId(IWorkbenchActionDefinitionIds.COPY);
		
		this.clipboard = clipboard;
		this.pasteAction = pasteAction;
	}

	public boolean accept(IServer server) {
		return server != null && server.getServerType() != null;
	}

	public void perform(IServer server) {
		clipboard.setContents(new Object[] { new IServer[] { server }, server.getName() },
			new Transfer[] { ServerTransfer.getInstance(), TextTransfer.getInstance() });
		pasteAction.setEnabled(true);
	}
}