/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.tests;

import org.eclipse.jst.server.tomcat.ui.internal.editor.MimeMappingDialog;
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

	/*public void testWebModuleDialog() {
		WebModuleDialog wmd = new WebModuleDialog(getShell(), null, null, null, true);
		UITestHelper.assertDialog(wmd);
	}*/
}