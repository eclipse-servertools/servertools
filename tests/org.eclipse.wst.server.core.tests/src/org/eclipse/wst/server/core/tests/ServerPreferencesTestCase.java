/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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

import org.eclipse.wst.server.core.IServerPreferences;
import org.eclipse.wst.server.core.ServerCore;

public class ServerPreferencesTestCase extends TestCase {
	protected static IServerPreferences prefs;	

	public static Test suite() {
		return new OrderedTestSuite(ServerPreferencesTestCase.class, "ServerPreferencesTestCase");
	}

	public void test00GetProperties() throws Exception {
		prefs = ServerCore.getServerPreferences();
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
	
	public void test04GetPref() throws Exception {
		//prefs.i();
	}
}