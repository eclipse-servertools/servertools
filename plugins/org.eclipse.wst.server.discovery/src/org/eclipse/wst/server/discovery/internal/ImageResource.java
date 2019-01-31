/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
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
package org.eclipse.wst.server.discovery.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
/**
 * Utility class to handle image resources.
 */
public class ImageResource {
	// the image registry
	private static ImageRegistry imageRegistry;

	// map of image descriptors since these
	// will be lost by the image registry
	private static Map<String, ImageDescriptor> imageDescriptors;

	// base urls for images
	private static URL ICON_BASE_URL;

	static {
		try {
			String pathSuffix = "icons/";
			ICON_BASE_URL = Activator.getDefault().getBundle().getEntry(pathSuffix);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not set icon base URL", e);
		}
	}

	private static final String URL_OBJ = "obj16/";

	private static final String URL_WIZBAN = "wizban/";

	// --- constants for images ---

	public static final String IMG_WIZARD = "wizard";
	public static final String IMG_EXTENSION = "extension";

	/**
	 * Cannot construct an ImageResource. Use static methods only.
	 */
	private ImageResource() {
		// do nothing
	}
	
	/**
	 * Dispose of element images that were created.
	 */
	protected static void dispose() {
		// do nothing
	}

	/**
	 * Return the image with the given key.
	 *
	 * @param key a key
	 * @return an image
	 */
	public static Image getImage(String key) {
		if (imageRegistry == null)
			initializeImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null) {
			imageRegistry.put(key, ImageDescriptor.getMissingImageDescriptor());
			image = imageRegistry.get(key);
		}
		return image;
	}

	/**
	 * Return the image descriptor with the given key.
	 *
	 * @param key a key
	 * @return an image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		if (imageRegistry == null)
			initializeImageRegistry();
		ImageDescriptor id = imageDescriptors.get(key);
		if (id != null)
			return id;
		
		return ImageDescriptor.getMissingImageDescriptor();
	}

	/**
	 * Initialize the image resources.
	 */
	protected static void initializeImageRegistry() {
		imageRegistry = new ImageRegistry();
		imageDescriptors = new HashMap<String, ImageDescriptor>();
		
		registerImage(IMG_WIZARD, URL_WIZBAN + "install_wiz.gif");
		registerImage(IMG_EXTENSION, URL_OBJ + "iu_obj.gif");
		
		PlatformUI.getWorkbench().getProgressService().registerIconForFamily(
				getImageDescriptor(IMG_EXTENSION), Activator.JOB_FAMILY);
	}

	/**
	 * Register an image with the registry.
	 *
	 * @param key a key
	 * @param partialURL a partial URL
	 */
	private static void registerImage(String key, String partialURL) {
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(ICON_BASE_URL, partialURL));
			imageRegistry.put(key, id);
			imageDescriptors.put(key, id);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error registering image " + key + " from " + partialURL, e);
		}
	}
}