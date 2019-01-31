/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.DeleteServerDialogExtension;

public class DeleteServerDialogTestExtension extends
		DeleteServerDialogExtension {

	public static final String BUTTON_TEXT = "Just testing";

	public DeleteServerDialogTestExtension() {

	}

	@Override
	public void createControl(Composite parent) {
		Button testButton = new Button(parent, SWT.CHECK);
		testButton.setText(BUTTON_TEXT);
		testButton.setSelection(true);
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void performPreDeleteAction(IProgressMonitor monitor) {
		// do nothing
	}

	@Override
	public void performPostDeleteAction(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

}