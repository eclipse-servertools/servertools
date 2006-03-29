/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.*;
/**
 * Helper class that stores preference information for server tools.
 */
public class ServerPreferences {
	private static final String PREF_AUTO_RESTART = "auto-restart";
	private static final String PREF_AUTO_PUBLISH = "auto-publish";
	private static final String PREF_MODULE_START_TIMEOUT = "module-start-timeout";
	
	private static final String PREF_AUTO_PUBLISH_LOCAL = "auto-publish-local";
	private static final String PREF_AUTO_PUBLISH_LOCAL_TIME = "auto-publish-local-time";
	private static final String PREF_AUTO_PUBLISH_REMOTE = "auto-publish-remote";
	private static final String PREF_AUTO_PUBLISH_REMOTE_TIME = "auto-publish-remote-time";
	
	private static final String PREF_MACHINE_SPEED = "machine-speed";

	private static final String PREF_SYNC_ON_STARTUP = "sync-on-startup";

	private Preferences preferences;

	protected static ServerPreferences instance;

	/**
	 * ServerPreference constructor comment.
	 */
	private ServerPreferences() {
		super();
		preferences = ServerPlugin.getInstance().getPluginPreferences();
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
	 * Returns whether servers will be automatically restarted when
	 * required.
	 *
	 * @return boolean
	 */
	public boolean isAutoRestarting() {
		return preferences.getBoolean(PREF_AUTO_RESTART);
	}

	/**
	 * Returns whether servers will be automatically restarted when
	 * required.
	 *
	 * @return boolean
	 */
	public boolean isDefaultAutoRestarting() {
		return false;
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
		preferences.setValue(PREF_AUTO_RESTART, value);
		ServerPlugin.getInstance().savePluginPreferences();
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
	 * Return the machine speed index, from 1 to 10.
	 * 
	 * @return the relative speed
	 */
	public int getMachineSpeed() {
		return preferences.getInt(PREF_MACHINE_SPEED);
	}
	
	/**
	 * Return the default machine speed index, 5.
	 * 
	 * @return the default speed index
	 */
	public int getDefaultMachineSpeed() {
		return 5;
	}

	/**
	 * Sets the relative machine speed index, from 1 to 10.
	 * 
	 * @param speed the relative speed 
	 */
	public void setMachineSpeed(int speed) {
		preferences.setValue(PREF_MACHINE_SPEED, speed);
	}

	/**
	 * Return the sync on startup value.
	 * 
	 * @return the sync on startup value
	 */
	public boolean isSyncOnStartup() {
		return preferences.getBoolean(PREF_SYNC_ON_STARTUP);
	}

	/**
	 * Return the default sync on startup value.
	 * 
	 * @return the default sync on startup value
	 */
	public boolean getDefaultSyncOnStartup() {
		return false;
	}

	/**
	 * Sets the sync on startup value.
	 * 
	 * @param sync the sync on startup value 
	 */
	public void setSyncOnStartup(boolean sync) {
		preferences.setValue(PREF_SYNC_ON_STARTUP, sync);
	}

	/**
	 * Returns the default setting for local auto-publishing.
	 * 
	 * @return int
	 */
	public boolean getDefaultAutoPublishLocal() {
		return false;
	}

	/**
	 * Returns the setting for local auto-publishing.
	 * 
	 * @return int
	 */
	public boolean getAutoPublishLocal() {
		return preferences.getBoolean(PREF_AUTO_PUBLISH_LOCAL);
	}

	/**
	 * Sets the value for local auto-publishing.
	 * 
	 * @param auto
	 */
	public void setAutoPublishLocal(boolean auto) {
		preferences.setValue(PREF_AUTO_PUBLISH_LOCAL, auto);
		ServerPlugin.getInstance().savePluginPreferences();
	}
	
	/**
	 * Returns the default setting for local auto-publishing.
	 * 
	 * @return int
	 */
	public int getDefaultAutoPublishLocalTime() {
		return 15;
	}

	/**
	 * Returns the setting for local auto-publishing.
	 * 
	 * @return int
	 */
	public int getAutoPublishLocalTime() {
		return preferences.getInt(PREF_AUTO_PUBLISH_LOCAL_TIME);
	}

	/**
	 * Sets the value for local auto-publishing.
	 * 
	 * @param auto
	 */
	public void setAutoPublishLocalTime(int auto) {
		preferences.setValue(PREF_AUTO_PUBLISH_LOCAL_TIME, auto);
		ServerPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the default setting for remote auto-publishing.
	 * 
	 * @return int
	 */
	public boolean getDefaultAutoPublishRemote() {
		return false;
	}

	/**
	 * Returns the setting for remote auto-publishing.
	 * 
	 * @return int
	 */
	public boolean getAutoPublishRemote() {
		return preferences.getBoolean(PREF_AUTO_PUBLISH_REMOTE);
	}

	/**
	 * Sets the value for remote auto-publishing.
	 * 
	 * @param auto
	 */
	public void setAutoPublishRemote(boolean auto) {
		preferences.setValue(PREF_AUTO_PUBLISH_REMOTE, auto);
		ServerPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the default setting for remote auto-publishing.
	 * 
	 * @return int
	 */
	public int getDefaultAutoPublishRemoteTime() {
		return 60;
	}

	/**
	 * Returns the setting for remote auto-publishing.
	 * 
	 * @return int
	 */
	public int getAutoPublishRemoteTime() {
		return preferences.getInt(PREF_AUTO_PUBLISH_REMOTE_TIME);
	}

	/**
	 * Sets the value for remote auto-publishing.
	 * 
	 * @param auto
	 */
	public void setAutoPublishRemoteTime(int auto) {
		preferences.setValue(PREF_AUTO_PUBLISH_REMOTE_TIME, auto);
		ServerPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Set the default values.
	 */
	public void setDefaults() {
		preferences.setDefault(PREF_AUTO_PUBLISH, isDefaultAutoPublishing());
		preferences.setDefault(PREF_AUTO_RESTART, isDefaultAutoRestarting());
		preferences.setDefault(PREF_MACHINE_SPEED, getDefaultMachineSpeed());
		
		preferences.setDefault(PREF_AUTO_PUBLISH_LOCAL, getDefaultAutoPublishLocal());
		preferences.setDefault(PREF_AUTO_PUBLISH_LOCAL_TIME, getDefaultAutoPublishLocalTime());
		preferences.setDefault(PREF_AUTO_PUBLISH_REMOTE, getDefaultAutoPublishRemote());
		preferences.setDefault(PREF_AUTO_PUBLISH_REMOTE_TIME, getDefaultAutoPublishRemoteTime());
		
		preferences.setDefault(PREF_SYNC_ON_STARTUP, getDefaultSyncOnStartup());
		
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