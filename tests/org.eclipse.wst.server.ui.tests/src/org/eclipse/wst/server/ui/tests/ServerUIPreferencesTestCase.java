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
package org.eclipse.wst.server.ui.tests;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.ServerUIPreferences;

public class ServerUIPreferencesTestCase extends TestCase {
	protected static ServerUIPreferences prefs;	

	public static Test suite() {
		return new OrderedTestSuite(ServerUIPreferencesTestCase.class, "ServerUIPreferencesTestCase");
	}

	public void test00GetProperties() throws Exception {
		prefs = ServerUIPlugin.getPreferences();
	}

	public void test01GetPref() throws Exception {
		prefs.getPromptBeforeIrreversibleChange();
	}

	public void test02GetPref() throws Exception {
		prefs.getSaveEditors();
	}
	
	public void test03SetPref() throws Exception {
		prefs.setPromptBeforeIrreversibleChange(false);
		assertFalse(prefs.getPromptBeforeIrreversibleChange());
	}
	
	public void test04SetPref() throws Exception {
		prefs.setPromptBeforeIrreversibleChange(true);
		assertTrue(prefs.getPromptBeforeIrreversibleChange());
	}
	
	public void test05SetPref() throws Exception {
		prefs.setSaveEditors(ServerUIPreferences.SAVE_EDITORS_AUTO);
		assertEquals(prefs.getSaveEditors(), ServerUIPreferences.SAVE_EDITORS_AUTO);
	}
	
	public void test06SetPref() throws Exception {
		prefs.setSaveEditors(ServerUIPreferences.SAVE_EDITORS_NEVER);
		assertEquals(prefs.getSaveEditors(), ServerUIPreferences.SAVE_EDITORS_NEVER);
	}
	
	public void test07SetPref() throws Exception {
		prefs.setSaveEditors(ServerUIPreferences.SAVE_EDITORS_PROMPT);
		assertEquals(prefs.getSaveEditors(), ServerUIPreferences.SAVE_EDITORS_PROMPT);
	}
	
	public void test08DefaultPref() throws Exception {
		prefs.setPromptBeforeIrreversibleChange(prefs.getDefaultPromptBeforeIrreversibleChange());
		assertEquals(prefs.getPromptBeforeIrreversibleChange(), prefs.getDefaultPromptBeforeIrreversibleChange());
	}
	
	public void test09DefaultPref() throws Exception {
		prefs.setSaveEditors(prefs.getDefaultSaveEditors());
		assertEquals(prefs.getSaveEditors(), prefs.getDefaultSaveEditors());
	}
}