/**********************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.tests;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.ServerPreferences;

public class ServerPreferencesTestCase extends TestCase {
	protected static ServerPreferences prefs;	

	public void test00GetProperties() throws Exception {
		prefs = ServerPreferences.getInstance();
	}

	public void test01GetPref() throws Exception {
		prefs.isAutoPublishing();
	}
	
	public void test04SetPref() throws Exception {
		prefs.setAutoPublishing(false);
		assertFalse(prefs.isAutoPublishing());
	}
	
	public void test05SetPref() throws Exception {
		prefs.setAutoPublishing(true);
		assertTrue(prefs.isAutoPublishing());
	}

	public void test10DefaultPref() throws Exception {
		prefs.setAutoPublishing(prefs.isDefaultAutoPublishing());
		assertEquals(prefs.isAutoPublishing(), prefs.isDefaultAutoPublishing());
	}
}