/*******************************************************************************
 * Copyright (c) 2005, 2013 IBM Corporation and others.
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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.ui.internal.wizard.ModifyModulesWizard;
import org.eclipse.wst.server.ui.internal.wizard.NewRuntimeWizard;
import org.eclipse.wst.server.ui.internal.wizard.NewServerWizard;
import org.eclipse.wst.server.ui.internal.wizard.RunOnServerWizard;

public class WizardTestCase extends TestCase {
	public static void testRoS(IModule module) {
		Shell shell = UITestHelper.getShell();
		RunOnServerWizard ros = new RunOnServerWizard(module, ILaunchManager.RUN_MODE, null);
		WizardDialog dialog = new WizardDialog(shell, ros);
		UITestHelper.assertDialog(dialog);
	}

	public void testAll() throws Exception {
		testRoS(null);

		Shell shell = UITestHelper.getShell();
		ModifyModulesWizard wiz = new ModifyModulesWizard(null);
		WizardDialog dialog = new WizardDialog(shell, wiz);
		UITestHelper.assertDialog(dialog);

		shell = UITestHelper.getShell();
		NewRuntimeWizard wiz2 = new NewRuntimeWizard();
		dialog = new WizardDialog(shell, wiz2);
		UITestHelper.assertDialog(dialog);

		shell = UITestHelper.getShell();
		NewServerWizard wiz3 = new NewServerWizard();
		dialog = new WizardDialog(shell, wiz3);
		UITestHelper.assertDialog(dialog);
	}
}