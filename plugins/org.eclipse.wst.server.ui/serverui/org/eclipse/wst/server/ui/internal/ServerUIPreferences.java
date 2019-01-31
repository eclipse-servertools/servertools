/*******************************************************************************
 * Copyright (c) 2003, 2016 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Preferences;
/**
 * Helper class that stores preference information for server tools UI.
 */
public class ServerUIPreferences {
	private static final String PREF_IMPORT_LOCATION = "import-location";
	private static final String PREF_SAVE_EDITORS = "save-editors";
	private static final String PREF_HOST_NAMES = "host-names";
	private static final String PREF_SHOW_ON_ACTIVITY = "show-on-activity";
	private static final String PREF_LAUNCH_MODE = "launch-mode";
	private static final String PREF_LAUNCH_MODE2 = "launch-mode2";
	private static final String PREF_ENABLE_BREAKPOINTS = "enable-breakpoints";
	private static final String PREF_RESTART = "restart";
	private static final String PREF_CREATE_SERVER_WITH_RUNTIME = "create-server";
	private static final String PREF_PUBLISH_ON_ADD_REMOVE = "publish-on-add-remove";
	private static final String PREF_EXT_ADAPTER = "external-adapter";
	private static final String PREF_CACHE_FREQUENCY = "cache-frequency";
	private static final String PREF_CACHE_LAST_UPDATED_DATE = "cache-lastUpdatedDate";
	private static final String PREF_DO_NOT_SHOW_REMOVE_MODULE_WARNING = "do-not-show-remove-module-warning";

	public static final byte SAVE_EDITORS_ALWAYS = 2;
	public static final byte SAVE_EDITORS_NEVER = 0;
	public static final byte SAVE_EDITORS_PROMPT = 1;

	public static final byte LAUNCH_MODE_PROMPT = 0;
	public static final byte LAUNCH_MODE_CONTINUE = 1;
	public static final byte LAUNCH_MODE_RESTART = 2;

	public static final byte LAUNCH_MODE2_PROMPT = 0;
	public static final byte LAUNCH_MODE2_CONTINUE = 1;
	public static final byte LAUNCH_MODE2_RESTART = 2;
	public static final byte LAUNCH_MODE2_DISABLE_BREAKPOINTS = 3;

	public static final byte ENABLE_BREAKPOINTS_PROMPT = 0;
	public static final byte ENABLE_BREAKPOINTS_ALWAYS = 1;
	public static final byte ENABLE_BREAKPOINTS_NEVER = 2;

	public static final byte RESTART_PROMPT = 0;
	public static final byte RESTART_ALWAYS = 1;
	public static final byte RESTART_NEVER = 2;

	private static final int MAX_HOSTNAMES = 10;

	private Preferences preferences;

	private static ServerUIPreferences instance;

	/**
	 * ServerUIPreference constructor comment.
	 */
	private ServerUIPreferences() {
		super();
		preferences = ServerUIPlugin.getInstance().getPluginPreferences();
		setDefaults();
	}

	public static ServerUIPreferences getInstance() {
		if (instance == null)
			instance = new ServerUIPreferences();
		return instance;
	}

	private void setDefaults() {
		preferences.setDefault(PREF_LAUNCH_MODE, getDefaultLaunchMode());
		preferences.setDefault(PREF_LAUNCH_MODE2, getDefaultLaunchMode2());
		preferences.setDefault(PREF_ENABLE_BREAKPOINTS, getDefaultEnableBreakpoints());
		preferences.setDefault(PREF_RESTART, getDefaultRestart());
		preferences.setDefault(PREF_SAVE_EDITORS, getDefaultSaveEditors());
		preferences.setDefault(PREF_HOST_NAMES, "localhost");
		preferences.setDefault(PREF_SHOW_ON_ACTIVITY, true);
		preferences.setDefault(PREF_CREATE_SERVER_WITH_RUNTIME, false);
		preferences.setDefault(PREF_PUBLISH_ON_ADD_REMOVE, true);
		preferences.setDefault(PREF_EXT_ADAPTER, true);
		preferences.setDefault(PREF_CACHE_FREQUENCY, 2);
		preferences.setDefault(PREF_DO_NOT_SHOW_REMOVE_MODULE_WARNING, false);
	}

	/**
	 * Returns the default value of whether the user should be prompted
	 * when the launch mode of the server doesn't match.
	 *
	 * @return byte
	 */
	public byte getDefaultLaunchMode() {
		return LAUNCH_MODE_PROMPT;
	}

	/**
	 * Returns whether the user should be prompted when the launch mode
	 * of the server doesn't match.
	 * 
	 * @return int
	 */
	public int getLaunchMode() {
		return preferences.getInt(PREF_LAUNCH_MODE);
	}

	/**
	 * Sets whether the user should be prompted when the launch mode
	 * of the server doesn't match.
	 *
	 * @param b a launch mode constant
	 */
	public void setLaunchMode(int b) {
		preferences.setValue(PREF_LAUNCH_MODE, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the default value of whether the user should be prompted
	 * when the launch mode of the server doesn't match.
	 *
	 * @return byte
	 */
	public byte getDefaultLaunchMode2() {
		return LAUNCH_MODE2_PROMPT;
	}

	/**
	 * Returns whether the user should be prompted when the launch mode
	 * of the server doesn't match.
	 * 
	 * @return int
	 */
	public int getLaunchMode2() {
		return preferences.getInt(PREF_LAUNCH_MODE2);
	}

	/**
	 * Sets whether the user should be prompted when the launch mode
	 * of the server doesn't match.
	 *
	 * @param b a launch mode constant
	 */
	public void setLaunchMode2(int b) {
		preferences.setValue(PREF_LAUNCH_MODE2, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the default value of whether the user should be prompted
	 * when the breakpoint enablement doesn't match the server state.
	 *
	 * @return int
	 */
	public byte getDefaultEnableBreakpoints() {
		return ENABLE_BREAKPOINTS_PROMPT;
	}

	/**
	 * Returns whether the user should be prompted when the breakpoint
	 * enablement doesn't match the server state.
	 * 
	 * @return int
	 */
	public int getEnableBreakpoints() {
		return preferences.getInt(PREF_ENABLE_BREAKPOINTS);
	}

	/**
	 * Sets whether the user should be prompted when the breakpoint
	 * enablement doesn't match the server state.
	 *
	 * @param b a breakpoint enablement constant
	 */
	public void setEnableBreakpoints(int b) {
		preferences.setValue(PREF_ENABLE_BREAKPOINTS, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the default value of whether the user should be prompted
	 * when the server requires restarting.
	 *
	 * @return int
	 */
	public byte getDefaultRestart() {
		return RESTART_PROMPT;
	}

	/**
	 * Returns whether the user should be prompted when the server requires
	 * restarting.
	 * 
	 * @return int
	 */
	public int getRestart() {
		return preferences.getInt(PREF_RESTART);
	}

	/**
	 * Sets whether the user should be prompted when the server requires restarting.
	 *
	 * @param b a breakpoint enablement constant
	 */
	public void setRestart(int b) {
		preferences.setValue(PREF_RESTART, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the import location.
	 *
	 * @return java.lang.String
	 */
	public String getImportLocation() {
		return preferences.getString(PREF_IMPORT_LOCATION);
	}

	/**
	 * Sets the import location.
	 *
	 * @param s the import location
	 */
	public void setImportLocation(String s) {
		preferences.setValue(PREF_IMPORT_LOCATION, s);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the default setting for saving editors before launching.
	 * 
	 * @return byte
	 */
	public byte getDefaultSaveEditors() {
		return SAVE_EDITORS_PROMPT;
	}

	/**
	 * Returns the setting for saving editors before launching.
	 * 
	 * @return byte
	 */
	public byte getSaveEditors() {
		return (byte) preferences.getInt(PREF_SAVE_EDITORS);
	}

	/**
	 * Sets the value for saving editors before launching.
	 * 
	 * @param b
	 */
	public void setSaveEditors(byte b) {
		preferences.setValue(PREF_SAVE_EDITORS, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the default setting for opening the servers view on activity.
	 * 
	 * @return boolean
	 */
	public boolean getDefaultShowOnActivity() {
		return true;
	}

	/**
	 * Returns the setting for opening the servers view on activity.
	 * 
	 * @return boolean
	 */
	public boolean getShowOnActivity() {
		return preferences.getBoolean(PREF_SHOW_ON_ACTIVITY);
	}

	/**
	 * Sets the value for opening the servers view on activity.
	 * 
	 * @param b
	 */
	public void setShowOnActivity(boolean b) {
		preferences.setValue(PREF_SHOW_ON_ACTIVITY, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Return the list of most recently used hostnames.
	 * 
	 * @return the hostnames
	 */
	public List<String> getHostnames() {
		String s = preferences.getString(PREF_HOST_NAMES);
		StringTokenizer st = new StringTokenizer(s, "|*|");
		List<String> list = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}
		return list;
	}

	/**
	 * Add a new hostname to the most recently used list.
	 *  
	 * @param hostname
	 */
	public void addHostname(String hostname) {
		if ("localhost".equals(hostname))
			return;
		
		List<String> list = getHostnames();
		
		// remove duplicates
		if (list.contains(hostname))
			list.remove(hostname);
		
		// always add second (leave localhost first)
		list.add(1, hostname);
		
		// remove least used hostname
		if (list.size() > MAX_HOSTNAMES)
			list.remove(list.size() - 1);
		
		StringBuffer sb = new StringBuffer();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			sb.append(s);
			sb.append("|*|");
		}
		preferences.setValue(PREF_HOST_NAMES, sb.toString());
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the setting for whether a server should be created with runtimes
	 * when possible.
	 * 
	 * @return boolean
	 */
	public boolean getCreateServerWithRuntime() {
		return preferences.getBoolean(PREF_CREATE_SERVER_WITH_RUNTIME);
	}

	/**
	 * Sets the value for whether a server should be created with runtimes
	 * when possible.
	 * 
	 * @param b
	 */
	public void setCreateServerWithRuntime(boolean b) {
		preferences.setValue(PREF_CREATE_SERVER_WITH_RUNTIME, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the setting for publishing when modules are added or removed.
	 * 
	 * @return boolean
	 */
	public boolean getPublishOnAddRemoveModule() {
		return preferences.getBoolean(PREF_PUBLISH_ON_ADD_REMOVE);
	}

	/**
	 * Sets the value for publishing when modules are added or removed.
	 * 
	 * @param b
	 */
	public void setPublishOnAddRemoveModule(boolean b) {
		preferences.setValue(PREF_PUBLISH_ON_ADD_REMOVE, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}
	
	/**
	 * Returns whether the user should be prompted when the launch mode
	 * of the server doesn't match.
	 * 
	 * @return int
	 */
	public boolean getExtAdapter() {
		return preferences.getBoolean(PREF_EXT_ADAPTER);
	}

	/**
	 * Sets whether the user should be prompted when the launch mode
	 * of the server doesn't match.
	 *
	 * @param b a launch mode constant
	 */
	public void setExtAdapter(boolean b) {
		preferences.setValue(PREF_EXT_ADAPTER, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}
	
	/**
	 * Returns the frequency for cache update.
	 * 
	 * @return String
	 */
	public int getCacheFrequency() {
		return preferences.getInt(PREF_CACHE_FREQUENCY);
	}

	/**
	 * Sets the frequency for cache update.
	 *
	 * @param frequency - 0-Manual, 1-Fortnightly, 2-Monthly and 3-Quarterly 
	 */
	public void setCacheFrequency(int frequency) {
		preferences.setValue(PREF_CACHE_FREQUENCY, frequency);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Returns the cache last updated date.
	 * 
	 * @return String
	 */
	public String getPrefCacheLastUpdatedDate() {
		return preferences.getString(PREF_CACHE_LAST_UPDATED_DATE);
	}
	
	/**
	 * Sets the frequency for cache update.
	 *
	 * @param frequency - 0-Manual, 1-Fortnightly, 2-Monthly and 3-Quarterly 
	 */
	public void setPrefCacheLastUpdatedDate(int frequency) {
		preferences.setValue(PREF_CACHE_FREQUENCY, frequency);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}
	
	/**
	 * Returns the option to show the warning dialog if a module or modules are to be removed from the server.
	 * 
	 * @return boolean - false: will show the dialog; true: will not show the dialog
	 */
	public boolean getDoNotShowRemoveModuleWarning() {
		return preferences.getBoolean(PREF_DO_NOT_SHOW_REMOVE_MODULE_WARNING);
	}

	/**
	 * Sets the preference to show whether the warning dialog will appear or not when a module or modules are to
	 * be removed from the server
	 * 
	 * @param b - false: will show the dialog; true: will not show the dialog
	 */
	public void setDoNotShowRemoveModuleWarning(boolean b) {
		preferences.setValue(PREF_DO_NOT_SHOW_REMOVE_MODULE_WARNING, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}
}