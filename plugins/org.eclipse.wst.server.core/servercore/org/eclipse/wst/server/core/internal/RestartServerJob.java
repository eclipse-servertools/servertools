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
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServer.IOperationListener;
import org.eclipse.wst.server.core.internal.ServerSchedulingRule;
/**
 * A job for restarting a server.
 */
public class RestartServerJob extends ChainedJob {
	protected String launchMode;
	protected boolean isRestartCompleted = false;
	protected IStatus resultStatus;

	public RestartServerJob(IServer server, String launchMode) {
		super(NLS.bind(Messages.jobRestartingServer, server.getName()), server);
		this.launchMode = launchMode;
		setRule(new ServerSchedulingRule(server));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		IOperationListener listener2 = new IOperationListener() {
			public void done(IStatus result) {
				isRestartCompleted = true;
				resultStatus = result;
			}
		};
		getServer().restart(launchMode, listener2);
		
		// block util the restart is completed
		while (!isRestartCompleted) {
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// Do nothing.
			}
		}
		
		if (resultStatus != null)
			return resultStatus;
		
		return Status.OK_STATUS;
	}
}