/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Custom message dialog that is used when the server is not in the correct launch mode.
 */
public class BreakpointDialog extends MessageDialog {
	protected boolean remember;

	public BreakpointDialog(Shell parentShell) {
		super(parentShell, Messages.wizDebugOnServerTitle, null,
			Messages.dialogBreakpoints, MessageDialog.WARNING,
			new String[] {IDialogConstants.OK_LABEL, IDialogConstants.PROCEED_LABEL,
			IDialogConstants.CANCEL_LABEL}, 0);
	}

	protected Control createCustomArea(Composite parent) {
		final Button rememberB = new Button(parent, SWT.CHECK);
		rememberB.setText(Messages.dialogRemember);
		rememberB.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				remember = rememberB.getSelection();
			}
		});
		return rememberB;
	}

	public boolean isRemember() {
		return remember;
	}
}