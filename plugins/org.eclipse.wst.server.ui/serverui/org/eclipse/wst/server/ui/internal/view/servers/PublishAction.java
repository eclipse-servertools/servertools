/**********************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.PublishServerJob;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.swt.widgets.Shell;
/**
 * Publish to a server.
 */
public class PublishAction extends AbstractServerAction {
	public PublishAction(Shell shell, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, Messages.actionPublish);
		setToolTipText(Messages.actionPublishToolTip);
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_PUBLISH));
		setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_PUBLISH));
		setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_PUBLISH));
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public boolean accept(IServer server) {
		return server.canPublish().isOK();
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void perform(IServer server) {
		if (!ServerUIPlugin.promptIfDirty(shell, server))
			return;
		
		if (!ServerUIPlugin.saveEditors())
			return;
		
		PublishServerJob publishJob = new PublishServerJob(server, IServer.PUBLISH_INCREMENTAL, false);
		publishJob.setUser(true);
		publishJob.schedule();
	}
}