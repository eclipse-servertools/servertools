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
package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class InputWizardFragment extends WizardFragment {
	protected String[] ids;
	protected Object[] values;
	
	public InputWizardFragment(String id, Object value) {
		this(new String[] { id }, new Object[] { value });
	}

	public InputWizardFragment(String[] ids, Object[] values) {
		this.ids = ids;
		this.values = values;
	}
	
	/*public void enter() {
		int size = ids.length;
		for (int i = 0; i < size; i++)
			getTaskModel().putObject(ids[i], values[i]);
	}*/
	
	public void setTaskModel(ITaskModel taskModel) {
		super.setTaskModel(taskModel);
		if (taskModel == null)
			return;
		
		int size = ids.length;
		for (int i = 0; i < size; i++)
			taskModel.putObject(ids[i], values[i]);
	}
}