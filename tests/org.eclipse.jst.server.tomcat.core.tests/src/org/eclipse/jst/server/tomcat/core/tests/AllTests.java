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
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jst.server.tomcat.core.tests.internal.UtilTestCase;
import org.eclipse.jst.server.tomcat.core.tests.internal.XmlTestCase;
import org.eclipse.jst.server.tomcat.core.tests.module.DeleteModuleTestCase;
import org.eclipse.jst.server.tomcat.core.tests.module.ModuleTestCase;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.jst.server.tomcat.core.tests");
		//$JUnit-BEGIN$
		System.setProperty("wtp.autotest.noninteractive", "true");
		
		suite.addTestSuite(ExistenceTest.class);
		suite.addTest(TomcatRuntimeTestCase.getOrderedTests());
		suite.addTest(ModuleTestCase.getOrderedTests());
		
		String s = System.getProperty("org.eclipse.jst.server.tomcat.32"); 
		//s = "D:\\Tools\\tomcat\\jakarta-tomcat-3.2.4";
		if (s != null && s.length() > 0) {
			RuntimeLocation.runtimeLocation = s;
			TestSuite subSuite = new TestSuite(Tomcat32RuntimeTestCase.class);
			Tomcat32RuntimeTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
			subSuite = new TestSuite();
			Tomcat32ServerTestCase tc = new Tomcat32ServerTestCase();
			subSuite.addTest(tc.getOrderedTests(Tomcat32ServerTestCase.class));			
			Tomcat32ServerTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
		} else {
			System.err.println("Warning: Tomcat 3.2 not found - tests skipped");
		}
		
		s = System.getProperty("org.eclipse.jst.server.tomcat.40");
		if (s != null && s.length() > 0) {
			RuntimeLocation.runtimeLocation = s;
			TestSuite subSuite = new TestSuite(Tomcat40RuntimeTestCase.class);
			Tomcat40RuntimeTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
			subSuite = new TestSuite();
			Tomcat40ServerTestCase tc = new Tomcat40ServerTestCase();
			subSuite.addTest(tc.getOrderedTests(Tomcat40ServerTestCase.class));				
			Tomcat40ServerTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
		} else {
			System.err.println("Warning: Tomcat 4.0 not found - tests skipped");
		}
		
		s = System.getProperty("org.eclipse.jst.server.tomcat.41");
		if (s != null && s.length() > 0) {
			RuntimeLocation.runtimeLocation = s;
			TestSuite subSuite = new TestSuite(Tomcat41RuntimeTestCase.class);
			Tomcat41RuntimeTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
			subSuite = new TestSuite();
			Tomcat41ServerTestCase tc = new Tomcat41ServerTestCase();
			subSuite.addTest(tc.getOrderedTests(Tomcat41ServerTestCase.class));		
			Tomcat41ServerTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
		} else {
			System.err.println("Warning: Tomcat 4.1 not found - tests skipped");
		}
		
		s = System.getProperty("org.eclipse.jst.server.tomcat.50");
		//s = "D:\\Tools\\tomcat\\jakarta-tomcat-5.0.19";
		if (s != null && s.length() > 0) {
			RuntimeLocation.runtimeLocation = s;
			TestSuite subSuite = new TestSuite(Tomcat50RuntimeTestCase.class);
			Tomcat50RuntimeTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
			subSuite = new TestSuite();
			Tomcat50ServerTestCase tc = new Tomcat50ServerTestCase();
			subSuite.addTest(tc.getOrderedTests(Tomcat50ServerTestCase.class));			
			Tomcat50ServerTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
		} else {
			System.err.println("Warning: Tomcat 5.0 not found - tests skipped");
		}
		
		s = System.getProperty("org.eclipse.jst.server.tomcat.55");
		if (s != null && s.length() > 0) {
			RuntimeLocation.runtimeLocation = s;
			TestSuite subSuite = new TestSuite(Tomcat55RuntimeTestCase.class);
			Tomcat55RuntimeTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
			subSuite = new TestSuite();
			Tomcat55ServerTestCase tc = new Tomcat55ServerTestCase();
			subSuite.addTest(tc.getOrderedTests(Tomcat55ServerTestCase.class));
			Tomcat55ServerTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
		} else {
			System.err.println("Warning: Tomcat 5.5 not found - tests skipped");
		}
		
		s = System.getProperty("org.eclipse.jst.server.tomcat.60");
		if (s != null && s.length() > 0) {
			RuntimeLocation.runtimeLocation = s;
			TestSuite subSuite = new TestSuite(Tomcat60RuntimeTestCase.class);
			Tomcat60RuntimeTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
			subSuite = new TestSuite();
			Tomcat60ServerTestCase tc = new Tomcat60ServerTestCase();
			subSuite.addTest(tc.getOrderedTests(Tomcat60ServerTestCase.class));
			Tomcat60ServerTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
		} else {
			System.err.println("Warning: Tomcat 6.0 not found - tests skipped");
		}

		// Note that Tomcat 7.0 requires Java SE 6
		s = System.getProperty("org.eclipse.jst.server.tomcat.70");
		if (s != null && s.length() > 0) {
			RuntimeLocation.runtimeLocation = s;
			TestSuite subSuite = new TestSuite(Tomcat70RuntimeTestCase.class);
			Tomcat70RuntimeTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
			subSuite = new TestSuite();
			Tomcat70ServerTestCase tc = new Tomcat70ServerTestCase();
			subSuite.addTest(tc.getOrderedTests(Tomcat70ServerTestCase.class));
			Tomcat70ServerTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
		} else {
			System.err.println("Warning: Tomcat 7.0 not found - tests skipped");
		}

		suite.addTestSuite(UtilTestCase.class);
		suite.addTestSuite(XmlTestCase.class);
		
		suite.addTestSuite(DeleteModuleTestCase.class);
		//$JUnit-END$
		return suite;
	}
}
