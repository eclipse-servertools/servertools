/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.model.PublisherDelegate;
/**
 * 
 */
public class Publisher {
	private IConfigurationElement element;
	private PublisherDelegate delegate;
	private boolean modifyModules = false;

	/**
	 * Publisher constructor comment.
	 * 
	 * @param element a configuration element 
	 */
	public Publisher(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/*
	 * @see
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	public String getName() {
		return element.getAttribute("name");
	}

	public String getDescription() {
		return element.getAttribute("description");
	}

	protected String[] getTypeIds() {
		try {
			return ServerPlugin.tokenize(element.getAttribute("typeIds"), ",");
		} catch (Exception e) {
			return null;
		}
	}

	public boolean supportsType(String id) {
		return ServerPlugin.contains(getTypeIds(), id);
	}

	/*
	 * @see IPublisher#getDelegate()
	 */
	public PublisherDelegate getDelegate() {
		if (delegate == null) {
			try {
				long time = System.currentTimeMillis();
				delegate = (PublisherDelegate) element.createExecutableExtension("class");
				Trace.trace(Trace.PERFORMANCE, "PublishTask.getDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getId());
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate" + toString(), t);
			}
		}
		return delegate;
	}

	/**
	 * Should the original {@link ISchedulingRule} be changed with the new {@link ISchedulingRule}?
	 * 
	 * @param originalRule
	 *            The original {@link ISchedulingRule}
	 * @param newRule
	 *            The new {@link ISchedulingRule}
	 * @return <code>true</code> if the new scheduling rule should be applied; Otherwise <code>false</code>.
	 */
	private boolean changeSchedulingRule(ISchedulingRule originalRule, ISchedulingRule newRule) {

		boolean changeRule = false;
		if ((originalRule == null) && (newRule == null)) {
			// no need to change rules if they're both null
			changeRule = false;
		}
		else if((originalRule == null) && (newRule != null)) {
			// there is currently no rule and a new not-null rule wants to be added 
			changeRule = true;
		}
		else if((originalRule != null) && (newRule == null)) {
			// there is currently a rule and a new null rule wants to be applied
			changeRule = true;
		}
		else if((originalRule != null) && (newRule != null)) {
			// there is currently a rule and a new not-null rule wants to be applied.
			changeRule = !originalRule.equals(newRule);
		}
		return changeRule;
	}

	/**
	 * rebuild the cache for the modules involved with this task.
	 */
	private void rebuildModuleCache() {

		// reset the publishing cache for the modules that are part of this task.
		Server server = (Server) getDelegate().getTaskModel().getObject(TaskModel.TASK_SERVER);
		if (server != null) {
			// make sure the right server is used.
			if(server.isWorkingCopy()) {
				IServerWorkingCopy workingCopy = (IServerWorkingCopy)server;
				server = (Server) workingCopy.getOriginal();
			}
			final List<IModule[]> moduleList = getDelegate().getModifiedModules();
			if (moduleList != null) {
				final Iterator<IModule[]> moduleIterator = moduleList.iterator();
				while (moduleIterator.hasNext()) {
					IModule[] module = moduleIterator.next();
					if (module != null) {
						Trace.trace(Trace.FINEST, "rebuilding cache for module: " + module[module.length - 1]);
						server.getServerPublishInfo().rebuildCache(module);
					}
				}
			}
		}
	}
	
	public IStatus execute(int kind, IProgressMonitor monitor, IAdaptable info) throws CoreException {

		Trace.trace(Trace.FINEST, "Task.init " + this);
		ISchedulingRule delegatePublisherRule = null;
		final ISchedulingRule originalPublisherRule = Job.getJobManager().currentRule();
		IStatus resultStatus = null;
		boolean changeSchedulingRules = false;
		try {
			delegatePublisherRule = getDelegate().getRule();
			changeSchedulingRules = this.changeSchedulingRule(originalPublisherRule, delegatePublisherRule);
			Trace.trace(Trace.FINEST, "Change the scheduling rule to execute delegate: " + changeSchedulingRules);
			if (changeSchedulingRules) {
				Trace.trace(Trace.FINEST, "Ending the current scheduling rule " + originalPublisherRule);
				Job.getJobManager().endRule(originalPublisherRule);
				Trace.trace(Trace.FINEST, "Beginning the new scheduling rule: " + delegatePublisherRule);
				Job.getJobManager().beginRule(delegatePublisherRule, monitor);
			}
			resultStatus = getDelegate().execute(kind, monitor, info);
			this.modifyModules = getDelegate().isModifyModules();
			Trace.trace(Trace.FINEST, "The publisher delegate stated that it modified modules: " + this.modifyModules);
			if(this.modifyModules) {
				this.rebuildModuleCache();
			}
		}
		catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), e);
			resultStatus = new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, "Error in delegate", e);
		}
		finally {
			if (changeSchedulingRules) {
				Trace.trace(Trace.FINEST, "Reseting the scheduling rules... ending: " + delegatePublisherRule);
				Job.getJobManager().endRule(delegatePublisherRule);
				Trace.trace(Trace.FINEST, "Reseting the scheduling rules... beginning: " + originalPublisherRule);
				Job.getJobManager().beginRule(originalPublisherRule, monitor);
			}
		}
		return resultStatus;
	}

	public void setTaskModel(TaskModel taskModel) {
		try {
			getDelegate().setTaskModel(taskModel);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), e);
		}
	}

	/**
	 * Accessor to find out if this publisher modified any modules that are published on the server.
	 * 
	 * @return <code>true</code> if the publisher modified the contents of any modules that are published on the server.
	 */
	public boolean isModifyModules() {

		return this.modifyModules;
	}
	
	/**
	 * Return a string representation of this object.
	 * 
	 * @return a string
	 */
	public String toString() {
		return "Publisher[" + getId() + "]";
	}
}