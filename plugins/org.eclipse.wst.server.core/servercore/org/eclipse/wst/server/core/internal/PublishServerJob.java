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

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
/**
 * Job to publish to a particular server.
 * 
 * @deprecated - use API directly, it will kick off jobs as required
 */
public class PublishServerJob extends ChainedJob {
	protected int kind;
	protected boolean check;
	protected List<IModule[]> modules;
	protected IAdaptable info;

	/**
	 * Create a new publishing job.
	 * 
	 * @param server the server to publish to
	 * @param kind the kind of publish
	 * @param info the IAdaptable (or <code>null</code>) provided by the
	 *    caller in order to supply UI information for prompting the
	 *    user if necessary. When this parameter is not
	 *    <code>null</code>, it should minimally contain an adapter
	 *    for the Shell class.
	 */
	public PublishServerJob(IServer server, int kind, IAdaptable info) {
		this(server, kind, null, info);
	}

	/**
	 * Create a new publishing job.
	 * 
	 * @param server the server to publish to
	 * @param kind the kind of publish
	 * @param modules a list of modules to publish, or <code>null</code> to
	 *    publish all modules
	 * @param info the IAdaptable (or <code>null</code>) provided by the
	 *    caller in order to supply UI information for prompting the
	 *    user if necessary. When this parameter is not
	 *    <code>null</code>, it should minimally contain an adapter
	 *    for the Shell class.
	 */
	public PublishServerJob(IServer server, int kind, List<IModule[]> modules, IAdaptable info) {
		super(NLS.bind(Messages.publishing, server.getName()), server);
		this.kind = kind;
		this.modules = modules;
		this.info = info;
	}

	/**
	 * Create a new publishing job.
	 * 
	 * @param server the server to publish to
	 * @param kind the kind of publish
	 * @param check to check if auto-publishing is already covering the publish
	 */
	public PublishServerJob(IServer server, int kind, boolean check) {
		this(server, kind, null, null);
		this.check = check;
	}

	/**
	 * Create a new publishing job.
	 * 
	 * @param server the server to publish to
	 * @deprecated use one of the other constructors instead
	 */
	public PublishServerJob(IServer server) {
		this(server, IServer.PUBLISH_INCREMENTAL, true);
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.Job#run(IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		//if (!getServer().shouldPublish())
		//	return Status.OK_STATUS;
		
		if (check && !ServerCore.isAutoPublishing()) {
			// don't run if we're auto-publishing and there is no need for a publish.
			// can't execute this code in shouldRun() because it will cancel the job
			// instead of returning immediately
				return Status.OK_STATUS;
		}
		
		final IStatus[] status = new IStatus[1];
		getServer().publish(kind, modules, info, new IServer.IOperationListener() {
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
		
		return Status.CANCEL_STATUS;
	}
}