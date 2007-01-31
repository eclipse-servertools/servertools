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
import org.eclipse.wst.server.core.internal.PublishServerJob;
import org.eclipse.wst.server.core.internal.ServerPreferences;
import org.eclipse.wst.server.core.internal.ServerType;
import org.eclipse.wst.server.core.internal.StartServerJob;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Shell;
/**
 * Start a server.
 */
public class StartAction extends AbstractServerAction {
	protected String launchMode = ILaunchManager.RUN_MODE;
	
	public StartAction(Shell shell, ISelectionProvider selectionProvider, String launchMode) {
		super(shell, selectionProvider, "start");
		this.launchMode = launchMode;
		if (launchMode == ILaunchManager.RUN_MODE) {
			setToolTipText(Messages.actionStartToolTip);
			setText(Messages.actionStart);
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
		} else if (launchMode == ILaunchManager.DEBUG_MODE) {
			setToolTipText(Messages.actionDebugToolTip);
			setText(Messages.actionDebug);
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
		} else if (launchMode == ILaunchManager.PROFILE_MODE) {
			setToolTipText(Messages.actionProfileToolTip);
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
	}

	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server a server
	 */
	public boolean accept(IServer server) {
		return server.canStart(launchMode).isOK();
	}

	/**
	 * Perform action on this server.
	 * @param server a server
	 */
	public void perform(IServer server) {
		if (!ServerUIPlugin.saveEditors())
			return;
		
		if (!ServerPreferences.getInstance().isAutoPublishing()) {
			StartServerJob startJob = new StartServerJob(server, launchMode);
			startJob.schedule();
			return;
		}
		
		try {
			PublishServerJob publishJob = new PublishServerJob(server, IServer.PUBLISH_INCREMENTAL, false); 
			StartServerJob startJob = new StartServerJob(server, launchMode);
			
			if (((ServerType)server.getServerType()).startBeforePublish()) {
				startJob.setNextJob(publishJob);
				startJob.schedule();
			} else {
				publishJob.setNextJob(startJob);
				publishJob.schedule();
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error starting server", e);
		}
	}
}