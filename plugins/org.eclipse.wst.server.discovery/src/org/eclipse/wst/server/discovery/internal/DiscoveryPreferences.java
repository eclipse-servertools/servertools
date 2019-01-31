/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
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
package org.eclipse.wst.server.discovery.internal;

import org.eclipse.core.runtime.Preferences;
/**
 * Helper class that stores preference information for Discovery.
 */
public class DiscoveryPreferences {

	private static final String PREF_CACHE_FREQUENCY = "cache-frequency";
	private static final String PREF_CACHE_LAST_UPDATED_DATE = "cache-lastUpdatedDate";

	private Preferences preferences;

	private static DiscoveryPreferences instance;

	/**
	 * ServerUIPreference constructor comment.
	 */
	private DiscoveryPreferences() {
		super();
		preferences = Activator.getDefault().getPluginPreferences();
		setDefaults();
	}

	public static DiscoveryPreferences getInstance() {
		if (instance == null)
			instance = new DiscoveryPreferences();
		return instance;
	}

	private void setDefaults() {
		preferences.setDefault(PREF_CACHE_FREQUENCY, 2);
		preferences.setDefault(PREF_CACHE_LAST_UPDATED_DATE, Messages.cacheUpdate_Never);
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
		Activator.getDefault().savePluginPreferences();
	}

	/**
	 * Returns the cache last updated date.
	 * 
	 * @return String
	 */
	public String getCacheLastUpdatedDate() {
		return preferences.getString(PREF_CACHE_LAST_UPDATED_DATE);
	}
	
	/**
	 * Sets the last updated cache date
	 *
	 */
	public void setCacheLastUpdatedDate(String lastUpdateDate) {
		preferences.setValue(PREF_CACHE_LAST_UPDATED_DATE, lastUpdateDate);
		Activator.getDefault().savePluginPreferences();
	}
}