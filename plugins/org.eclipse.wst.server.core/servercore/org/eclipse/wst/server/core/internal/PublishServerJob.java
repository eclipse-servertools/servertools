/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
/**
 * Job to publish to a particular server.
 */
public class PublishServerJob extends ChainedJob {
	protected int kind;
	protected boolean check;

	/**
	 * Create a new publishing job.
	 * 
	 * @param server the server to publish to
	 * @param kind the kind of publish
	 * @param check to check if autopublishing is already covering the publish
	 */
	public PublishServerJob(IServer server, int kind, boolean check) {
		super(NLS.bind(Messages.publishing, server.getName()), server);
		this.kind = kind;
		this.check = check;
		
		IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
		
		// 102227 - lock entire workspace during publish		
		ISchedulingRule[] rules = new ISchedulingRule[2];
		rules[0] = ruleFactory.createRule(ResourcesPlugin.getWorkspace().getRoot());
		rules[1] = new ServerSchedulingRule(server);
		
		setRule(MultiRule.combine(rules));
	}

	/**
	 * Create a new publishing job.
	 * 
	 * @param server the server to publish to
	 */
	public PublishServerJob(IServer server) {
		this(server, IServer.PUBLISH_INCREMENTAL, true);
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.Job#run(IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		if (check) {
			// don't run if we're auto-publishing and there is no need for a publish.
			// can't execute this code in shouldRun() because it will cancel the job
			// instead of returning immediately
			if (!ServerCore.isAutoPublishing() || !getServer().shouldPublish())
				return Status.OK_STATUS;
		}
		
		return getServer().publish(kind, monitor);
	}
}