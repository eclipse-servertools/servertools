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
import org.eclipse.wst.server.ui.internal.actions.DebugOnServerActionDelegate;
/**
 * "Debug on Server" menu action. Allows the user to select an
 * object, and have automatic server creation, launching, and
 * the appropriate client to appear. A new instance of this
 * action must be created for each object that the user selects.
 * 
 * @since 1.0
 */
public class DebugOnServerAction extends Action {
	protected DebugOnServerActionDelegate delegate;

	/**
	 * DebugOnServerAction constructor comment.
	 */
	public DebugOnServerAction(Object object) {
		super(ServerUIPlugin.getResource("%actionDebugOnServer"));
	
		setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DTOOL_DEBUG_ON_SERVER));
		setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_DEBUG_ON_SERVER));
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ETOOL_DEBUG_ON_SERVER));
	
		delegate = new DebugOnServerActionDelegate();
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