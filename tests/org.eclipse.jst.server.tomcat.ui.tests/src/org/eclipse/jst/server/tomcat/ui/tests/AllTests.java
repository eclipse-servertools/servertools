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
package org.eclipse.jst.server.tomcat.ui.tests;

import org.eclipse.jst.server.tomcat.core.tests.RuntimeLocation;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.jst.server.tomcat.ui.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExistenceTest.class);
		String s = System.getProperty("tomcat50Dir");
		//s = "D:\\Tools\\tomcat\\jakarta-tomcat-3.2.4";
		if (s != null && s.length() > 0) {
			RuntimeLocation.runtimeLocation = s;
			suite.addTestSuite(OpenEditorTestCase.class);
		}
		suite.addTestSuite(DialogsTestCase.class);
		//$JUnit-END$
		return suite;
	}
}