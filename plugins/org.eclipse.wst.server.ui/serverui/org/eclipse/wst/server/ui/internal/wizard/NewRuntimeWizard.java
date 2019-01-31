/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewRuntimeWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
/**
 * A wizard to create a new runtime.
 */
public class NewRuntimeWizard extends TaskWizard implements INewWizard {
	/**
	 * NewRuntimeWizard constructor comment.
	 */
	public NewRuntimeWizard() {
		super(Messages.wizNewRuntimeWizardTitle, new WizardFragment() {
			protected void createChildFragments(List<WizardFragment> list) {
				list.add(new NewRuntimeWizardFragment());
				list.add(WizardTaskUtil.SaveRuntimeFragment);
			}
		});

		setForcePreviousAndNextButtons(true);
	}
	
	public void init(IWorkbench newWorkbench, IStructuredSelection newSelection) {
		// do nothing
	}
}