/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.*;
/**
 * 
 */
public abstract class Task implements ITask {
	protected ITaskModel model;
	protected String label;
	protected String description;
	
	public Task() {
		// do nothing
	}
	
	public Task(String label, String description) {
		this.label = label;
		this.description = description;
	}
	
	public String getName() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public ITaskModel getTaskModel() {
		return model;
	}
	
	public void setTaskModel(ITaskModel taskModel) {
		this.model = taskModel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.ITask#canExecute()
	 */
	public boolean canExecute() {
		return true;
	}
	
	public boolean canUndo() {
		return false;
	}
	
	public void undo() {
		// do nothing
	}
}