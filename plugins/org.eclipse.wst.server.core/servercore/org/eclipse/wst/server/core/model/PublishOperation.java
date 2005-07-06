/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.TaskModel;
/**
 * An publish operation that will be executed during publishing. 
 * 
 * [issue: EY It is not clear to me that when this task will be run. Will the place where the
 * task is being run depend on the server, e.g. the TDC tasks for the v6 server should be run
 * after the server is started the modules are added to the server; however, the EJB deploy task
 * makes more sense to be run before adding the project to the server.]
 * 
 * @plannedfor 1.0
 */
public abstract class PublishOperation {
	private TaskModel model;
	private String label;
	private String description;

   /**
	 * Operation kind constant (value 0) indicating that the operation
	 * does not need be executed.
	 * 
	 * @see #getKind()
	 */
	public static final int OPTIONAL = 0;

	/**
	 * Operation kind constant (value 1) indicating that the operation
	 * should be executed.
	 * 
	 * @see #getKind()
	 */
	public static final int PREFERRED = 1;

	/**
	 * Operation kind constant (value 2) indicating that the operation
	 * must be executed.
	 * 
	 * @see #getKind()
	 */
	public static final int REQUIRED = 2;

	/**
	 * Create a new operation. The label and description must be supplied
	 * by overriding the getLabel() and getDescription() methods.
	 */
	public PublishOperation() {
		// do nothing
	}

	/**
	 * Create a new operation with the given label and description.
	 * 
	 * @param label a translated label for the operation
	 * @param description the operation description
	 */
	public PublishOperation(String label, String description) {
		this.label = label;
		this.description = description;
	}

	/**
	 * Returns the kind of this operation. Operations can either be OPTIONAL
	 * (do not have to be executed), PREFERRED (should be executed), or
	 * REQUIRED (must be executed).
	 * 
	 * @return one of the kind constants (e.g. <code>REQUIRED</code>)
	 *    declared on {@link PublishOperation}
	 */
	public int getKind() {
		return OPTIONAL;
	}

	/**
	 * Returns the order (index/priority) of the task that will be run. The task with
	 * a smaller order value will be run before the task with a bigger order value.
	 * For tasks that have the same order value, the order of running those task are 
	 * not guaranteed.  
	 * 
	 * @return the order (index/priority) of the task that will be run.
	 */
	public abstract int getOrder();

	/**
	 * Returns the displayable label for this operation.
	 * <p>
	 * Note that this label is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable label for this operation
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the displayable description for this operation.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable description for this operation
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Return the task model.
	 * <p>
	 * A task model contains information about the overall task flow and allows
	 * tasks to store and retreive data. Its usage allows mutliple tasks to be
	 * chained together and share data from the output of one task to the input
	 * of another.
	 * </p>
	 * 
	 * @return the task model
	 */
	public TaskModel getTaskModel() {
		return model;
	}

	/**
	 * Set the task model.
	 * <p>
	 * A task model contains information about the overall task flow and allows
	 * tasks to store and retreive data. Its usage allows mutliple tasks to be
	 * chained together and share data from the output of one task to the input
	 * of another.
	 * </p>
	 * 
	 * @param taskModel the task model
	 */
	public void setTaskModel(TaskModel taskModel) {
		this.model = taskModel;
	}

	/**
	 * Execute (perform) the operation.
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @param info the IAdaptable (or <code>null</code>) provided by the
	 *    caller in order to supply UI information for prompting the
	 *    user if necessary. When this parameter is not
	 *    <code>null</code>, it should minimally contain an adapter
	 *    for the org.eclipse.swt.widgets.Shell.class.
	 * @throws CoreException if there was an error while executing the task
	 */
	public abstract void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException;
}