/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.provisional;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;

public class ManagedUIDecorator extends UIDecorator {
	private static final String[] serverStateUnmanaged = new String[] {
		"",
		Messages.viewStatusStarting4,
		Messages.viewStatusStarted2,
		Messages.viewStatusStopping4,
		Messages.viewStatusStopped2};

	private static final String[] startingText = new String[] {
		Messages.viewStatusStarting1,
		Messages.viewStatusStarting2,
		Messages.viewStatusStarting3};
	
	private static final String[] stoppingText = new String[] {
		Messages.viewStatusStopping1,
		Messages.viewStatusStopping2,
		Messages.viewStatusStopping3};
	
	private static Image[] startingImages;
	private static Image[] stoppingImages;
	
	/**
	 * Initialize the variables for this class
	 */
	public void init(){
		loadImages();
	}
	
	/**
	 * Load the Server starting images. 
	 * NOTE: This is done so that we don't initialize the images when a label is being requested
	 */
	public void loadImages(){
		if (startingImages == null){
			startingImages = new Image[] {
				ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_1),
				ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_2),
				ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_3)
			};
			stoppingImages = new Image[] {
					ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_1),
					ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2),
					ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2)
			};
		}
	}
		
	/**
	 * @see UIDecorator#getStateLabel(int, String, int)
	 */
	public String getStateLabel(int state, String mode, int count) {
		if (state == IServer.STATE_UNKNOWN)
			return "";
		else if (state == IServer.STATE_STARTING)
			return startingText[count];
		else if (state == IServer.STATE_STOPPING)
			return stoppingText[count];
		else if (state == IServer.STATE_STARTED) {
			if (ILaunchManager.DEBUG_MODE.equals(mode))
				return Messages.viewStatusStartedDebug;
			else if (ILaunchManager.PROFILE_MODE.equals(mode))
				return Messages.viewStatusStartedProfile;
			else
				return Messages.viewStatusStarted;
		} else if (state == IServer.STATE_STOPPED)
			return Messages.viewStatusStopped;
		
		return serverStateUnmanaged[state];
	}

	/**
	 * @see UIDecorator#getStateImage(int, String, int)
	 */
	public Image getStateImage(int state, String mode, int count) { 
		// Only initialize the images when an image is required
		init();
		
		if (state == IServer.STATE_UNKNOWN)
			return null;
		else if (state == IServer.STATE_STARTING)
			return startingImages[count];
		else if (state == IServer.STATE_STOPPING)
			return stoppingImages[count];
		else if (state == IServer.STATE_STOPPED)
			return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPED);
		else { //if (state == IServer.STATE_STARTED) {
			//String mode = server.getMode();
			if (ILaunchManager.DEBUG_MODE.equals(mode))
				return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED_DEBUG);
			else if (ILaunchManager.PROFILE_MODE.equals(mode))
				return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED_PROFILE);
			else
				return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED);
		}
	}

	public String getModuleName() {
		return "module";
	}
	
	public boolean canRestart() {
		return true;
	}

	@Override
	public Image getStateImageOverlay(int state, String mode, int count) {
		// TODO Intentionally left blank
		return null;
	}
}