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
package org.eclipse.wst.server.ui.tests;

import org.eclipse.wst.server.ui.tests.dialog.*;
import org.eclipse.wst.server.ui.tests.editor.*;
import org.eclipse.wst.server.ui.tests.wizard.IWizardHandleTestCase;
import org.eclipse.wst.server.ui.tests.wizard.TaskWizardTestCase;
import org.eclipse.wst.server.ui.tests.wizard.WizardFragmentTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test suite for org.eclipse.wst.server.ui.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ExistenceTest.class);
		suite.addTestSuite(ServerUIPreferencesTestCase.class);
		
		suite.addTestSuite(ServerUICoreTestCase.class);
		suite.addTestSuite(ServerUIUtilTestCase.class);
		suite.addTest(new OrderedTestSuite(ServerLaunchConfigurationTabTestCase.class));
		
		suite.addTest(new OrderedTestSuite(IServerEditorPartInputTestCase.class));
		suite.addTest(new OrderedTestSuite(ServerEditorSectionTestCase.class));
		suite.addTest(new OrderedTestSuite(ServerEditorPartTestCase.class));
		
		suite.addTestSuite(DialogsTestCase.class);
		suite.addTestSuite(PreferencesTestCase.class);
		suite.addTestSuite(ViewTestCase.class);
		// seems to hang on build machine, 
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=107697
		//suite.addTestSuite(WizardTestCase.class);
		
		suite.addTest(new OrderedTestSuite(IOrderedTestCase.class));
		suite.addTest(new OrderedTestSuite(ServerEditorActionFactoryDelegateTestCase.class));
		
		suite.addTestSuite(IWizardHandleTestCase.class);
		suite.addTest(new OrderedTestSuite(TaskWizardTestCase.class));
		suite.addTest(new OrderedTestSuite(WizardFragmentTestCase.class));
		//$JUnit-END$
		return suite;
	}
}