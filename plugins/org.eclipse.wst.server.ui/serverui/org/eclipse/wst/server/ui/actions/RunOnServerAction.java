/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.actions;

import java.util.HashMap;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
/**
 * "Run on Server" menu action. Allows the user to select an
 * object, and have automatic server creation, launching, and
 * the appropriate client to appear. A new instance of this
 * action must be created for each object that the user selects.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class RunOnServerAction extends Action {
	protected RunOnServerActionDelegate delegate;

	protected HashMap<String,Object> actionProperties;
	/**
	 * Create a new Run on Server action for run mode.
	 * 
	 * @param object the object to attempt to run
	 */
	public RunOnServerAction(Object object) {
		this(object, ILaunchManager.RUN_MODE);
	}
	
	/**
	 * Create a new Run on Server action for run mode.
	 * 
	 * @param object the object to attempt to run
	 */
	public RunOnServerAction(Object object, HashMap<String,Object> actionProperties) {		
		this(object, ILaunchManager.RUN_MODE);
		this.actionProperties = actionProperties;
		delegate.setActionProperties(actionProperties);
	}

	/**
	 * Create a new Run on Server action.
	 * 
	 * @param object the object to attempt to run
	 * @param launchMode a {@link ILaunchManager} launch mode
	 */
	public RunOnServerAction(Object object, String launchMode) {
		super();
		
		if (actionProperties == null){
			actionProperties = new HashMap<String, Object>();
		}
		
		if (ILaunchManager.DEBUG_MODE.equals(launchMode)) {
			setText(Messages.actionDebugOnServer);
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DTOOL_DEBUG_ON_SERVER));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_DEBUG_ON_SERVER));
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ETOOL_DEBUG_ON_SERVER));
		} else if (ILaunchManager.PROFILE_MODE.equals(launchMode)) {
			setText(Messages.actionProfileOnServer);
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DTOOL_PROFILE_ON_SERVER));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_PROFILE_ON_SERVER));
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ETOOL_PROFILE_ON_SERVER));
		} else {
			setText(Messages.actionRunOnServer);
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DTOOL_RUN_ON_SERVER));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_RUN_ON_SERVER));
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ETOOL_RUN_ON_SERVER));
		}
		
		delegate = new RunOnServerActionDelegate(actionProperties);
		delegate.setLaunchMode(launchMode);
		if (object != null) {
			StructuredSelection sel = new StructuredSelection(object);
			delegate.selectionChanged(this, sel);
		} else
			delegate.selectionChanged(this, null);
	}

	/* (non-Javadoc)
	 * Method declared on IAction.
	 */
	public void run() {
		delegate.run(this);
	}
}