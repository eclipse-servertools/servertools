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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;
/**
 * A Job that is potentially dependant on the completion of another Job.
 */
public abstract class DependantJob extends Job {
	protected IServer server;
	protected Job dependantJob;

	/**
	 * Create a new dependant job.
	 * 
	 * @param name the name of the job
	 * @param server the server to publish to
	 */
	public DependantJob(String name, IServer server) {
		super(name);
		this.server = server;
	}

	/**
	 * @see Job#shouldRun()
	 */
	public boolean shouldRun() {
		if (dependantJob != null) {
			IStatus status = dependantJob.getResult();
			if (!status.isOK())
				return false;
		}
		
		return true;
	}

	/**
	 * Set the job that this job is dependant on completion for.
	 * 
	 * @param job a job
	 */
	public void setDependantJob(Job job) {
		dependantJob = job;
	}
}