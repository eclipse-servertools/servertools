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
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.ServerSchedulingRule;
/**
 * 
 */
public class StartServerJob extends Job {
	protected IServer server;
	protected String launchMode;
	
	public static void startServer(IServer server, String launchMode) {
		StartServerJob job = new StartServerJob(server, launchMode);
		//job.setUser(true);
		job.schedule();
	}

	public StartServerJob(IServer server, String launchMode) {
		super("Starting server");
		this.server = server;
		this.launchMode = launchMode;
		setRule(new ServerSchedulingRule(server));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		IStatus status = new Status(IStatus.OK, ServerUIPlugin.PLUGIN_ID, 0, "", null);
		try {
			server.synchronousStart(launchMode, monitor);
		} catch (CoreException ce) {
			return ce.getStatus();
		}
		return status;
	}
}