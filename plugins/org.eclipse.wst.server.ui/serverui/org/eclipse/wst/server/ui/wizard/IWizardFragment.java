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

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.ITaskModel;
/**
 * 
 */
public interface IWizardFragment {
	public boolean hasComposite();

	public Composite createComposite(Composite parent, IWizardHandle handle);

	public void setTaskModel(ITaskModel model);
	
	public ITaskModel getTaskModel();

	public void enter();

	public void exit();

	public ITask createFinishTask();

	public ITask createCancelTask();

	public List getChildFragments();

	public void updateSubFragments();

	public boolean isComplete();
}