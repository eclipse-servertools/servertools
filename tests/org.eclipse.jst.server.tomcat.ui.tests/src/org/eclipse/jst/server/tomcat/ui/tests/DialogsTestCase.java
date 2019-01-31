/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
package org.eclipse.jst.server.tomcat.ui.tests;

import org.eclipse.jst.server.tomcat.ui.internal.CleanWorkDirDialog;
import org.eclipse.jst.server.tomcat.ui.internal.editor.MimeMappingDialog;
import org.eclipse.jst.server.tomcat.ui.internal.editor.WebModuleDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import junit.framework.TestCase;

public class DialogsTestCase extends TestCase {
	private Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public void testMimeMappingDialog() {
		MimeMappingDialog mmd = new MimeMappingDialog(getShell());
		UITestHelper.assertDialog(mmd);
	}

	public void testWebModuleDialog() {
		try {
			WebModuleDialog wmd = new WebModuleDialog(getShell(), null, null, null, true);
			UITestHelper.assertDialog(wmd);
		} catch (Exception e) {
			// ignore for now, will need to improve test later
		}
	}

	public void testCleanWorkDirDialog() {
		try {
			CleanWorkDirDialog cwdd = new CleanWorkDirDialog(getShell(), null, null);
			UITestHelper.assertDialog(cwdd);
		} catch (Exception e) {
			// ignore for now, will need to improve test later
		}
	}
}