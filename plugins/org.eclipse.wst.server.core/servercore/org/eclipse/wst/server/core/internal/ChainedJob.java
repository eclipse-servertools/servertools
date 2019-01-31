/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * A Job that can start another job upon successful completion.
 */
public abstract class ChainedJob extends Job {
	private IServer server;
	private Job nextJob;
	private IJobChangeListener listener;

	/**
	 * Create a new dependent job.
	 * 
	 * @param name the name of the job
	 * @param server the server to publish to
	 */
	public ChainedJob(String name, IServer server) {
		super(name);
		this.server = server;
	}

	/**
	 * @see Job#belongsTo(java.lang.Object)
	 */
	public boolean belongsTo(Object family) {
		return ServerUtil.SERVER_JOB_FAMILY.equals(family);
	}

	/**
	 * Returns the server that this job was created with.
	 * 
	 * @return a server
	 */
	public IServer getServer() {
		return server;
	}

	/**
	 * Create a listener for when this job finishes.
	 */
	protected void createListener() {
		if (listener != null)
			return;
		
		listener = new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				jobDone(event.getResult());
			}
		};
		
		addJobChangeListener(listener);
	}

	/**
	 * Called when this job is complete.
	 * 
	 * @param status the result of the current job
	 */
	protected void jobDone(IStatus status) {
		if (listener == null)
			return;
		
		removeJobChangeListener(listener);
		listener = null;
		
		if (nextJob != null && status != null && status.getSeverity() != IStatus.ERROR
				&& status.getSeverity() != IStatus.CANCEL)
			nextJob.schedule();
	}

	/**
	 * Set the next job, which should be scheduled if and only if this job completes
	 * successfully. The next job will be run as long as the result of this job is
	 * not an ERROR or CANCEL status.
	 * This method is not thread-safe. However, the next job can be changed anytime
	 * up until the current job completes.
	 * 
	 * @param job the next job that should be scheduled
	 */
	public void setNextJob(Job job) {
		nextJob = job;
		createListener();
	}
}