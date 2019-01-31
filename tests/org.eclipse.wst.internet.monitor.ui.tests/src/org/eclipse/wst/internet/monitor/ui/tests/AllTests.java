/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.internet.monitor.ui.tests.extension.ContentViewersTestCase;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.wst.internet.monitor.ui.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExistenceTest.class);
		suite.addTest(ContentViewersTestCase.getOrderedTests());
		suite.addTestSuite(MonitorUICoreTest.class);
		
		suite.addTestSuite(DialogsTestCase.class);
		suite.addTestSuite(PreferencesTestCase.class);
		suite.addTestSuite(ViewTestCase.class);
		//$JUnit-END$
		return suite;
	}
}