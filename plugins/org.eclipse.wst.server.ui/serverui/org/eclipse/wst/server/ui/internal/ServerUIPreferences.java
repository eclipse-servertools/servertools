/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Preferences;
/**
 * Helper class that stores preference information for
 * the server tools.
 */
public class ServerUIPreferences {
	private static final String PREF_PROMPT_IRREVERSIBLE = "prompt-irreversible";
	private static final String PREF_IMPORT_LOCATION = "import-location";
	private static final String PREF_SAVE_EDITORS = "save-editors";
	private static final String PREF_HOST_NAMES = "host-names";

	public static final byte SAVE_EDITORS_NEVER = 0;
	public static final byte SAVE_EDITORS_PROMPT = 1;
	public static final byte SAVE_EDITORS_AUTO = 2;

	private static final int MAX_HOSTNAMES = 10;

	private Preferences preferences;

	/**
	 * ServerUIPreference constructor comment.
	 */
	public ServerUIPreferences() {
		super();
		preferences = ServerUIPlugin.getInstance().getPluginPreferences();
	}

	public void setDefaults() {
		preferences.setDefault(PREF_PROMPT_IRREVERSIBLE, getDefaultPromptBeforeIrreversibleChange());
		preferences.setDefault(PREF_SAVE_EDITORS, getDefaultSaveEditors());
		preferences.setDefault(PREF_HOST_NAMES, "localhost");
	}

	/**
	 * Returns whether the user should be prompted before making an
	 * irreversible change in the editor.
	 * 
	 * @return boolean
	 */
	public boolean getPromptBeforeIrreversibleChange() {
		return preferences.getBoolean(PREF_PROMPT_IRREVERSIBLE);
	}

	/**
	 * Returns the default value of whether the user should be prompted
	 * before making an irreversible change in the editor.
	 *
	 * @return boolean
	 */
	public boolean getDefaultPromptBeforeIrreversibleChange() {
		return true;
	}

	/**
	 * Sets whether the user should be prompted before making an
	 * irreversible change in the editor.
	 *
	 * @return boolean
	 */
	public void setPromptBeforeIrreversibleChange(boolean b) {
		preferences.setValue(PREF_PROMPT_IRREVERSIBLE, b);
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
	 * @return java.lang.String
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
	 * @param byte
	 */
	public void setSaveEditors(byte b) {
		preferences.setValue(PREF_SAVE_EDITORS, b);
		ServerUIPlugin.getInstance().savePluginPreferences();
	}

	/**
	 * Return the list of most recently used hostnames.
	 * 
	 * @return
	 */
	public List getHostnames() {
		String s = preferences.getString(PREF_HOST_NAMES);
		StringTokenizer st = new StringTokenizer(s, "|*|");
		List list = new ArrayList();
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
		List list = getHostnames();
		
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
}