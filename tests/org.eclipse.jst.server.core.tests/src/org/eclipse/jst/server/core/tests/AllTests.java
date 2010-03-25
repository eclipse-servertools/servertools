/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.tests;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.core.tests.j2ee.*;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static IPath runtimeLocation;

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.jst.server.core.tests");
		//$JUnit-BEGIN$
		System.setProperty("wtp.autotest.noninteractive", "true");
		
		suite.addTestSuite(ExistenceTest.class);
		TestSuite subSuite = new TestSuite(GenericRuntimeTestCase.class);
		GenericRuntimeTestCase.addOrderedTests(subSuite);
		suite.addTest(subSuite);
		
		suite.addTestSuite(J2EEModuleTestCase.class);
		suite.addTestSuite(ApplicationClientTestCase.class);
		suite.addTestSuite(ConnectorModuleTestCase.class);
		suite.addTestSuite(EJBModuleTestCase.class);
		suite.addTestSuite(WebModuleTestCase.class);
		suite.addTestSuite(EnterpriseApplicationTestCase.class);
		
		suite.addTestSuite(EJBBeanTestCase.class);
		suite.addTestSuite(ServletTestCase.class);
		suite.addTestSuite(JndiObjectTestCase.class);
		suite.addTestSuite(JndiLaunchableTestCase.class);
		
		suite.addTestSuite(RuntimeClasspathProviderDelegateTestCase.class);
		
		String s = System.getProperty("org.eclipse.jst.server.tomcat.60");
		//s = "D:\\Tools\\tomcat\\jakarta-tomcat-3.2.4";
		if (s != null && s.length() > 0) {
			if (!s.endsWith(File.separator))
				s += File.separator;
			runtimeLocation = new Path(s + "lib");
		}
		//s = System.getProperty("org.eclipse.jst.server.tomcat.55");
		if (s != null && s.length() > 0) {
			if (!s.endsWith(File.separator))
				s += File.separator;
			runtimeLocation = new Path(s + "common" + File.separator + "lib");
		}
		
		// bug 160848
		
		if (runtimeLocation != null) {
			subSuite = new TestSuite(ModuleTestCase.class);
			ModuleTestCase.addOrderedTests(subSuite);
			suite.addTest(subSuite);
		}
		
		subSuite = new TestSuite(NoSourceTestCase.class);
		NoSourceTestCase.addOrderedTests(subSuite);
		suite.addTest(subSuite);
		subSuite = new TestSuite(BinaryTestCase.class);
		BinaryTestCase.addOrderedTests(subSuite);
		suite.addTest(subSuite);
		
		//$JUnit-END$
		return suite;
	}
}