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
		suite.addTest(new OrderedTestSuite(MonitorTestCase.class));
		suite.addTest(new OrderedTestSuite(MonitorListenerTestCase.class));
		//$JUnit-END$
		return suite;
	}
}