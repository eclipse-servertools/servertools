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
package org.eclipse.wst.server.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.swt.graphics.Image;
/**
 * Provides access to server UI general images. getImage() and getImageDescriptor()
 * provide access (through keys in the class header) to the images that can be used
 * for server state, publishing, etc.
 */
public class ServerImageResource {
	// server state images
	public static final String IMG_SERVER_STATE_UNKNOWN = "stateUnknown";
	public static final String IMG_SERVER_STATE_STARTING = "stateStarting1";
	public static final String IMG_SERVER_STATE_STARTED = "stateStarted";
	public static final String IMG_SERVER_STATE_STARTED_DEBUG = "stateStartedDebug";
	public static final String IMG_SERVER_STATE_STOPPING = "stateStopping1";
	public static final String IMG_SERVER_STATE_STOPPED = "stateStopped";

	// misc images
	public static final String IMG_SERVER_PROJECT = "serverProject";
	public static final String IMG_SERVER_CONFIGURATION = "configuration";
	public static final String IMG_SERVER = "server";
	public static final String IMG_SERVER_CONFIGURATION_NONE = "noConfiguration";
	public static final String IMG_SERVER_CONFIGURATION_MISSING = "configurationMissing";
	public static final String IMG_SERVER_CONFIGURATION_FOLDER = "configurationFolder";
	public static final String IMG_SERVER_FOLDER = "serverFolder";
	public static final String IMG_PROJECT_MISSING = "projectMissing";

	/**
	 * Return the image with the given key.
	 *
	 * @param key java.lang.String
	 * @return org.eclipse.swt.graphics.Image
	 */
	public static Image getImage(String key) {
		return ImageResource.getImage(key);
	}

	/**
	 * Return the image descriptor with the given key.
	 *
	 * @param key java.lang.String
	 * @return import org.eclipse.jface.resource.ImageDescriptor
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		return ImageResource.getImageDescriptor(key);
	}
}