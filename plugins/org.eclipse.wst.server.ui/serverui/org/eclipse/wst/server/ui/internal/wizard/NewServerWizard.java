/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.fragment.ModifyModulesWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewServerWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
/**
 * A wizard to create a new server and server configuration.
 */
public class NewServerWizard extends TaskWizard implements INewWizard {
	/**
	 * NewServerWizard constructor comment.
	 */
	public NewServerWizard() {
		this(null, null);
	}

	public NewServerWizard(final String[] ids, final String[] values) {
		super(Messages.wizNewServerWizardTitle, new WizardFragment() {
			protected void createChildFragments(List list) {
				list.add(new NewServerWizardFragment());
				
				list.add(WizardTaskUtil.TempSaveRuntimeFragment);
				list.add(WizardTaskUtil.TempSaveServerFragment);
				
				list.add(new ModifyModulesWizardFragment());
				list.add(new TasksWizardFragment());
				
				list.add(WizardTaskUtil.SaveRuntimeFragment);
				list.add(WizardTaskUtil.SaveServerFragment);
				list.add(WizardTaskUtil.SaveHostnameFragment);
			}
		});
		
		if (ids != null) {
			TaskModel taskModel2 = getTaskModel();
			int size = ids.length;
			for (int i = 0; i < size; i++) {
				taskModel2.putObject(ids[i], values[i]);
			}
		}
	}

	public void init(IWorkbench newWorkbench, IStructuredSelection newSelection) {
		// do nothing
	}
}