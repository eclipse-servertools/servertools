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
package org.eclipse.jst.server.core.tests;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static IPath runtimeLocation;

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.jst.server.core.tests");
		//$JUnit-BEGIN$
		System.setProperty("wtp.autotest.noninteractive", "true");
		
		suite.addTestSuite(ExistenceTest.class);
		suite.addTest(new OrderedTestSuite(GenericRuntimeTestCase.class));
		
		suite.addTest(new OrderedTestSuite(J2EEModuleTestCase.class));
		suite.addTest(new OrderedTestSuite(ApplicationClientTestCase.class));
		suite.addTest(new OrderedTestSuite(ConnectorModuleTestCase.class));
		suite.addTest(new OrderedTestSuite(EJBModuleTestCase.class));
		suite.addTest(new OrderedTestSuite(WebModuleTestCase.class));
		suite.addTest(new OrderedTestSuite(EnterpriseApplicationTestCase.class));
		
		suite.addTest(new OrderedTestSuite(EJBBeanTestCase.class));
		suite.addTest(new OrderedTestSuite(ServletTestCase.class));
		suite.addTest(new OrderedTestSuite(JndiObjectTestCase.class));
		suite.addTest(new OrderedTestSuite(JndiLaunchableTestCase.class));
		
		suite.addTest(new OrderedTestSuite(RuntimeClasspathProviderDelegateTestCase.class));
		
		String s = System.getProperty("org.eclipse.jst.server.tomcat.32");
		//s = "D:\\Tools\\tomcat\\jakarta-tomcat-3.2.4";
		if (s != null && s.length() > 0) {
			if (!s.endsWith(File.separator))
				s += File.separator;
			runtimeLocation = new Path(s + "lib");
		}
		s = System.getProperty("org.eclipse.jst.server.tomcat.40");
		if (s != null && s.length() > 0) {
			if (!s.endsWith(File.separator))
				s += File.separator;
			runtimeLocation = new Path(s + "common" + File.separator + "lib");
		}
		
		// bug 160848
		/*
		if (runtimeLocation != null)
			suite.addTest(new OrderedTestSuite(ModuleTestCase.class));
		
		suite.addTest(new OrderedTestSuite(NoSourceTestCase.class));
		suite.addTest(new OrderedTestSuite(BinaryTestCase.class));
		*/
		//$JUnit-END$
		return suite;
	}
}