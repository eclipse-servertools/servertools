package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.*;

/**
 * 
 */
public abstract class Task implements ITask {
	protected ITaskModel model;
	protected String label;
	protected String description;
	
	public Task() { }
	
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
	
	public void undo() { }
}
