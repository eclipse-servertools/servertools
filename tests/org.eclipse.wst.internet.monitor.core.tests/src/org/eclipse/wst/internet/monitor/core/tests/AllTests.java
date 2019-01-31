/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.core.tests;

import org.eclipse.wst.internet.monitor.core.tests.extension.*;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.wst.internet.monitor.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExistenceTest.class);
		suite.addTestSuite(ContentFiltersTestCase.class);
		TestSuite subSuite = new TestSuite(MonitorTestCase.class);
		MonitorTestCase.addOrderedTests(subSuite);
		suite.addTest(subSuite);
		subSuite = new TestSuite(MonitorListenerTestCase.class);
		MonitorListenerTestCase.addOrderedTests(subSuite);
		suite.addTest(subSuite);
		subSuite = new TestSuite(RequestTestCase.class);
		RequestTestCase.addOrderedTests(subSuite);
		suite.addTest(subSuite);
		suite.addTestSuite(ContentFilterTestCase.class);
		//$JUnit-END$
		return suite;
	}
}