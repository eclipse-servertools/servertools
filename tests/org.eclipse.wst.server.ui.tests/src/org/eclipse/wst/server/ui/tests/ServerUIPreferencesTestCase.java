/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests;

import junit.framework.TestCase;

import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.ServerUIPreferences;

public class ServerUIPreferencesTestCase extends TestCase {
	protected static ServerUIPreferences prefs;

	public void testAll() throws Exception {
		prefs = ServerUIPlugin.getPreferences();

		prefs.getSaveEditors();

		prefs.setSaveEditors(ServerUIPreferences.SAVE_EDITORS_ALWAYS);
		assertEquals(prefs.getSaveEditors(), ServerUIPreferences.SAVE_EDITORS_ALWAYS);

		prefs.setSaveEditors(ServerUIPreferences.SAVE_EDITORS_NEVER);
		assertEquals(prefs.getSaveEditors(), ServerUIPreferences.SAVE_EDITORS_NEVER);

		prefs.setSaveEditors(ServerUIPreferences.SAVE_EDITORS_PROMPT);
		assertEquals(prefs.getSaveEditors(), ServerUIPreferences.SAVE_EDITORS_PROMPT);

		prefs.setLaunchMode(ServerUIPreferences.LAUNCH_MODE_RESTART);
		assertEquals(prefs.getLaunchMode(), ServerUIPreferences.LAUNCH_MODE_RESTART);

		prefs.setLaunchMode(ServerUIPreferences.LAUNCH_MODE_CONTINUE);
		assertEquals(prefs.getLaunchMode(), ServerUIPreferences.LAUNCH_MODE_CONTINUE);

		prefs.setLaunchMode(ServerUIPreferences.LAUNCH_MODE_PROMPT);
		assertEquals(prefs.getLaunchMode(), ServerUIPreferences.LAUNCH_MODE_PROMPT);

		prefs.setLaunchMode2(ServerUIPreferences.LAUNCH_MODE2_RESTART);
		assertEquals(prefs.getLaunchMode2(), ServerUIPreferences.LAUNCH_MODE2_RESTART);

		prefs.setLaunchMode2(ServerUIPreferences.LAUNCH_MODE2_DISABLE_BREAKPOINTS);
		assertEquals(prefs.getLaunchMode2(), ServerUIPreferences.LAUNCH_MODE2_DISABLE_BREAKPOINTS);

		prefs.setLaunchMode2(ServerUIPreferences.LAUNCH_MODE2_CONTINUE);
		assertEquals(prefs.getLaunchMode2(), ServerUIPreferences.LAUNCH_MODE2_CONTINUE);

		prefs.setLaunchMode2(ServerUIPreferences.LAUNCH_MODE2_PROMPT);
		assertEquals(prefs.getLaunchMode2(), ServerUIPreferences.LAUNCH_MODE2_PROMPT);

		prefs.setEnableBreakpoints(ServerUIPreferences.ENABLE_BREAKPOINTS_ALWAYS);
		assertEquals(prefs.getEnableBreakpoints(), ServerUIPreferences.ENABLE_BREAKPOINTS_ALWAYS);

		prefs.setEnableBreakpoints(ServerUIPreferences.ENABLE_BREAKPOINTS_NEVER);
		assertEquals(prefs.getEnableBreakpoints(), ServerUIPreferences.ENABLE_BREAKPOINTS_NEVER);

		prefs.setEnableBreakpoints(ServerUIPreferences.ENABLE_BREAKPOINTS_PROMPT);
		assertEquals(prefs.getEnableBreakpoints(), ServerUIPreferences.ENABLE_BREAKPOINTS_PROMPT);

		prefs.setSaveEditors(prefs.getDefaultSaveEditors());
		assertEquals(prefs.getSaveEditors(), prefs.getDefaultSaveEditors());
	}
}