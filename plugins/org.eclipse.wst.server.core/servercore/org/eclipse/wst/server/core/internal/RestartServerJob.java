/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServer.IOperationListener;
import org.eclipse.wst.server.core.internal.ServerSchedulingRule;
/**
 * 
 */
public class RestartServerJob extends Job {
	protected IServer server;
	protected String launchMode;
	protected boolean isRestartCompleted = false;
	protected IStatus resultStatus;

	public RestartServerJob(IServer server, String launchMode) {
		super("Restart server");
		this.server = server;
		this.launchMode = launchMode;
		setRule(new ServerSchedulingRule(server));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		IOperationListener listener = new IOperationListener() {
			public void done(IStatus result) {
				isRestartCompleted = true;
				resultStatus = result;
			}
		};
		server.restart(launchMode, listener);
		
		// block util the restart is completed
		while (!isRestartCompleted) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// Do nothing.
			}
		}
		
		return resultStatus == null ? new Status(IStatus.OK, ServerPlugin.PLUGIN_ID, 0, "", null) : resultStatus;
	}
}