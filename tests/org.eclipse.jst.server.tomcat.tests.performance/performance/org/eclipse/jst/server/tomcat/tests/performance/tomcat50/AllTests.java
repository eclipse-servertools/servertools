/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.tests.performance.tomcat50;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jst.server.tomcat.core.tests.RuntimeLocation;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.jst.server.tomcat.tests.performance.tomcat50");
		//$JUnit-BEGIN$		
		System.setProperty("wtp.autotest.noninteractive", "true");
		
		// Bug 107442 against Eclipse. Performance problem in Form based editors
		// because FormUtil.computeWrapSize() calls BreakIterator.getWordInstance(),
		// which takes 1.1s on the first load
		
		ResourcesPlugin.getWorkspace().getDescription().setAutoBuilding(false);
		
		suite.addTestSuite(CreateModulesTestCase.class);
		suite.addTestSuite(BuildFullTestCase.class);
		
		String s = System.getProperty("org.eclipse.jst.server.tomcat.50");
		//s = "D:\\Tools\\tomcat\\jakarta-tomcat-5.0.25";
		if (s != null && s.length() > 0) {
			RuntimeLocation.runtimeLocation = s;
			
			suite.addTestSuite(Tomcat50ServerTestCase.class);
			
			suite.addTestSuite(GetDelegateTestCase.class);
			suite.addTestSuite(OpenEditorTestCase.class);
			suite.addTestSuite(OpenEditorAgainTestCase.class);
			//suite.addTestSuite(ServerActionsTestCase.class);
		} else {
			System.err.println("Warning: Tomcat 5.0 not found - performance tests skipped");
		}
		
		suite.addTestSuite(CreateWebContentTestCase.class);
		suite.addTestSuite(CreateXMLContentTestCase.class);
		suite.addTestSuite(CreateJavaContentTestCase.class);
		suite.addTestSuite(BuildFullAgainTestCase.class);
		suite.addTestSuite(BuildCleanTestCase.class);
		suite.addTestSuite(BuildIncrementalTestCase.class);
		suite.addTestSuite(AddRemoveModulesWizardTestCase.class);
		suite.addTestSuite(AddRemoveModulesWizard2TestCase.class);
		
		suite.addTestSuite(CreateHugeModuleTestCase.class);
		
		if (s != null && s.length() > 0) {
			suite.addTestSuite(PublishTestCase.class);
			suite.addTestSuite(PublishHugeModuleTestCase.class);
		}
		
		suite.addTestSuite(DeleteModulesTestCase.class);
		
		AbstractTomcatServerTestCase.deleteServer();
		
		suite.addTestSuite(AutobuildTestCase.class);
		//ResourcesPlugin.getWorkspace().getDescription().setAutoBuilding(true);
		
		//$JUnit-END$
		return suite;
	}
}
