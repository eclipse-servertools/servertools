/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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
 * A job for stopping the server.
 * 
 * @deprecated - use API directly, it will kick off jobs as required
 */
public class StopServerJob extends ChainedJob {
	public StopServerJob(IServer server) {
		super(NLS.bind(Messages.jobStopping, server.getName()), server);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		final IStatus[] status = new IStatus[1];
		getServer().stop(false, new IServer.IOperationListener() {
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
		return status[0];
	}
}
