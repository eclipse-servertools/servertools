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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;
/**
 * Job to publish to a particular server.
 */
public class PublishServerJob extends Job {
	protected IServer server;
	protected int kind;
	protected boolean check; 

	public PublishServerJob(IServer server, int kind, boolean check) {
		super(ServerPlugin.getResource("%publishing", server.getName()));
		this.server = server;
		this.kind = kind;
		this.check = check;
		setRule(new ServerSchedulingRule(server));
		if (kind != IServer.PUBLISH_AUTO)
			setUser(true);
	}

	public PublishServerJob(IServer server) {
		this(server, IServer.PUBLISH_INCREMENTAL, true);
	}

	/*
	 * Returns whether this job should be run.
	 */
	public boolean shouldRun() {
		if (!check)
			return true;
		return ServerPreferences.getInstance().isAutoPublishing() && ((Server)server).shouldPublish();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		return server.publish(kind, monitor);
	}
}