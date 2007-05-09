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
package org.eclipse.wst.server.preview.adapter.internal.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
/**
 * The HTTP server core plugin.
 */
public class PreviewPlugin extends Plugin {
	public static final String PLUGIN_ID = "org.eclipse.wst.server.preview.adapter";

	private static PreviewPlugin plugin;

	/**
	 * The constructor
	 */
	public PreviewPlugin() {
		plugin = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * 
	 * @return an instance
	 */
	public static PreviewPlugin getInstance() {
		return plugin;
	}

	/**
	 * Return the install location preference.
	 * 
	 * @param id a runtime type id
	 * @return the install location
	 */
	public static String getPreference(String id) {
		return getInstance().getPluginPreferences().getString(id);
	}

	/**
	 * Set the install location preference.
	 * 
	 * @param id the runtimt type id
	 * @param value the location
	 */
	public static void setPreference(String id, String value) {
		getInstance().getPluginPreferences().setValue(id, value);
		getInstance().savePluginPreferences();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
}