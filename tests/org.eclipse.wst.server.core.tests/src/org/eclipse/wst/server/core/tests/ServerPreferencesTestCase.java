/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.ServerPreferences;

public class ServerPreferencesTestCase extends TestCase {
	protected static ServerPreferences prefs;	

	public static Test suite() {
		return new OrderedTestSuite(ServerPreferencesTestCase.class, "ServerPreferencesTestCase");
	}

	public void test00GetProperties() throws Exception {
		prefs = ServerPreferences.getInstance();
	}

	public void test01GetPref() throws Exception {
		prefs.isAutoPublishing();
	}

	public void test02GetPref() throws Exception {
		prefs.isAutoRestarting();
	}

	public void test03GetPref() throws Exception {
		prefs.isCreateResourcesInWorkspace();
	}
	
	public void test04SetPref() throws Exception {
		prefs.setAutoPublishing(false);
		assertFalse(prefs.isAutoPublishing());
	}
	
	public void test05SetPref() throws Exception {
		prefs.setAutoPublishing(true);
		assertTrue(prefs.isAutoPublishing());
	}
	
	public void test06SetPref() throws Exception {
		prefs.setAutoRestarting(false);
		assertFalse(prefs.isAutoRestarting());
	}
	
	public void test07SetPref() throws Exception {
		prefs.setAutoRestarting(true);
		assertTrue(prefs.isAutoRestarting());
	}
	
	public void test08SetPref() throws Exception {
		prefs.setCreateResourcesInWorkspace(false);
		assertFalse(prefs.isCreateResourcesInWorkspace());
	}
	
	public void test09SetPref() throws Exception {
		prefs.setCreateResourcesInWorkspace(true);
		assertTrue(prefs.isCreateResourcesInWorkspace());
	}
	
	public void test10DefaultPref() throws Exception {
		prefs.setAutoPublishing(prefs.isDefaultAutoPublishing());
		assertEquals(prefs.isAutoPublishing(), prefs.isDefaultAutoPublishing());
	}
	
	public void test11DefaultPref() throws Exception {
		prefs.setAutoRestarting(prefs.isDefaultAutoRestarting());
		assertEquals(prefs.isAutoRestarting(), prefs.isDefaultAutoRestarting());
	}
	
	public void test12DefaultPref() throws Exception {
		prefs.setCreateResourcesInWorkspace(prefs.isDefaultCreateResourcesInWorkspace());
		assertEquals(prefs.isCreateResourcesInWorkspace(), prefs.isDefaultCreateResourcesInWorkspace());
	}
}