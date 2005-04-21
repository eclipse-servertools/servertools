/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests;

import org.eclipse.wst.server.ui.ServerLaunchConfigurationTab;
import junit.framework.Test;
import junit.framework.TestCase;

public class ServerLaunchConfigurationTabTestCase extends TestCase {
	protected static ServerLaunchConfigurationTab tab;
	
	public static Test suite() {
		return new OrderedTestSuite(ServerLaunchConfigurationTabTestCase.class, "ServerLaunchConfigurationTabTestCase");
	}

	public void test00CreateTab() {
		tab = new ServerLaunchConfigurationTab();
	}
	
	public void test01CreateTab() {
		tab = new ServerLaunchConfigurationTab(new String[] {"test"});
	}
	
	public void test02CreateControl() {
		try {
			tab.createControl(null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test03SetDefaults() {
		try {
			tab.setDefaults(null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test04InitializeFrom() {
		try {
			tab.initializeFrom(null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test05PerformApply() {
		try {
			tab.performApply(null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test06IsValid() {
		try {
			tab.isValid(null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test07GetImage() {
		tab.getImage();
	}
	
	public void test08GetName() {
		tab.getName();
	}

	public void test09TestProtectedMethods() {
		class MyLaunchTab extends ServerLaunchConfigurationTab {
			public void testProtected() {
				try {
					handleServerSelection();
				} catch (Exception e) {
					// ignore
				}
			}
		}
		MyLaunchTab mlt = new MyLaunchTab();
		mlt.testProtected();
	}
}