/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.ITaskModel;
/**
 * 
 */
public class WizardFragment implements IWizardFragment {
	protected List listImpl;
	private boolean isComplete = true;
	private ITaskModel model;

	public boolean hasComposite() {
		return false;
	}

	public Composite createComposite(Composite parent, IWizardHandle handle) {
		return null;
	}

	public void setTaskModel(ITaskModel model) {
		this.model = model;
	}

	public ITaskModel getTaskModel() {
		return model;
	}

	public void enter() { }

	public void exit() { }

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.IWizardFragment#createFinishTask()
	 */
	public ITask createFinishTask() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.IWizardFragment#createCancelTask()
	 */
	public ITask createCancelTask() {
		return null;
	}

	public void createSubFragments(List list) {
		// add to list
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.IWizardFragment#getChildren()
	 */
	public List getChildFragments() {
		if (listImpl == null) {
			listImpl = new ArrayList();
			createSubFragments(listImpl);
		}
		return listImpl;
	}

	public void updateSubFragments() {
		listImpl = null;
	}

	public boolean isComplete() {
		return isComplete;
	}

	protected void setComplete(boolean complete) {
		this.isComplete = complete;
	}
}