/*******************************************************************************
 * Copyright (c) 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.dialog;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.ui.internal.wizard.ModifyModulesWizard;
import org.eclipse.wst.server.ui.internal.wizard.NewRuntimeWizard;
import org.eclipse.wst.server.ui.internal.wizard.NewServerWizard;
import org.eclipse.wst.server.ui.internal.wizard.RunOnServerWizard;

public class WizardTestCase extends TestCase {

	// This test suite ensures the test methods are run in order
	public static TestSuite getOrderedTests() {
		TestSuite mySuite = new TestSuite();
	
		mySuite.addTest(TestSuite.createTest(WizardTestCase.class, "testRunOnServerWizard"));
		mySuite.addTest(TestSuite.createTest(WizardTestCase.class, "testModifyModulesWizard"));
		mySuite.addTest(TestSuite.createTest(WizardTestCase.class, "testNewRuntimeWizard"));
		mySuite.addTest(TestSuite.createTest(WizardTestCase.class, "testNewServerWizard"));
		return mySuite;
	}	
	
	public static void testRoS(IModule module) {
		Shell shell = UITestHelper.getShell();
		RunOnServerWizard ros = new RunOnServerWizard(module, ILaunchManager.RUN_MODE, null);
		WizardDialog dialog = new WizardDialog(shell, ros);
		UITestHelper.assertDialog(dialog);
	}

	public void testRunOnServerWizard() throws Exception {
		testRoS(null);
	}

	public void testModifyModulesWizard() throws Exception {
		Shell shell = UITestHelper.getShell();
		ModifyModulesWizard wiz = new ModifyModulesWizard(null);
		WizardDialog dialog = new WizardDialog(shell, wiz);
		UITestHelper.assertDialog(dialog);
	}

	public void testNewRuntimeWizard() throws Exception {
		Shell shell = UITestHelper.getShell();
		NewRuntimeWizard wiz = new NewRuntimeWizard();
		WizardDialog dialog = new WizardDialog(shell, wiz);
		UITestHelper.assertDialog(dialog);
	}

	public void testNewServerWizard() throws Exception {
		Shell shell = UITestHelper.getShell();
		NewServerWizard wiz = new NewServerWizard();
		WizardDialog dialog = new WizardDialog(shell, wiz);
		UITestHelper.assertDialog(dialog);
	}
}