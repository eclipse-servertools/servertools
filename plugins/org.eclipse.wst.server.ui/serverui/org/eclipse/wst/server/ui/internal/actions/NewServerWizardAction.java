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
package org.eclipse.wst.server.ui.internal.actions;

import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.wizard.NewServerWizard;
/**
 * An action to invoke the new server and server configuration wizard.
 */
public class NewServerWizardAction extends LaunchWizardAction {
	protected String[] ids;
	protected String[] values;

	/**
	 * New server action.
	 */
	public NewServerWizardAction() {
		super();
	
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_NEW_SERVER));
		setText(Messages.actionSetNewServer);
	}
	
	/**
	 * New server action.
	 * 
	 * @param ids ids to pass into the action
	 * @param values values to pass into the action
	 */
	public NewServerWizardAction(String[] ids, String[] values) {
		this();
		this.ids = ids;
		this.values = values;
	}

	/**
	 * Return the wizard that should be opened.
	 *
	 * @return org.eclipse.ui.IWorkbenchWizard
	 */
	protected IWorkbenchWizard getWizard() {
		return new NewServerWizard(ids, values);
	}
}