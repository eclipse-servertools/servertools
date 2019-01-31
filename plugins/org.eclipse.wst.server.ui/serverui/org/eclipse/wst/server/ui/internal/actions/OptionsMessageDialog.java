/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.actions;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Custom message dialog that displays a set of options.
 * Return value is the option
 */
public class OptionsMessageDialog extends MessageDialog {
	protected int radio;
	protected boolean remember;
	protected String[] options;

	public OptionsMessageDialog(Shell parentShell, String title, String dialogMessage, String[] options) {
		super(parentShell, title, null, dialogMessage, MessageDialog.QUESTION,
			new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0);
		this.options = options;
		if (options == null)
			throw new IllegalArgumentException("Must have at least one option");
	}

	protected Control createDialogArea(Composite parent) {
      // create message area
      createMessageArea(parent);
      // create the top level composite for the dialog area
      /*Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      composite.setLayout(layout);
      GridData data = new GridData(GridData.FILL_BOTH);
      data.horizontalSpan = 2;
      composite.setLayoutData(data);*/
      createCustomArea(parent);
      return parent;
  }

	protected Control createCustomArea(Composite parent) {
		int size = options.length;
		for (int i = 0; i < size; i++) {
			new Label(parent, SWT.NONE);
			
			Button radioB = new Button(parent, SWT.RADIO);
			radioB.setText(options[i]);
			final int x = i;
			radioB.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					radio = x;
				}
			});
			if (i == 0) {
				radioB.setSelection(true);
				radio = 0;
			}
		}
		
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		
		final Button rememberB = new Button(parent, SWT.CHECK);
		rememberB.setText(Messages.dialogRemember);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		rememberB.setLayoutData(data);
		rememberB.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				remember = rememberB.getSelection();
			}
		});
		return rememberB;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == 0)
			setReturnCode(radio);
		else
			setReturnCode(9);
		close();
	}

	public boolean isRemember() {
		return remember;
	}
}