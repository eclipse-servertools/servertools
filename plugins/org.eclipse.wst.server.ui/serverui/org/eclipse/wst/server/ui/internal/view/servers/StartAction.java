/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Shell;
/**
 * Start a server.
 */
public class StartAction extends AbstractServerAction {
	protected String launchMode = ILaunchManager.RUN_MODE;
	
	public StartAction(Shell shell, ISelectionProvider selectionProvider, String name, String launchMode) {
		super(shell, selectionProvider, name);
		this.launchMode = launchMode;
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
		return server.canStart(launchMode).isOK();
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void perform(final IServer server) {
		//if (!ServerUIUtil.promptIfDirty(shell, server))
		//	return;				
		
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