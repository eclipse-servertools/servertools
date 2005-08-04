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

import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
/**
 * Job to publish to a particular server.
 */
public class PublishServerJob extends DependantJob {
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
		super(NLS.bind(Messages.publishing, server.getName()), server);
		this.kind = kind;
		this.check = check;
		
		IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
		
		// 102227 - lock entire workspace during publish
		// TODO will have to revisit post 0.7 to determine if there is a better way to
		// do this, since it would be preferrable not to lock the entire workspace, and
		// therefore allow multiple servers to publish at once
		
		// find all projects that modules are in
		/*List projectList = new ArrayList();
		Iterator iterator = ((Server)server).getAllModules().iterator();
		while (iterator.hasNext()) {
			IModule[] modules = (IModule[]) iterator.next();
			IProject project = modules[modules.length - 1].getProject();
			if (project != null && !projectList.contains(project))
				projectList.add(project);
		}
		
		// add dependant projects
		iterator = projectList.iterator();
		List depProjectList = new ArrayList(); // use to avoid concurrent modification
		while (iterator.hasNext()) {
			IProject project = (IProject) iterator.next();
			try {
				IProject[] refs = project.getDescription().getReferencedProjects();
				if (refs != null) {
					int size = refs.length;
					for (int i = 0; i < size; i++)
						if (refs[i] != null && !projectList.contains(refs[i]) && !depProjectList.contains(refs[i]))
							depProjectList.add(refs[i]);
				}
			} catch (CoreException ce) {
				Trace.trace(Trace.WARNING, "Could not compute referenced projects", ce);
			}
		}
		
		iterator = depProjectList.iterator();
		while (iterator.hasNext()) {
			projectList.add(iterator.next());
		}
		
		// combine and build all the rules
		List list = new ArrayList();
		iterator = projectList.iterator();
		while (iterator.hasNext()) {
			IProject project = (IProject) iterator.next();
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
		
		int size = list.size();
		ISchedulingRule[] rules = new ISchedulingRule[size + 1];
		for (int i = 0; i < size; i++)
			rules[i] = (ISchedulingRule) list.get(i);
		
		rules[size] = new ServerSchedulingRule(server);*/
		
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
	 * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
	 */
	public boolean shouldRun() {
		if (!super.shouldRun())
			return false;
		
		if (!check)
			return true;
		return ServerPreferences.getInstance().isAutoPublishing() && ((Server)server).shouldPublish();
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.Job#run(IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		return server.publish(kind, monitor);
	}
}