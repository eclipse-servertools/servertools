/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.http.ui.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
/**
 * The activator class controls the plug-in life cycle
 */
public class HttpUIPlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.wst.server.http.ui";

	private Map<String, ImageDescriptor> imageDescriptors = new HashMap<String, ImageDescriptor>();

	// base url for icons
	private static URL ICON_BASE_URL;
	private static final String URL_WIZBAN = "wizban/";
	public static final String IMG_WIZ_SERVER = "wizServer";

	// The shared instance
	private static HttpUIPlugin plugin;

	/**
	 * The constructor
	 */
	public HttpUIPlugin() {
		plugin = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * 
	 * @return HttpUIPlugin
	 */
	public static HttpUIPlugin getInstance() {
		return plugin;
	}

	protected ImageRegistry createImageRegistry() {
		ImageRegistry registry = new ImageRegistry();
		registerImage(registry, IMG_WIZ_SERVER, URL_WIZBAN + "server_wiz.gif");
		
		return registry;
	}

	/**
	 * Return the image with the given key from the image registry.
	 * 
	 * @param key java.lang.String
	 * @return org.eclipse.jface.parts.IImage
	 */
	public static Image getImage(String key) {
		return getInstance().getImageRegistry().get(key);
	}

	/**
	 * Return the image with the given key from the image registry.
	 * 
	 * @param key java.lang.String
	 * @return org.eclipse.jface.parts.IImage
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		try {
			getInstance().getImageRegistry();
			return getInstance().imageDescriptors.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Register an image with the registry.
	 * 
	 * @param key java.lang.String
	 * @param partialURL java.lang.String
	 */
	private void registerImage(ImageRegistry registry, String key, String partialURL) {
		if (ICON_BASE_URL == null) {
			String pathSuffix = "icons/";
			ICON_BASE_URL = plugin.getBundle().getEntry(pathSuffix);
		}

		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(ICON_BASE_URL,
					partialURL));
			registry.put(key, id);
			imageDescriptors.put(key, id);
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error registering image", e);
		}
	}
}