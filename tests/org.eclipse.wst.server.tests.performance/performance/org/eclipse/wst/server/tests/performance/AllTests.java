/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
  *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.tests.performance;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.wst.server.tests.performance");
		//$JUnit-BEGIN$
		suite.addTestSuite(StartupExtensionTestCase.class);
		suite.addTestSuite(ModuleFactoriesExtensionTestCase.class);
		//$JUnit-END$
		return suite;
	}
}