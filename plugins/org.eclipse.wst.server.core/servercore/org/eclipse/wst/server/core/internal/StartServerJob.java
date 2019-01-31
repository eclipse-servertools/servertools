/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
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
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
/**
 * A job for starting the server.
 * 
 * @deprecated - use API directly, it will kick off jobs as required
 */
public class StartServerJob extends ChainedJob {
	protected String launchMode;

	public StartServerJob(IServer server, String launchMode) {
		super(NLS.bind(Messages.jobStarting, server.getName()), server);
		this.launchMode = launchMode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		final IStatus[] status = new IStatus[1];
		getServer().start(launchMode, new IServer.IOperationListener() {
			public void done(IStatus result) {
				status[0] = result;
			}
		});
		
		while (status[0] == null & !monitor.isCanceled()) {
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				// ignore
			}
		}
		if (status[0] != null)
			return status[0];
		
		getServer().stop(true, null);
		return Status.CANCEL_STATUS;
	}
}