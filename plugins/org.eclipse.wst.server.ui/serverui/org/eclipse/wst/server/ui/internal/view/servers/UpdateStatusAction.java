/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.*;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ProgressUtil;
/**
 * Action to update a server's status.
 */
public class UpdateStatusAction extends Action {
	protected IServer[] servers;

	/**
	 * An action to update the status of a server.
	 * 
	 * @param server a server
	 */
	public UpdateStatusAction(IServer server) {
		this(new IServer[] { server });
	}

	/**
	 * An action to update the status of a server.
	 * 
	 * @param servers an array of servers
	 */
	public UpdateStatusAction(IServer[] servers) {
		super(Messages.actionUpdateStatus);
		this.servers = servers;
	}

	/**
	 * Invoked when an action occurs. 
	 */
	public void run() {
		class UpdateStatusJob extends Job {
			public UpdateStatusJob() {
				super(Messages.jobUpdateStatus);
			}

			public IStatus run(IProgressMonitor monitor) {
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					servers[i].loadAdapter(ServerBehaviourDelegate.class, ProgressUtil.getSubMonitorFor(monitor, 100));
				}
				
				return new Status(IStatus.OK, ServerPlugin.PLUGIN_ID, 0, "", null);
			}
		}
		UpdateStatusJob job = new UpdateStatusJob();
		job.schedule();
	}
}