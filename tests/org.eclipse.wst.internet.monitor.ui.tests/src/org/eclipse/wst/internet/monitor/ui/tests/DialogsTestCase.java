/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui.tests;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorDialog;
import junit.framework.TestCase;

public class DialogsTestCase extends TestCase {
	private Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public void testMonitorDialog() {
		MonitorDialog td = new MonitorDialog(getShell());
		UITestHelper.assertDialog(td);
	}
}