/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
/**
 * 
 */
public class PublishServerJob extends Job {
	protected IServer server;

	public PublishServerJob(IServer server) {
		super(ServerUIPlugin.getResource("%publishingJob", server.getName()));
		this.server = server;
		setRule(new ServerSchedulingRule(server));
		setUser(true);
	}

	/*
	 * Returns whether this job should be run.
	 */
	public boolean shouldRun() {
		return ServerCore.getServerPreferences().isAutoPublishing() && server.shouldPublish();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		return server.publish(IServer.PUBLISH_INCREMENTAL, monitor);
	}
}