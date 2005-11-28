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
import org.eclipse.wst.server.core.internal.ServerSchedulingRule;
/**
 * A job for starting the server.
 */
public class StartServerJob extends ChainedJob {
	protected String launchMode;

	public StartServerJob(IServer server, String launchMode) {
		super(NLS.bind(Messages.jobStartingServer, server.getName()), server);
		this.launchMode = launchMode;
		setRule(new ServerSchedulingRule(server));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		try {
			getServer().synchronousStart(launchMode, monitor);
		} catch (CoreException ce) {
			return ce.getStatus();
		}
		return Status.OK_STATUS;
	}
}