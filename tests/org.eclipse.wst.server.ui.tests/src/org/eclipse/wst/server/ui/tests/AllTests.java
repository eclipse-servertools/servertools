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

import org.eclipse.wst.server.ui.tests.dialog.DialogsTestCase;
import org.eclipse.wst.server.ui.tests.dialog.PreferencesTestCase;
import org.eclipse.wst.server.ui.tests.dialog.ViewTestCase;
import org.eclipse.wst.server.ui.tests.editor.*;
import org.eclipse.wst.server.ui.tests.wizard.IWizardHandleTestCase;
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
		
		suite.addTestSuite(ICommandManagerTestCase.class);
		suite.addTestSuite(IServerEditorPartInputTestCase.class);
		suite.addTestSuite(IServerEditorSectionTestCase.class);
		
		suite.addTestSuite(DialogsTestCase.class);
		suite.addTestSuite(PreferencesTestCase.class);
		suite.addTestSuite(ViewTestCase.class);
		
		suite.addTestSuite(IOrderedTestCase.class);
		suite.addTestSuite(ServerEditorActionFactoryDelegateTestCase.class);
		suite.addTestSuite(ServerEditorPageSectionFactoryDelegateTestCase.class);
		suite.addTestSuite(ServerEditorPartFactoryDelegateTestCase.class);
		
		suite.addTestSuite(IWizardHandleTestCase.class);
		//$JUnit-END$
		return suite;
	}
}