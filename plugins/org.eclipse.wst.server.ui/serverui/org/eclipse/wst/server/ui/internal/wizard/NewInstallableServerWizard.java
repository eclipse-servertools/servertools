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
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.internal.IInstallableServer;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewInstallableServerWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard to create a new installable server.
 */
public class NewInstallableServerWizard extends TaskWizard {
	/**
	 * NewInstallableServerWizard constructor comment.
	 */
	public NewInstallableServerWizard() {
		super(Messages.wizNewServerWizardTitle, new WizardFragment() {
			protected void createChildFragments(List list) {
				list.add(new NewInstallableServerWizardFragment());
				list.add(new WizardFragment() {
					public void performFinish(IProgressMonitor monitor) throws CoreException {
						IInstallableServer is = (IInstallableServer) getTaskModel().getObject("installableServer");
						if (is != null)
							is.install(monitor);
					}
				});
			}
		});
	}
}