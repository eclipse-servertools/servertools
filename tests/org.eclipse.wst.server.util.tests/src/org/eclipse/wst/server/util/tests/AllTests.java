/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.util.tests;

import org.eclipse.wst.server.util.tests.dialog.PreferencesTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.wst.server.util.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExistenceTest.class);		
		suite.addTestSuite(PreferencesTestCase.class);
		//$JUnit-END$
		return suite;
	}
}