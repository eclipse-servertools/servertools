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
package org.eclipse.wst.server.ui.internal.provisional;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

public class ConnectedUIDecorator extends UIDecorator {
	private static final String[] serverStateUnmanaged = new String[] {
		"",
		ServerUIPlugin.getResource("%viewStatusStarting4"),
		ServerUIPlugin.getResource("%viewStatusStarted2"),
		ServerUIPlugin.getResource("%viewStatusStopping4"),
		ServerUIPlugin.getResource("%viewStatusStopped2")};

	private static final String[] startingText = new String[] {
		ServerUIPlugin.getResource("%viewStatusStarting1"),
		ServerUIPlugin.getResource("%viewStatusStarting2"),
		ServerUIPlugin.getResource("%viewStatusStarting3")};
	
	private static final String[] stoppingText = new String[] {
		ServerUIPlugin.getResource("%viewStatusStopping1"),
		ServerUIPlugin.getResource("%viewStatusStopping2"),
		ServerUIPlugin.getResource("%viewStatusStopping3")};
	
	private static final Image[] startingImages = new Image[] {
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_1),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_2),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_3)
	};
	
	private static final Image[] stoppingImages = new Image[] {
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_1),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2)
	};

	/**
	 * Returns a string representing the given state.
	 *
	 * @return java.lang.String
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
				return ServerUIPlugin.getResource("%viewStatusStartedDebug");
			else if (ILaunchManager.PROFILE_MODE.equals(mode))
				return ServerUIPlugin.getResource("%viewStatusStartedProfile");
			else
				return ServerUIPlugin.getResource("%viewStatusStarted");
		} else if (state == IServer.STATE_STOPPED)
			return ServerUIPlugin.getResource("%viewStatusStopped");
		
		return serverStateUnmanaged[state];
	}

	/**
	 * Returns an image representing the given state.
	 * 
	 * @return org.eclipse.jface.parts.IImage
	 */
	public Image getStateImage(int state, String mode, int count) {
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
	
	public boolean canRestart() {
		return false;
	}
	
	public void setupAction(Action action, int action2) {
		action.setToolTipText(ServerUIPlugin.getResource("%actionStopToolTip2"));
		action.setText(ServerUIPlugin.getResource("%actionStop2"));
		action.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_DISCONNECT));
		action.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_DISCONNECT));
		action.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_DISCONNECT));
	}
}