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
	private ITaskModel model;
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
	 * @param name
	 * @param description
	 */
	public Task(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	/*
	 * @see org.eclipse.wst.server.core.ITask.getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * @see org.eclipse.wst.server.core.ITask.getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * @see org.eclipse.wst.server.core.ITask.getTaskModel()
	 */
	public ITaskModel getTaskModel() {
		return model;
	}
	
	/*
	 * @see org.eclipse.wst.server.core.ITask.setTaskModel(org.eclipse.wst.server.core.ITaskModel)
	 */
	public void setTaskModel(ITaskModel taskModel) {
		this.model = taskModel;
	}

	/*
	 * @see org.eclipse.wst.server.core.ITask.canExecute()
	 */
	public boolean canExecute() {
		return true;
	}
	
	/*
	 * @see org.eclipse.wst.server.core.ITask.canUndo()
	 */
	public boolean canUndo() {
		return false;
	}
	
	/*
	 * @see org.eclipse.wst.server.core.ITask.undo()
	 */
	public void undo() {
		// do nothing
	}
}