/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
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
package org.eclipse.wst.server.core.model;

import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.TaskModel;
/**
 * An operation that will be executed during publishing. 
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @since 1.1
 */
public abstract class PublisherDelegate {
	private TaskModel model;

	/**
	 * Create a new operation. The label and description must be supplied
	 * by overriding the getLabel() and getDescription() methods.
	 */
	public PublisherDelegate() {
		// do nothing
	}

	/**
	 * Return the task model.
	 * <p>
	 * A task model contains information about the overall task flow and allows
	 * tasks to store and retrieve data. Its usage allows multiple tasks to be
	 * chained together and share data from the output of one task to the input
	 * of another.
	 * </p>
	 * 
	 * @return the task model
	 * @see #setTaskModel(TaskModel)
	 */
	public TaskModel getTaskModel() {
		return model;
	}

	/**
	 * Set the task model.
	 * <p>
	 * A task model contains information about the overall task flow and allows
	 * tasks to store and retrieve data. Its usage allows multiple tasks to be
	 * chained together and share data from the output of one task to the input
	 * of another.
	 * </p>
	 * 
	 * @param taskModel the task model
	 * @see #getTaskModel()
	 */
	public void setTaskModel(TaskModel taskModel) {
		this.model = taskModel;
	}

	/**
	 * Execute (perform) the operation.
	 * 
	 * @param kind the kind of publish being requested. Valid values are:
	 *    <ul>
	 *    <li><code>PUBLISH_FULL</code>- indicates a full publish.</li>
	 *    <li><code>PUBLISH_INCREMENTAL</code>- indicates a incremental publish.
	 *    <li><code>PUBLISH_CLEAN</code>- indicates a clean request. Clean throws
	 *      out all state and cleans up the module on the server before doing a
	 *      full publish.
	 *    </ul>
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @param info the IAdaptable (or <code>null</code>) provided by the
	 *    caller in order to supply UI information for prompting the
	 *    user if necessary. When this parameter is not
	 *    <code>null</code>, it should minimally contain an adapter
	 *    for the Shell class.
	 * @return status indicating what (if anything) went wrong
	 * @throws CoreException if there was an error while executing the task
	 */
	public abstract IStatus execute(int kind, IProgressMonitor monitor, IAdaptable info) throws CoreException;

	/**
	 * Returns the scheduling rule that is required for executing the publisher delegate. The default is the current
	 * rule defined on the publishing job.
	 * 
	 * @return A {@link ISchedulingRule} for the job that defines how this publisher can execute in the publishing job.
	 *         A <code>null</code> value may be returned if the publishing job does not have any rule defined.
	 * @since 3.2
	 */
	public ISchedulingRule getRule() {

		return Job.getJobManager().currentRule();
	}

	/**
	 * Accessor to find out if this publisher delegate modified any modules that are published on the server.
	 * 
	 * @return <code>true</code> if the publisher modified the contents of any modules that are published on the server.
	 * @since 3.2
	 */
	public boolean isModifyModules() {

		return false;
	}

	/**
	 * Accessor to acquire the list of {@link IModule} entries that were modified by the publisher.  By default, it will
	 * return all modules published to the server
	 * 
	 * @return An array of {@link IModule} entries that were modified by the publisher
	 * @since 3.2
	 */
	public List<IModule[]> getModifiedModules() {

		return (List<IModule[]>) this.model.getObject(TaskModel.TASK_MODULES);
	}
}