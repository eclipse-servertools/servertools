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
package org.eclipse.wst.server.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
/**
 * "Run on Server" menu action. Allows the user to select an
 * object, and have automatic server creation, launching, and
 * the appropriate client to appear. A new instance of this
 * action must be created for each object that the user selects.
 * 
 * @since 1.0
 */
public class RunOnServerAction extends Action {
	protected RunOnServerActionDelegate delegate;

	/**
	 * Run on server action.
	 * 
	 * @param the object to attempt to debug
	 */
	public RunOnServerAction(Object object) {
		super(ServerUIPlugin.getResource("%actionRunOnServer"));
	
		setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DTOOL_RUN_ON_SERVER));
		setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_RUN_ON_SERVER));
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ETOOL_RUN_ON_SERVER));
	
		delegate = new RunOnServerActionDelegate();
		if (object != null) {
			StructuredSelection sel = new StructuredSelection(object);
			delegate.selectionChanged(this, sel);
		} else
			delegate.selectionChanged(this, null);
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		delegate.run(this);
	}
}