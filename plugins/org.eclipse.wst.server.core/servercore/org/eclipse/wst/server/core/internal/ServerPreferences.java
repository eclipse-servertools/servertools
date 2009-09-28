/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.*;
/**
 * Helper class that stores preference information for server tools.
 */
public class ServerPreferences {
	private static final String PREF_AUTO_PUBLISH = "auto-publish";
	private static final String PREF_MODULE_START_TIMEOUT = "module-start-timeout";
		
	private Preferences preferences;

	protected static ServerPreferences instance;

	/**
	 * ServerPreference constructor comment.
	 */
	private ServerPreferences() {
		super();
		preferences = ServerPlugin.getInstance().getPluginPreferences();
		setDefaults();
	}

	/**
	 * Returns the static instance.
	 * 
	 * @return the static instance
	 */
	public static ServerPreferences getInstance() {
		if (instance == null)
			instance = new ServerPreferences();
		return instance;
	}

	/**
	 * Returns whether publishing should occur before starting the
	 * server.
	 *
	 * @return boolean
	 */
	public boolean isAutoPublishing() {
		return preferences.getBoolean(PREF_AUTO_PUBLISH);
	}
	
	/**
	 * Returns whether publishing should occur before starting the
	 * server.
	 *
	 * @return boolean
	 */
	public boolean isDefaultAutoPublishing() {
		return true;
	}

	/**
	 * Set whether servers will be automatically restarted when
	 * they need a restart.
	 *
	 * @param value
	 */
	public void setAutoRestarting(boolean value) {
		// ignore
	}

	/**
	 * Set whether publishing should happen before the server starts.
	 *
	 * @param value
	 */
	public void setAutoPublishing(boolean value) {
		preferences.setValue(PREF_AUTO_PUBLISH, value);
		ServerPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the module start timeout.
	 * 
	 * @return the module start timeout
	 */
	public int getModuleStartTimeout() {
		return preferences.getInt(PREF_MODULE_START_TIMEOUT);
	}

	/**
	 * Set the default values.
	 */
	private void setDefaults() {
		preferences.setDefault(PREF_AUTO_PUBLISH, isDefaultAutoPublishing());
			
		preferences.setDefault(PREF_MODULE_START_TIMEOUT, 300001);
		boolean save = false;
		if (preferences.isDefault(PREF_MODULE_START_TIMEOUT)) {
			preferences.setValue(PREF_MODULE_START_TIMEOUT, 300000);
			save = true;
		}
		if (save)
			ServerPlugin.getInstance().savePluginPreferences();
	}
}