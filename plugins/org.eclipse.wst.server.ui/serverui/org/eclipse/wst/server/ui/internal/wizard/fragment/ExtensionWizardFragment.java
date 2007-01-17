/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.ExtensionWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.internal.wizard.page.Extension2Composite;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * 
 */
public class ExtensionWizardFragment extends WizardFragment {
	protected Extension2Composite comp;
	protected IWizardHandle wizard;

	public ExtensionWizardFragment() {
		// do nothing
	}

	public boolean hasComposite() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard2) {
		this.wizard = wizard2;
		comp = new Extension2Composite(parent, getTaskModel(), wizard);
		
		wizard.setTitle(Messages.wizNewInstallableServerTitle);
		wizard.setDescription(Messages.wizNewInstallableServerDescription);
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_SERVER));
		return comp;
	}

	public boolean isComplete() {
		return getTaskModel().getObject(WizardTaskUtil.TASK_FEATURE) != null;
	}

	public void exit() {
		ExtensionWizard.updateLicense(wizard, getTaskModel());
	}
}