/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DeleteRuntimeDialog extends MessageDialog {
	protected boolean promptDeleteServers;
	protected boolean promptRemoveTargets;
	protected boolean deleteServers;
	protected boolean removeTargets;

	public DeleteRuntimeDialog(Shell parentShell, boolean promptDeleteServers, boolean promptRemoveTargets) {
		super(parentShell, Messages.defaultDialogTitle, null, Messages.dialogRuntimeInUse, QUESTION,
			new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
		this.promptDeleteServers = promptDeleteServers;
		this.promptRemoveTargets = promptRemoveTargets;
	}

	protected Control createCustomArea(Composite parent) {
		if (!promptDeleteServers && !promptRemoveTargets)
			return null;
		
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(3);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		
		if (promptDeleteServers) {
			deleteServers = true;
			final Button deleteServersButton = new Button(comp, SWT.CHECK);
			deleteServersButton.setText(Messages.dialogRuntimeDeleteServers);
			deleteServersButton.setSelection(true);
			deleteServersButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					deleteServers = deleteServersButton.getSelection();
				}
			});
		}
		
		if (promptRemoveTargets) {
			removeTargets = true;
			final Button removeTargetsButton = new Button(comp, SWT.CHECK);
			removeTargetsButton.setText(Messages.dialogRuntimeRemoveTargets);
			removeTargetsButton.setSelection(true);
			removeTargetsButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					removeTargets = removeTargetsButton.getSelection();
				}
			});
		}
		
		return comp;
	}

	public boolean isDeleteServers() {
		return deleteServers;
	}

	public boolean isRemoveTargets() {
		return removeTargets;
	}
}