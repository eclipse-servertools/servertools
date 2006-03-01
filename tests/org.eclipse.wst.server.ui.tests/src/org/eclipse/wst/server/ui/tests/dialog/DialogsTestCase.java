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
package org.eclipse.wst.server.ui.tests.dialog;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.DeleteServerDialog;
import org.eclipse.wst.server.ui.internal.TerminationDialog;
import junit.framework.TestCase;

public class DialogsTestCase extends TestCase {
	private Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public void testDeleteServerDialog() {
		DeleteServerDialog dsd = new DeleteServerDialog(getShell(), new IServer[0], new IFolder[0]);
		UITestHelper.assertDialog(dsd);
	}

	public void _testTerminationDialog() {
		TerminationDialog td = new TerminationDialog(getShell(), "MyServer with a really long name");
		UITestHelper.assertDialog(td);
	}
}