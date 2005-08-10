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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.TaskModel;
/**
 * A wizard fragment is a
 * 
 * @plannedfor 1.0
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
	 * Called when the wizard that this fragment belongs to has traversed
	 * into this wizard fragment. It is called to give the fragment the
	 * opportunity to initialize any values shown in the composite or
	 * update the task model.
	 * <p>
	 * When finish is pressed, the current fragment is exit()ed, and then
	 * performFinish() is called on all of the fragments in the tree.
	 * enter() and exit() are not called on the remaining fragments.
	 * </p>
	 */
	public void enter() {
		// do nothing
	}

	/**
	 * Called when the wizard that this fragment belongs to has traversed
	 * out of this wizard fragment. It is called to give the fragment the
	 * opportunity to save any values entered into the composite or
	 * update the task model.
	 * <p>
	 * When finish is pressed, the current fragment is exit()ed, and then
	 * performFinish() is called on all of the fragments in the tree.
	 * enter() and exit() are not called on the remaining fragments. 
	 * </p>
	 */
	public void exit() {
		// do nothing
	}

	/**
	 * Called when the wizard that this fragment belongs to is finished.
	 * After exit()ing the current page, all fragment's performFinish()
	 * methods are called in order.
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if something goes wrong
	 */
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		// do nothing
	}

	/**
	 * Called when the wizard that this fragment belongs to is canceled.
	 * After exit()ing the current page, all fragment's performCancel()
	 * methods are called in order.
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if something goes wrong
	 */
	public void performCancel(IProgressMonitor monitor) throws CoreException {
		// do nothing
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
	 * @return <code>true</code> if the fragment is complete, and
	 *    <code>false</code> otherwise
	 */
	public boolean isComplete() {
		return isComplete;
	}

	/**
	 * Set the isComplete state.
	 * 
	 * @param complete <code>true</code> if the fragment is complete, and
	 *    <code>false</code> otherwise
	 */
	protected void setComplete(boolean complete) {
		this.isComplete = complete;
	}
}