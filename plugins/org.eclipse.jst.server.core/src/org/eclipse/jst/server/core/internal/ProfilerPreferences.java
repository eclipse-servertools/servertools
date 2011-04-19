/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Helper class which wraps around the preference store so that options related to 
 * server profilers can be persisted. 
 */
public class ProfilerPreferences {
	
	private static ProfilerPreferences prefs = null;
	private IPreferencesService prefService;
	
	/* The qualifier (scope) in the preference store */
	private static final String PREF_PROFILER_QUALIFIER = "org.eclipse.jst.server.core.internal.profilers";
	/* The key used to store the selected profiler preference */
	private static final String PREF_SELECTED_PROFILER = "selected-profiler";
	
	
	/**
	 * Private Constructor - This should be a singleton class 
	 */
	private ProfilerPreferences() {
		prefService = Platform.getPreferencesService();		
	}
	
	/**
	 * Returns an instance of this class
	 * @return
	 */
	public static ProfilerPreferences getInstance() {
		if ( prefs == null ) 
			prefs = new ProfilerPreferences();
		return prefs;
	}
	
	/**
	 * Sets the profiler to use when profiling on server
	 * @param profilerId the id of the server profiler
	 */
	public void setServerProfilerId( String profilerId ) {
		Preferences node = new InstanceScope().getNode( PREF_PROFILER_QUALIFIER );
		node.put( PREF_SELECTED_PROFILER, profilerId );
		try {
			node.flush();			
		} catch (BackingStoreException e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not save server profiler preference", e);
			}
		}
	}
	
	/**
	 * Reutns the id of the current server profiler
	 * @return the id of the current server profiler
	 */
	public String getServerProfilerId() {
		return prefService.getString( PREF_PROFILER_QUALIFIER, PREF_SELECTED_PROFILER, null, null );
	}

}
