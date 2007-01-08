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

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.RestartServerJob;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.provisional.UIDecoratorManager;
import org.eclipse.swt.widgets.Shell;
/**
 * Restart a server.
 */
public class RestartAction extends AbstractServerAction {
	protected String mode;

	public RestartAction(Shell shell, ISelectionProvider selectionProvider, String mode) {
		super(shell, selectionProvider, "restart");
		if (mode == ILaunchManager.RUN_MODE) {
			setToolTipText(Messages.actionRestartToolTip);
			setText(Messages.actionStart);
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
		} else if (mode == ILaunchManager.DEBUG_MODE) {
			setToolTipText(Messages.actionDebugToolTip);
			setText(Messages.actionDebug);
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
		} else if (mode == ILaunchManager.PROFILE_MODE) {
			setToolTipText(Messages.actionRestartToolTip);
			setText(Messages.actionProfile);
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_PROFILE));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_PROFILE));
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_PROFILE));
		}
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
		this.mode = mode;
	}

	/**
	 * Return true if this server can currently be acted on.
	 *
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public boolean accept(IServer server) {
		String mode2 = mode;
		if (mode2 == null)
			mode2 = server.getMode();
		return server.getServerType() != null && UIDecoratorManager.getUIDecorator(server.getServerType()).canRestart() && server.canRestart(mode2).isOK();
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void perform(IServer server) {
		if (!ServerUIPlugin.promptIfDirty(shell, server))
			return;
		
		try {
			String launchMode = mode;
			if (launchMode == null)
				launchMode = server.getMode();
			RestartServerJob restartJob = new RestartServerJob(server, launchMode);
			restartJob.schedule();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error restarting server", e);
		}	
	}
}