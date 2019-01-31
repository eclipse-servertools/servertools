/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.discovery.Discovery;
import org.eclipse.wst.server.ui.internal.Messages;

public class DownloadableAdapterLicenseWizardFragment extends LicenseWizardFragment {
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		Discovery.installExtension((String) getTaskModel().getObject(TaskModel.TASK_EXTENSION));
	}
	
	public void enter() {
		super.enter();
		setForceLastFragment(true);
		if (((Integer) getTaskModel().getObject(LicenseWizardFragment.LICENSE_ERROR)).equals(new Integer(IMessageProvider.ERROR))){
			comp.setVisibleAcceptReject(false);
			wizardHandle.setTitle(Messages.errorTitle);
			wizardHandle.setMessage(Messages.chooseAnotherServer, IMessageProvider.ERROR);
		}
		else{
			comp.setVisibleAcceptReject(true);
			wizardHandle.setTitle(Messages.wizLicenseTitle);
			wizardHandle.setMessage("", IMessageProvider.NONE);
		}

	}

}
