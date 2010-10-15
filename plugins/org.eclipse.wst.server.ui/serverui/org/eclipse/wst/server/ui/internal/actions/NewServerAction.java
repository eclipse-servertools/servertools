/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.wst.server.ui.internal.wizard.NewServerWizard;
/**
 * Action to create a new server.
 */
public class NewServerAction extends NewWizardAction {
	protected String[] ids;
	protected String[] values;

	/**
	 * Create a new NewServerAction.
	 */
	public NewServerAction() {
		super();
	}

	/**
	 * Create a new NewServerAction with some initial task model
	 * properties.
	 * 
	 * @param ids
	 * @param values
	 */
	public NewServerAction(String[] ids, String[] values) {
		super();
		this.ids = ids;
		this.values = values;
	}

	/**
	 * Performs this action.
	 * <p>
	 * This method is called when the delegating action has been triggered.
	 * Implement this method to do the actual work.
	 * </p>
	 *
	 * @param action the action proxy that handles the presentation portion of the
	 *   action
	 */
	public void run(IAction action) {
		NewServerWizard wizard = null;
		if (ids == null)
			wizard = new NewServerWizard();
		else
			wizard = new NewServerWizard(ids, values);
		wizard.init(workbench, selection);
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.open();
	}
}