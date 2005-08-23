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

import junit.framework.TestCase;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.ui.ServerUIUtil;

public class ServerUIUtilTestCase extends TestCase {
	private static final String MAXIMUM_UI_WAIT_EXCEEDED = "maximum UI wait exceeded in testShowNewRuntimeWizard";

	public void testShowNewRuntimeWizard() {
		final int MAX_SPINS = 10000;
		int count = 0;
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final Display display = shell.getDisplay();
		shell.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				ServerUIUtil.showNewRuntimeWizard(shell, null, null);
			}
		});
		while (!display.readAndDispatch() && count++ < MAX_SPINS)
		{
			// wait until the UI thread settles (ie. the wizard opens)
			// then close the active window, which should be the wizard
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().close();
		}
		if (count >= MAX_SPINS) {
			throw new RuntimeException(MAXIMUM_UI_WAIT_EXCEEDED);
		}
	}
}