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
	 * Returns the label for this command.
	 *
	 * @return java.lang.String
	 */
	public String getName();

	/**
	 * Returns a description of this command.
	 *
	 * @return java.lang.String
	 */
	public String getDescription();

	/**
	 * Return the task model.
	 * 
	 * @return the task model
	 */
	public ITaskModel getTaskModel();

	/**
	 * Set the task model.
	 * 
	 * @param taskModel the task model
	 */
	public void setTaskModel(ITaskModel taskModel);

	/**
	 * Returns whether the task can be executed.
	 * 
	 * @return <code>true</code> if the task can be executed, and 
	 *    <code>false</code> otherwise
	 */
	public boolean canExecute();

	/**
	 * Execute (perform) the task.
	 * 
	 * @param monitor
	 * @throws CoreException
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