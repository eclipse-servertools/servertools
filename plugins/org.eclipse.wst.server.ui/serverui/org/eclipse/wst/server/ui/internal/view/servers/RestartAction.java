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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.PublishServerJob;
import org.eclipse.wst.server.ui.internal.RestartServerJob;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.provisional.UIDecoratorManager;
import org.eclipse.swt.widgets.Shell;
/**
 * Restart a server.
 */
public class RestartAction extends AbstractServerAction {
	protected String mode;

	public RestartAction(Shell shell, ISelectionProvider selectionProvider, String name) {
		this(shell, selectionProvider, name, null);
	}

	public RestartAction(Shell shell, ISelectionProvider selectionProvider, String name, String mode) {
		super(shell, selectionProvider, name);
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
		
		IProgressMonitor pm = Platform.getJobManager().createProgressGroup();
		try {
			pm.beginTask("Restarting", 100);
			PublishServerJob publishJob = new PublishServerJob(server); 
			publishJob.setProgressGroup(pm, 50);
			publishJob.schedule();
			String launchMode = mode;
			if (launchMode == null)
				launchMode = server.getMode();
			RestartServerJob restartJob = new RestartServerJob(server, launchMode);
			restartJob.setProgressGroup(pm, 50);
			restartJob.schedule();
		} catch (Exception e) {
			// ignore
		} finally {
			pm.done();
		}	
	}
}