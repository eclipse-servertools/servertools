/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import org.eclipse.wst.server.core.IElement;
/**
 * Dialog that prompts a user to delete server(s) and/or server configuration(s).
 */
public class DeleteServerDialog extends Dialog {
	protected List deleteList;
	protected List configList;

	protected Button check;

	/**
	 * DeleteServerDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 * @
	 */
	public DeleteServerDialog(Shell parentShell, List deleteList, List configList) {
		super(parentShell);
		
		if (deleteList == null)
			deleteList = new ArrayList(0);
		if (configList == null)
			configList = new ArrayList(0);
		
		this.deleteList = deleteList;
		this.configList = configList;

		setBlockOnOpen(true);
	}

	/**
	 *
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ServerUIPlugin.getResource("%deleteServerResourceDialogTitle"));
	}

	/**
	 * 
	 */
	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		//WorkbenchHelp.setHelp(composite, ContextIds.TERMINATE_SERVER_DIALOG);
	
		Label label = new Label(composite, SWT.NONE);
		if (deleteList.size() == 1) {
			IElement element = (IElement) deleteList.get(0);
			label.setText(ServerUIPlugin.getResource("%deleteServerResourceDialogMessage", element.getName()));
		} else
			label.setText(ServerUIPlugin.getResource("%deleteServerResourceDialogMessageMany", deleteList.size() + ""));
		label.setLayoutData(new GridData());
		
		if (!configList.isEmpty()) {
			check = new Button(composite, SWT.CHECK);
		
			if (configList.size() == 1) {
				IElement element = (IElement) configList.get(0);
				check.setText(ServerUIPlugin.getResource("%deleteServerResourceDialogLooseConfigurations", element.getName()));
			} else
				check.setText(ServerUIPlugin.getResource("%deleteServerResourceDialogLooseConfigurationsMany", configList.size() + ""));
			check.setSelection(true);
			check.setLayoutData(new GridData());
		}
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}

	protected void okPressed() {
		final boolean checked = (check != null && check.getSelection());
		
		try {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) throws CoreException {
					try {
						Iterator iterator = deleteList.iterator();
						while (iterator.hasNext()) {
							IElement element = (IElement) iterator.next();
							element.delete();
						}
						
						if (checked) {
							iterator = configList.iterator();
							while (iterator.hasNext()) {
								IElement element = (IElement) iterator.next();
								element.delete();
							}
						}
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Error while deleting resources", e);
					}
				}
			};
			new ProgressMonitorDialog(getShell()).run(true, true, op);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error deleting resources", e);
		}
		
		super.okPressed();
	}
}