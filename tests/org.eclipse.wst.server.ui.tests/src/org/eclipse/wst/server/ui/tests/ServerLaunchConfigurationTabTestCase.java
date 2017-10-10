/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

import org.eclipse.wst.server.ui.ServerLaunchConfigurationTab;

/* Note: These tests may be executed in any order.  Because null is used as most
 * arguments, the order doesn't currently matter.  If non-null arguments are used,
 * it may be necessary to rewrite the tests to make them truly order independent.
 */

public class ServerLaunchConfigurationTabTestCase extends TestCase {
	protected static ServerLaunchConfigurationTab tab;

	protected ServerLaunchConfigurationTab getServerLaunchConfigurationTab() {
		if (tab == null) {
			tab = new ServerLaunchConfigurationTab(new String[]{"test"});
		}
		return tab;
	}
	
	public void testCreateTabAllTypes() {
		new ServerLaunchConfigurationTab();
	}

	public void testCreateControl() {
		try {
			getServerLaunchConfigurationTab().createControl(null);
		}
		catch (Exception e) {
			// ignore
		}
	}

	public void testSetDefaults() {
		try {
			getServerLaunchConfigurationTab().setDefaults(null);
		}
		catch (Exception e) {
			// ignore
		}
	}

	public void testInitializeFrom() {
		try {
			getServerLaunchConfigurationTab().initializeFrom(null);
		}
		catch (Exception e) {
			// ignore
		}
	}

	public void testPerformApply() {
		try {
			getServerLaunchConfigurationTab().performApply(null);
		}
		catch (Exception e) {
			// ignore
		}
	}

	public void testIsValid() {
		try {
			getServerLaunchConfigurationTab().isValid(null);
		}
		catch (Exception e) {
			// ignore
		}
	}

	public void testGetImage() {
		getServerLaunchConfigurationTab().getImage();
	}

	public void testGetName() {
		getServerLaunchConfigurationTab().getName();
	}

	public void test09TestProtectedMethods() {
		class MyLaunchTab extends ServerLaunchConfigurationTab {
			public void testProtected() {
				try {
					handleServerSelection();
				}
				catch (Exception e) {
					// ignore
				}
			}
		}
		MyLaunchTab mlt = new MyLaunchTab();
		mlt.testProtected();
	}
}