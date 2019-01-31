/**********************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.tests;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.ServerPreferences;

public class ServerPreferencesTestCase extends TestCase {
	protected static ServerPreferences prefs;	

	protected ServerPreferences getServerPrefs() {
		if (prefs == null) {
			prefs = ServerPreferences.getInstance();
		}
		return prefs;
	}

	public void testGetAutoPubPref() throws Exception {
		getServerPrefs().isAutoPublishing();
	}
	
	public void testSetAutoPubPrefFalse() throws Exception {
		getServerPrefs().setAutoPublishing(false);
		assertFalse(getServerPrefs().isAutoPublishing());
	}
	
	public void testSetAutoPubPrefTrue() throws Exception {
		getServerPrefs().setAutoPublishing(true);
		assertTrue(getServerPrefs().isAutoPublishing());
	}

	public void testDefaultAutoPubPref() throws Exception {
		getServerPrefs().setAutoPublishing(getServerPrefs().isDefaultAutoPublishing());
		assertEquals(getServerPrefs().isAutoPublishing(), getServerPrefs().isDefaultAutoPublishing());
	}
}