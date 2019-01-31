/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.Server;
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
		setActionDefinitionId("org.eclipse.wst.server.stop");
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
		if (server.getServerType() == null)
			return false;
		return server.getServerType() != null && server.canStop().isOK();
	}

	/**
	 * Perform action on this server.
	 * @param server a server
	 */
	public void perform(IServer server) {
		stop(server, shell);
	}

	public static void stop(IServer server, Shell shell) {
		ServerUIPlugin.addTerminationWatch(shell, server, ServerUIPlugin.STOP); // TODO - should redo
		
		IJobManager jobManager = Job.getJobManager();
		Job[] jobs = jobManager.find(ServerUtil.SERVER_JOB_FAMILY);
		for (Job j: jobs) {
			if (j instanceof Server.StartJob) {
				Server.StartJob startJob = (Server.StartJob) j;
				if (startJob.getServer().equals(server)) {
					startJob.cancel();
					return;
				}
			}
		}
		
		server.stop(false);
	}
}