/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.actions;

import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.NewServerWizard;
/**
 * An action to invoke the new server and server configuration wizard.
 */
public class NewServerAction extends LaunchWizardAction {
	protected String[] ids;
	protected String[] values;

	/**
	 * NewServerAction constructor comment.
	 */
	public NewServerAction() {
		super();
	
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_NEW_SERVER));
		setText(ServerUIPlugin.getResource("%actionSetNewServer"));
	}

	public NewServerAction(String[] ids, String[] values) {
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