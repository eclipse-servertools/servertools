/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.*;

import org.eclipse.wst.server.core.*;
/**
 * Helper class that stores preference information for server tools.
 */
public class ServerPreferences implements IServerPreferences {
	private static final String PREF_AUTO_RESTART = "auto-restart";
	private static final String PREF_AUTO_PUBLISH = "auto-publish";
	private static final String PREF_AUTO_REPAIR_MODULES = "auto-repair-modules";
	private static final String PREF_PUBLISHER = "publisher";
	private static final String PREF_CREATE_IN_WORKSPACE = "create-workspace";
	private static final String PREF_STARTUP_TIMEOUT = "start-timeout";
	private static final String PREF_RESTART_MODULE_TIMEOUT = "restart-module-timeout";
	private static final String PREF_MODULE_START_TIMEOUT = "module-start-timeout";

	public static final String DEFAULT_PUBLISH_MANAGER = "com.ibm.wtp.server.core.publish.smart";

	private Preferences preferences;

	protected static ServerPreferences instance;

	/**
	 * ServerPreference constructor comment.
	 */
	private ServerPreferences() {
		super();
		preferences = ServerPlugin.getInstance().getPluginPreferences();
	}

	public static ServerPreferences getServerPreferences() {
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
	 * @param boolean
	 */
	public void setCreateResourcesInWorkspace(boolean b) {
		preferences.setValue(PREF_CREATE_IN_WORKSPACE, b);
		ServerPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the publisher preference.
	 *
	 * @return String
	 */
	public String getPublishManager() {
		return preferences.getString(PREF_PUBLISHER);
	}

	/**
	 * Returns the publisher preference.
	 *
	 * @return String
	 */
	public String getDefaultPublishManager() {
		return DEFAULT_PUBLISH_MANAGER;
	}

	/**
	 * Set whether servers will be automatically restarted when
	 * they need a restart.
	 *
	 * @param boolean
	 */
	public void setAutoRestarting(boolean value) {
		preferences.setValue(PREF_AUTO_RESTART, value);
		ServerPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Set whether publishing should happen before the server starts.
	 *
	 * @param boolean
	 */
	public void setAutoPublishing(boolean value) {
		preferences.setValue(PREF_AUTO_PUBLISH, value);
		ServerPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Sets the publisher to use.
	 *
	 * @param String id
	 */
	public void setPublishManager(String id) {
		preferences.setValue(PREF_PUBLISHER, id);
		ServerPlugin.getInstance().savePluginPreferences();
	}
	
	/**
	 * Returns whether changes to modules should be automatically fixed
	 * in the server configurations.
	 *
	 * @return byte
	 */
	public byte getModuleRepairStatus() {
		return (byte) preferences.getInt(PREF_AUTO_REPAIR_MODULES);
	}

	/**
	 * Returns the default auto module fix state.
	 *
	 * @return byte
	 */
	public byte getDefaultModuleRepairStatus() {
		return REPAIR_PROMPT;
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
	 * Sets whether changes to modules should be automatically fixed
	 * in the server configurations.
	 *
	 * @return byte
	 */
	public void setModuleRepairStatus(byte b) {
		preferences.setValue(PREF_AUTO_REPAIR_MODULES, b);
		ServerPlugin.getInstance().savePluginPreferences();
	}

	public void setDefaults() {
		preferences.setDefault(PREF_AUTO_PUBLISH, isDefaultAutoPublishing());
		preferences.setDefault(PREF_AUTO_RESTART, isDefaultAutoRestarting());
		preferences.setDefault(PREF_PUBLISHER, getDefaultPublishManager());
		preferences.setDefault(PREF_AUTO_REPAIR_MODULES, getDefaultModuleRepairStatus());
		preferences.setDefault(PREF_STARTUP_TIMEOUT, 210001);
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
