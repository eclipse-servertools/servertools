/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DeleteRuntimeDialog extends MessageDialog {
	protected boolean promptDeleteServers = false;
	protected boolean deleteServers = true;

	public DeleteRuntimeDialog(Shell parentShell, boolean promptDeleteServers) {
		super(parentShell, Messages.defaultDialogTitle, null, Messages.dialogRuntimeInUse, QUESTION,
			new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
		this.promptDeleteServers = promptDeleteServers;
	}

	protected Control createCustomArea(Composite parent) {
		if (!promptDeleteServers)
			return null;
		
		Button deleteServersButton = new Button(parent, SWT.CHECK);
		deleteServersButton.setText(Messages.dialogRuntimeDeleteServers);
		deleteServersButton.setSelection(true);
		deleteServersButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deleteServers = true;
			}
		});
		
		return deleteServersButton;
	}

	public boolean isDeleteServers() {
		return deleteServers;
	}
}