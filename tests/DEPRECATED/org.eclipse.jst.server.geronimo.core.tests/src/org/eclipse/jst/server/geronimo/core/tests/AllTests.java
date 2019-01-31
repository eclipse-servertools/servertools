/**********************************************************************
 * Copyright (c) 2005, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
�*
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.geronimo.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.jst.server.geronimo.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExistenceTest.class);
		//$JUnit-END$
		return suite;
	}
}