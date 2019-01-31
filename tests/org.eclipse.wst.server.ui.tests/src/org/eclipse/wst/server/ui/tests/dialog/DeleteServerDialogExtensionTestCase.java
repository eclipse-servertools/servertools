/*******************************************************************************
 * Copyright (c) 2014, 2019 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.dialog;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.DeleteServerDialog;

public class DeleteServerDialogExtensionTestCase extends TestCase {

	private List<Button> buttons = new ArrayList<Button>();

	private Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public void testDeleteServerDialogExtension() {
		DeleteServerDialog dsd = new DeleteServerDialog(getShell(),
				new IServer[0], new IFolder[0]);
		dsd.setBlockOnOpen(false);
		dsd.open();
		Shell shell = dsd.getShell();
		Control[] children = shell.getChildren();
		findButtons(children);
		boolean extensionFound = false;
		for (Button button : buttons) {
			if (button.getText().equals(
					DeleteServerDialogTestExtension.BUTTON_TEXT)) {
				extensionFound = true;
				break;
			}
		}
		assertTrue("Extension point did not work.", extensionFound);
		dsd.close();
	}

	private void findButtons(Control[] children) {
		for (Control control : children) {
			if (control instanceof Composite) {
				findButtons(((Composite) control).getChildren());
			} else if (control instanceof Button) {
				buttons.add((Button) control);

			}
		}
	}

}
