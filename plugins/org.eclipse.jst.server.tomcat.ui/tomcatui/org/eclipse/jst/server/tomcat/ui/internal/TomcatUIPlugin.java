/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
/**
 * The actual Tomcat plugin. It allows the rest of the classes
 * to load images and get a handle to the desktop.
 */
public class TomcatUIPlugin extends AbstractUIPlugin {
	protected static TomcatUIPlugin singleton;

	protected Map imageDescriptors = new HashMap();

	// base url for icons
	private static URL ICON_BASE_URL;

	private static final String URL_OBJ = "obj16/";
	private static final String URL_WIZBAN = "wizban/";

	public static final String PLUGIN_ID = "org.eclipse.tomcat";

	public static final String IMG_WIZ_TOMCAT = "wizTomcat";

	public static final String IMG_WEB_MODULE = "webModule";
	public static final String IMG_MIME_MAPPING = "mimeMapping";
	public static final String IMG_MIME_EXTENSION = "mimeExtension";
	public static final String IMG_PORT = "port";
	public static final String IMG_PROJECT_MISSING = "projectMissing";

	public static final String PREF_TOMCAT32_INSTALL_DIR = "tomcat32install";
	public static final String PREF_TOMCAT40_INSTALL_DIR = "tomcat40install";
	public static final String PREF_TOMCAT41_INSTALL_DIR = "tomcat41install";
	public static final String PREF_TOMCAT50_INSTALL_DIR = "tomcat50install";
	public static final String PREF_JDK_INSTALL_DIR = "jdkinstall";

	/**
	 * TomcatUIPlugin constructor comment.
	 */
	public TomcatUIPlugin() {
		super();
		singleton = this;
	}

	protected ImageRegistry createImageRegistry() {
		ImageRegistry registry = new ImageRegistry();
	
		registerImage(registry, IMG_WIZ_TOMCAT, URL_WIZBAN + "tomcat_wiz.gif");
	
		registerImage(registry, IMG_WEB_MODULE, URL_OBJ + "web_module.gif");
		registerImage(registry, IMG_MIME_MAPPING, URL_OBJ + "mime_mapping.gif");
		registerImage(registry, IMG_MIME_EXTENSION, URL_OBJ + "mime_extension.gif");
		registerImage(registry, IMG_PORT, URL_OBJ + "port.gif");
		registerImage(registry, IMG_PROJECT_MISSING, URL_OBJ + "project_missing.gif");
	
		return registry;
	}

	/**
	 * Return the image with the given key from the image registry.
	 * @param key java.lang.String
	 * @return org.eclipse.jface.parts.IImage
	 */
	public static Image getImage(String key) {
		return getInstance().getImageRegistry().get(key);
	}

	/**
	 * Return the image with the given key from the image registry.
	 * @param key java.lang.String
	 * @return org.eclipse.jface.parts.IImage
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		try {
			getInstance().getImageRegistry();
			return (ImageDescriptor) getInstance().imageDescriptors.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * @return org.eclipse.jst.server.tomcat.internal.TomcatUIPlugin
	 */
	public static TomcatUIPlugin getInstance() {
		return singleton;
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	/**
	 * Returns the translated String found with the given key.
	 * @return java.lang.String
	 * @param key java.lang.String
	 */
	public static String getResource(String key) {
		try {
			return Platform.getResourceString(getInstance().getBundle(), key);
		} catch (Exception e) {
			return key;
		}
	}
	
	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given object.
	 * @return java.lang.String
	 * @param key java.lang.String
	 * @param obj java.lang.Object[]
	 */
	public static String getResource(String key, Object[] obj) {
		try {
			return MessageFormat.format(getResource(key), obj);
		} catch (Exception e) {
			return key;
		}
	}
		
	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given object.
	 * @return java.lang.String
	 * @param key java.lang.String
	 * @param obj java.lang.Object[]
	 */
	public static String getResource(String key, String arg) {
		return getResource(key, new String[] { arg });
	}
	
	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given object.
	 * @return java.lang.String
	 * @param key java.lang.String
	 * @param obj java.lang.Object[]
	 */
	public static String getResource(String key, String arg1, String arg2) {
		return getResource(key, new String[] { arg1, arg2 });
	}
	
	/**
	 * Register an image with the registry.
	 * @param key java.lang.String
	 * @param partialURL java.lang.String
	 */
	private void registerImage(ImageRegistry registry, String key, String partialURL) {
		if (ICON_BASE_URL == null) {
			String pathSuffix = "icons/";
			ICON_BASE_URL = singleton.getBundle().getEntry(pathSuffix);
		}

		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(ICON_BASE_URL, partialURL));
			registry.put(key, id);
			imageDescriptors.put(key, id);
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error registering image", e);
		}
	}
}