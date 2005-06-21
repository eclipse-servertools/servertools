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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
/**
 * Job to publish to a particular server.
 */
public class PublishServerJob extends Job {
	protected IServer server;
	protected int kind;
	protected boolean check; 

	/**
	 * Create a new publishing job.
	 * 
	 * @param server the server to publish to
	 * @param kind the kind of publish
	 * @param check
	 */
	public PublishServerJob(IServer server, int kind, boolean check) {
		super(NLS.bind(Messages.publishing, server.getName()));
		this.server = server;
		this.kind = kind;
		this.check = check;
		
		IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
		
		List list = new ArrayList();
		IModule[] modules = server.getModules();
		int size = modules.length;
		for (int i = 0; i < size; i++) {
			IProject project = modules[i].getProject();
			if (project != null) {
				ISchedulingRule rule = ruleFactory.createRule(project);
				if (rule != null && !list.contains(rule))
					list.add(rule);
				
				rule = ruleFactory.modifyRule(project);
				if (rule != null && !list.contains(rule))
					list.add(rule);
				
				rule = ruleFactory.validateEditRule(new IResource[] { project });
				if (rule != null && !list.contains(rule))
					list.add(rule);
				
				rule = ruleFactory.markerRule(project);
				if (rule != null && !list.contains(rule))
					list.add(rule);
				
				rule = ruleFactory.refreshRule(project);
				if (rule != null && !list.contains(rule))
					list.add(rule);
			}
		}
		
		size = list.size();
		ISchedulingRule[] rules = new ISchedulingRule[size + 1];
		for (int i = 0; i < size; i++)
			rules[i] = (ISchedulingRule) list.get(i);
		
		rules[size] = new ServerSchedulingRule(server);
		setRule(MultiRule.combine(rules));
		
		if (kind != IServer.PUBLISH_AUTO)
			setUser(true);
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
	 * @see Job#shouldRun()
	 */
	public boolean shouldRun() {
		if (!check)
			return true;
		return ServerPreferences.getInstance().isAutoPublishing() && ((Server)server).shouldPublish();
	}

	/**
	 * @see Job#run(IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		return server.publish(kind, monitor);
	}
}