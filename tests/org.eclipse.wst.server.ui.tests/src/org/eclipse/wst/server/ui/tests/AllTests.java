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
		suite.addTest(ServerUIPreferencesTestCase.getOrderedTests());
		
		suite.addTestSuite(ServerUICoreTestCase.class);
		suite.addTestSuite(ServerLaunchConfigurationTabTestCase.class);
		
		suite.addTestSuite(IServerEditorPartInputTestCase.class);
		suite.addTestSuite(ServerEditorSectionTestCase.class);
		suite.addTestSuite(ServerEditorPartTestCase.class);
		
		suite.addTestSuite(DialogsTestCase.class);
		suite.addTestSuite(PreferencesTestCase.class);
		suite.addTestSuite(ViewTestCase.class);
		suite.addTest(WizardTestCase.getOrderedTests());
		
		suite.addTestSuite(ServerEditorActionFactoryDelegateTestCase.class);
		
		suite.addTest(IWizardHandleTestCase.getOrderedTests());

		suite.addTestSuite(TaskWizardTestCase.class);
		suite.addTestSuite(WizardFragmentTestCase.class);
			
		suite.addTestSuite(ServerTooltipTestCase.class);
		suite.addTestSuite(AbstractServerLabelProviderTestCase.class);
		//$JUnit-END$
		return suite;
	}
}