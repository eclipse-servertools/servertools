/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.ui.internal.task.ModifyModulesTask;
import org.eclipse.wst.server.ui.internal.wizard.page.ModifyModulesComposite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 */
public class ModifyModulesWizardFragment extends WizardFragment {
	protected ModifyModulesComposite comp;
	protected ModifyModulesTask task;

	public ModifyModulesWizardFragment() { }
	
	public boolean hasComposite() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		comp = new ModifyModulesComposite(parent, handle);
		return comp;
	}

	public void enter() {
		if (comp != null) {
			IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
			comp.setServer(server);
		}
	}

	public void exit() {
		if (comp != null) {
			createFinishTask();
			task.setAddModules(comp.getModulesToAdd());
			task.setRemoveModules(comp.getModulesToRemove());
		}
	}

	public ITask createFinishTask() {
		if (task == null)
			task = new ModifyModulesTask(); 
		return task;
	}
}