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
	private static final String PREF_CREATE_IN_WORKSPACE = "create-workspace";
	private static final String PREF_STARTUP_TIMEOUT = "start-timeout";
	private static final String PREF_RESTART_MODULE_TIMEOUT = "restart-module-timeout";
	private static final String PREF_MODULE_START_TIMEOUT = "module-start-timeout";
	
	private static final String PREF_AUTO_PUBLISH_LOCAL = "auto-publish-local";
	private static final String PREF_AUTO_PUBLISH_LOCAL_TIME = "auto-publish-local-time";
	private static final String PREF_AUTO_PUBLISH_REMOTE = "auto-publish-remote";
	private static final String PREF_AUTO_PUBLISH_REMOTE_TIME = "auto-publish-remote-time";

	private Preferences preferences;

	protected static ServerPreferences instance;

	/**
	 * ServerPreference constructor comment.
	 */
	private ServerPreferences() {
		super();
		preferences = ServerPlugin.getInstance().getPluginPreferences();
	}

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
	 * Returns whether servers and configurations should be created in the
	 * workspace (as opposed to in metadata).
	 *
	 * @return boolean
	 */
	public boolean isCreateResourcesInWorkspace() {
		return preferences.getBoolean(PREF_CREATE_IN_WORKSPACE);
	}

	/**
	 * Returns whether servers and configurations should be created in the
	 * workspace (as opposed to in metadata) by default.
	 *
	 * @return boolean
	 */
	public boolean isDefaultCreateResourcesInWorkspace() {
		return false;
	}

	/**
	 * Set whether servers and configurations should be created in the
	 * workspace (as opposed to in metadata).
	 *
	 * @param b
	 */
	public void setCreateResourcesInWorkspace(boolean b) {
		preferences.setValue(PREF_CREATE_IN_WORKSPACE, b);
		ServerPlugin.getInstance().savePluginPreferences();
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

	public int getStartupTimeout() {
		return preferences.getInt(PREF_STARTUP_TIMEOUT);
	}

	public int getRestartModuleTimeout() {
		return preferences.getInt(PREF_RESTART_MODULE_TIMEOUT);
	}

	public int getModuleStartTimeout() {
		return preferences.getInt(PREF_MODULE_START_TIMEOUT);
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

	public void setDefaults() {
		preferences.setDefault(PREF_AUTO_PUBLISH, isDefaultAutoPublishing());
		preferences.setDefault(PREF_AUTO_RESTART, isDefaultAutoRestarting());
		preferences.setDefault(PREF_STARTUP_TIMEOUT, 210001);
		
		preferences.setDefault(PREF_AUTO_PUBLISH_LOCAL, getDefaultAutoPublishLocal());
		preferences.setDefault(PREF_AUTO_PUBLISH_LOCAL_TIME, getDefaultAutoPublishLocalTime());
		preferences.setDefault(PREF_AUTO_PUBLISH_REMOTE, getDefaultAutoPublishRemote());
		preferences.setDefault(PREF_AUTO_PUBLISH_REMOTE_TIME, getDefaultAutoPublishRemoteTime());
		
		preferences.setDefault(PREF_RESTART_MODULE_TIMEOUT, 120001);
		preferences.setDefault(PREF_MODULE_START_TIMEOUT, 300001);
		boolean save = false;
		if (preferences.isDefault(PREF_STARTUP_TIMEOUT)) {
			preferences.setValue(PREF_STARTUP_TIMEOUT, 210000);
			save = true;
		}
		if (preferences.isDefault(PREF_RESTART_MODULE_TIMEOUT)) {
			preferences.setValue(PREF_RESTART_MODULE_TIMEOUT, 120000);
			save = true;
		}		if (preferences.isDefault(PREF_MODULE_START_TIMEOUT)) {
			preferences.setValue(PREF_MODULE_START_TIMEOUT, 300000);
			save = true;
		}
		if (save)
			ServerPlugin.getInstance().savePluginPreferences();
	}
}