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
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.*;
/**
 * An abstract implementation of the org.eclipse.wst.server.core.ITask interface
 * that provides default implementation of the methods.
 * 
 * @since 1.0
 */
public abstract class Task implements ITask {
	private TaskModel model;
	private String name;
	private String description;

	/**
	 * Create a new task with no name or description.
	 */
	public Task() {
		// do nothing
	}
	
	/**
	 * Create a new task with the given label and description.
	 * 
	 * @param name the task name
	 * @param description the task description
	 */
	public Task(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	/**
	 * @see ITask#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see ITask#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see ITask#getTaskModel()
	 */
	public TaskModel getTaskModel() {
		return model;
	}
	
	/**
	 * @see ITask#setTaskModel(TaskModel)
	 */
	public void setTaskModel(TaskModel taskModel) {
		this.model = taskModel;
	}
	
	/**
	 * @see ITask#canUndo()
	 */
	public boolean canUndo() {
		return false;
	}
	
	/**
	 * @see ITask#undo()
	 */
	public void undo() {
		// do nothing
	}
}