package org.eclipse.wst.server.ui.actions;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import org.eclipse.wst.server.ui.internal.wizard.NewServerProjectWizard;
import org.eclipse.ui.IWorkbenchWizard;
/**
 * An action to invoke the new server project wizard.
 */
public class NewServerProjectAction extends LaunchWizardAction {
	/**
	 * NewServerProjectAction constructor comment.
	 */
	public NewServerProjectAction() {
		super();
	}

	/**
	 * Return the wizard that should be opened.
	 *
	 * @return org.eclipse.ui.IWorkbenchWizard
	 */
	public IWorkbenchWizard getWizard() {
		return new NewServerProjectWizard();
	}
}
