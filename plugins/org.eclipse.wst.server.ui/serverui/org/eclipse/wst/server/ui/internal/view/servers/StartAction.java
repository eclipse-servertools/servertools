/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.PublishServerJob;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.StartServerJob;
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
		return server.canStart(launchMode);
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
		
		IProgressMonitor pm = Platform.getJobManager().createProgressGroup();
		try {
			pm.beginTask("Starting", 10);
			PublishServerJob publishJob = new PublishServerJob(server); 
			publishJob.setProgressGroup(pm, 5);
			publishJob.schedule();
			StartServerJob startJob = new StartServerJob(server, launchMode);
			startJob.setProgressGroup(pm, 5);
			startJob.schedule();
		} catch (Exception e) {
			// ignore
		} finally {
			pm.done();
		}
	}
}