/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Gorkem Ercan
 */
public class GenericUiPlugin extends AbstractUIPlugin {
	
	/**
	 * Plug-in ID
	 */
	public static final String PLUGIN_ID = "org.eclipse.jst.server.generic.ui"; //$NON-NLS-1$
	
    public static final String WIZBAN_IMAGE = "genericlogo"; //$NON-NLS-1$
    //The shared instance.
	private static GenericUiPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public GenericUiPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.jst.server.generic.ui.GenericUiPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static GenericUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = GenericUiPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

    protected ImageRegistry createImageRegistry() {
        ImageRegistry registry = new ImageRegistry();
        ImageDescriptor desc = ImageDescriptor.createFromURL(getDefault().getBundle().getEntry("/icons/wizban/new_server_wiz.gif")); //$NON-NLS-1$
        registry.put(WIZBAN_IMAGE,desc);
        return registry;
    }
  	public ImageDescriptor imageDescriptor(String key){
		return getImageRegistry().getDescriptor(key);
	}
	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
