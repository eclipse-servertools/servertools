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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.wst.server.core.IServer;
/**
 * A Job that can start another job upon successful completion.
 */
public abstract class ChainedJob extends Job {
	protected IServer server;
	protected Job nextJob;
	protected IJobChangeListener listener;

	/**
	 * Create a new dependant job.
	 * 
	 * @param name the name of the job
	 * @param server the server to publish to
	 */
	public ChainedJob(String name, IServer server) {
		super(name);
		this.server = server;
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
		
		if (nextJob != null && status != null && status.getSeverity() != IStatus.ERROR)
			nextJob.schedule();
	}

	/**
	 * Set the next job that should run if this job completes successfully.
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