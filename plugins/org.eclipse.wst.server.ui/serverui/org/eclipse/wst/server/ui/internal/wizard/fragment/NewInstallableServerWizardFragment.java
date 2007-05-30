/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
import org.eclipse.wst.server.ui.internal.wizard.NewInstallableServerWizard;
import org.eclipse.wst.server.ui.internal.wizard.page.NewInstallableServerComposite;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * 
 */
public class NewInstallableServerWizardFragment extends WizardFragment {
	protected NewInstallableServerComposite comp;
	protected IWizardHandle wizard;

	public NewInstallableServerWizardFragment() {
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
		comp = new NewInstallableServerComposite(parent, getTaskModel(), wizard);
		
		wizard.setTitle(Messages.wizNewInstallableServerTitle);
		wizard.setDescription(Messages.wizNewInstallableServerDescription);
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_SERVER));
		return comp;
	}

	public boolean isComplete() {
		return getTaskModel().getObject("installableServer") != null;
	}

	public void exit() {
		NewInstallableServerWizard.updateLicense(wizard, getTaskModel());
	}
}