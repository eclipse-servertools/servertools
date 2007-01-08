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
package org.eclipse.wst.server.ui.tests.dialog;

import junit.framework.TestCase;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.ModifyModulesWizard;
import org.eclipse.wst.server.ui.internal.wizard.NewRuntimeWizard;
import org.eclipse.wst.server.ui.internal.wizard.NewServerWizard;
import org.eclipse.wst.server.ui.internal.wizard.RunOnServerWizard;

public class WizardTestCase extends TestCase {
	public static void testRoS(IModule module) {
		Shell shell = UITestHelper.getShell();
		RunOnServerWizard ros = new RunOnServerWizard(module, ILaunchManager.RUN_MODE, null);
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, ros);
		UITestHelper.assertDialog(dialog);
	}

	public void testRunOnServerWizard() throws Exception {
		testRoS(null);
	}

	public void testModifyModulesWizard() throws Exception {
		Shell shell = UITestHelper.getShell();
		ModifyModulesWizard wiz = new ModifyModulesWizard(null);
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wiz);
		UITestHelper.assertDialog(dialog);
	}

	public void testNewRuntimeWizard() throws Exception {
		Shell shell = UITestHelper.getShell();
		NewRuntimeWizard wiz = new NewRuntimeWizard();
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wiz);
		UITestHelper.assertDialog(dialog);
	}

	public void testNewServerWizard() throws Exception {
		Shell shell = UITestHelper.getShell();
		NewServerWizard wiz = new NewServerWizard();
		ClosableWizardDialog dialog = new ClosableWizardDialog(shell, wiz);
		UITestHelper.assertDialog(dialog);
	}
}