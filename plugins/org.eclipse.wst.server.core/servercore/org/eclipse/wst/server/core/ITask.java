/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * This interface represents a task that can be executed (and then possibly
 * undone). To tie together multiple tasks, a common task model can be used
 * to pass parameters to the task or have the output of one task feed into
 * another task.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @since 1.0
 */
public interface ITask {
	/**
	 * Returns the displayable name for this task.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this task
	 */
	public String getName();

	/**
	 * Returns the displayable description for this task.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable description for this task
	 */
	public String getDescription();

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
	public TaskModel getTaskModel();

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
	public void setTaskModel(TaskModel taskModel);

	/**
	 * Execute (perform) the task.
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there was an error while executing the task
	 */
	public void execute(IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns whether the task can be undone.
	 * 
	 * @return <code>true</code> if the task can be undone, and 
	 *    <code>false</code> otherwise
	 */
	public boolean canUndo();
	
	/**
	 * Undo the task.
	 */
	public void undo();
}