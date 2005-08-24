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
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.List;

import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.page.NewRuntimeComposite;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

import org.eclipse.swt.widgets.Composite;
/**
 * 
 */
public class NewRuntimeWizardFragment extends WizardFragment {
	protected NewRuntimeComposite page;
	
	// filter by type/version
	protected String type;
	protected String version;
	
	// filter by partial runtime type id
	protected String runtimeTypeId;
	
	public NewRuntimeWizardFragment() {
		// do nothing
	}
	
	public NewRuntimeWizardFragment(String type, String version, String runtimeTypeId) {
		this.type = type;
		this.version = version;
		this.runtimeTypeId = runtimeTypeId;
	}

	public boolean hasComposite() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		page = new NewRuntimeComposite(parent, wizard, getTaskModel(), type, version, runtimeTypeId);
		return page;
	}

	protected void createChildFragments(List list) {
		if (getTaskModel() == null)
			return;
	
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
		if (runtime == null)
			return;

		WizardFragment sub = ServerUIPlugin.getWizardFragment(runtime.getRuntimeType().getId());
		if (sub != null)
			list.add(sub);
	}
}