/*******************************************************************************
 * Copyright (c) 2008,2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.cnf;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.provisional.UIDecorator;

public class CNFManagedUIDecorator extends UIDecorator {
	private static final String[] serverStateUnmanaged = new String[] {
		"",
		Messages.viewStatusStarting4,
		Messages.viewStatusStarted2,
		Messages.viewStatusStopping4,
		Messages.viewStatusStopped2};
	
	private static Image[] startingImages;
	private static ImageDescriptor[] startingImagesDescriptor;	
	private static Image[] stoppingImages;
	private static ImageDescriptor[] stoppingImagesDescriptor;
	
	private static Image[] startingImagesOverlay;	
	private static Image[] stoppingImagesOverlay;

	
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
			
			startingImagesDescriptor = new ImageDescriptor[] {
				ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STARTING_1),
				ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STARTING_2),
				ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STARTING_3)
			};
			
			stoppingImages = new Image[] {
				ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_1),
				ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2),
				ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2)
			};
			
			stoppingImagesDescriptor = new ImageDescriptor[] {
				ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STOPPING_1),
				ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STOPPING_2),
				ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STOPPING_3)
			};
					
			// overlay icons
			startingImagesOverlay = new Image[] {
					ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_1_OVERLAY),
					ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_2_OVERLAY),
					ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_3_OVERLAY)
				};			
				
			stoppingImagesOverlay = new Image[] {
					ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_1_OVERLAY),
					ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2_OVERLAY),
					ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2_OVERLAY)
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
			return Messages.viewStatusStarting;
		else if (state == IServer.STATE_STOPPING)
			return Messages.viewStatusStopping;
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.provisional.UIDecorator#getStateImage(int, java.lang.String, int)
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.provisional.UIDecorator#getStateImage(int, java.lang.String, int)
	 */
	public Image getStateImageOverlay(int state, String mode, int count) {
		// Only initialize the images when an image is required
		init();
		
		if (state == IServer.STATE_UNKNOWN)
			return null;
		else if (state == IServer.STATE_STARTING)
			return startingImagesOverlay[count];
		else if (state == IServer.STATE_STOPPING)
			return stoppingImagesOverlay[count];
		else if (state == IServer.STATE_STOPPED)
			return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPED_OVERLAY);
		else { //if (state == IServer.STATE_STARTED) {
			//String mode = server.getMode();
			if (ILaunchManager.DEBUG_MODE.equals(mode))
				return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED_DEBUG_OVERLAY);
			else if (ILaunchManager.PROFILE_MODE.equals(mode))
				return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED_PROFILE_OVERLAY);
			else
				return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED_OVERLAY);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.provisional.UIDecorator#getStateImageDescriptor(int, java.lang.String, int)
	 */
	public ImageDescriptor getStateImageDescriptor(int state, String mode, int count) {
		// Only initialize the images when an image is required
		if (state == IServer.STATE_UNKNOWN)
			return null;
		else if (state == IServer.STATE_STARTING)
			return startingImagesDescriptor[count];
		else if (state == IServer.STATE_STOPPING)
			return stoppingImagesDescriptor[count];
		else if (state == IServer.STATE_STOPPED)
			return ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STOPPED);
		else { //if (state == IServer.STATE_STARTED) {
			//String mode = server.getMode();
			if (ILaunchManager.DEBUG_MODE.equals(mode))
				return ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STARTED_DEBUG);
			else if (ILaunchManager.PROFILE_MODE.equals(mode))
				return ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STARTED_PROFILE);
			else
				return ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_STATE_STARTED);
		}
	}
	
	public String getModuleName() {
		return "module";
	}
	
	public boolean canRestart() {
		return true;
	}
}