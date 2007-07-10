/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.StopServerJob;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.swt.widgets.Shell;
/**
 * Stop (terminate) a server.
 */
public class StopAction extends AbstractServerAction {
	public StopAction(Shell shell, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, Messages.actionStop);
		setToolTipText(Messages.actionStopToolTip);
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_STOP));
		setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_STOP));
		setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_STOP));
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
		if (server.getServerType() == null)
			return false;
		return server.getServerType() != null && server.canStop().isOK();
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void perform(final IServer server) {
		ServerUIPlugin.addTerminationWatch(shell, server, ServerUIPlugin.STOP);
		
		StopServerJob stopJob = new StopServerJob(server);
		stopJob.schedule();
	}
}