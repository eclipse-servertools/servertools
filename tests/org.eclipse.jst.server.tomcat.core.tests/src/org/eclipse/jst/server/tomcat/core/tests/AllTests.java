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
package org.eclipse.jst.server.tomcat.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.jst.server.tomcat.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExistenceTest.class);
		suite.addTest(new OrderedTestSuite(TomcatRuntimeTestCase.class));
		suite.addTest(new OrderedTestSuite(TomcatRuntimeTestCase2.class));
		suite.addTest(new OrderedTestSuite(TomcatServerTestCase2.class));
		//$JUnit-END$
		return suite;
	}
}