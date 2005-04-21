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
package org.eclipse.wst.server.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.TaskModel;
/**
 * A wizard fragment is a
 * 
 * @since 1.0
 */
public abstract class WizardFragment {
	private TaskModel taskModel;
	private boolean isComplete = true;
	private List listImpl;

	/**
	 * Returns <code>true</code> if this fragment has an associated UI,
	 * and <code>false</code> otherwise.
	 * 
	 * @return true if the fragment has a composite
	 */
	public boolean hasComposite() {
		return false;
	}

	/**
	 * Creates the composite associated with this fragment. 
	 * This method is only called when hasComposite() returns true.
	 * 
	 * @param parent
	 * @param handle
	 * @return the created composite
	 */
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		return null;
	}

	/**
	 * Sets the wizard task model. The task model is shared by all fragments
	 * in the wizard and is used to share data.
	 * 
	 * @param taskModel the task model
	 */
	public void setTaskModel(TaskModel taskModel) {
		this.taskModel = taskModel;
	}

	/**
	 * Returns the wizard task model.
	 * 
	 * @return the task model
	 */
	public TaskModel getTaskModel() {
		return taskModel;
	}

	/**
	 * The fragment has been entered.
	 */
	public void enter() {
		// do nothing
	}

	/**
	 * The fragment has been left.
	 */
	public void exit() {
		// do nothing
	}

	/**
	 * Create a task to run when the wizard finishes.
	 * 
	 * @return the task
	 */
	public ITask createFinishTask() {
		return null;
	}

	/**
	 * Create a task to run when the wizard is cancelled.
	 * 
	 * @return the task
	 */
	public ITask createCancelTask() {
		return null;
	}

	/**
	 * Returns the child fragments. Child fragments come directly after this fragment
	 * in the wizard flow.
	 * 
	 * @return a list of child fragments
	 */
	public List getChildFragments() {
		if (listImpl == null) {
			listImpl = new ArrayList();
			createChildFragments(listImpl);
		}
		return listImpl;
	}

	/**
	 * Gives the fragment a chance to update it's child fragments.
	 */
	public void updateChildFragments() {
		listImpl = null;
	}

	/**
	 * Called to allow the fragment to update it's children.
	 * 
	 * @param list
	 */	
	protected void createChildFragments(List list) {
		// do nothing
	}

	/**
	 * Returns true if this fragment is complete (can finish).
	 * 
	 * @return true if the fragment is complete
	 */
	public boolean isComplete() {
		return isComplete;
	}

	/**
	 * Set the isComplete state.
	 * 
	 * @param complete
	 */
	protected void setComplete(boolean complete) {
		this.isComplete = complete;
	}
}