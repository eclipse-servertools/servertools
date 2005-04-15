/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.resources.IFolder;
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

import org.eclipse.wst.server.core.IServer;
/**
 * Dialog that prompts a user to delete server(s) and/or server configuration(s).
 */
public class DeleteServerDialog extends Dialog {
	protected IServer[] servers;
	protected IFolder[] configs;

	protected Button check;

	/**
	 * DeleteServerDialog constructor comment.
	 * 
	 * @param parentShell a shell
	 * @param servers an array of servers
	 * @param configs an array of server configurations
	 */
	public DeleteServerDialog(Shell parentShell, IServer[] servers, IFolder[] configs) {
		super(parentShell);
		
		if (servers == null || configs == null)
			throw new IllegalArgumentException();
		
		this.servers = servers;
		this.configs = configs;

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
		if (servers.length == 1) {
			label.setText(ServerUIPlugin.getResource("%deleteServerResourceDialogMessage", servers[0].getName()));
		} else
			label.setText(ServerUIPlugin.getResource("%deleteServerResourceDialogMessageMany", servers.length + ""));
		label.setLayoutData(new GridData());
		
		if (configs.length > 0) {
			check = new Button(composite, SWT.CHECK);
		
			if (configs.length == 1) {
				check.setText(ServerUIPlugin.getResource("%deleteServerResourceDialogLooseConfigurations", configs[0].getName()));
			} else
				check.setText(ServerUIPlugin.getResource("%deleteServerResourceDialogLooseConfigurationsMany", configs.length + ""));
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
						int size = servers.length;
						for (int i = 0; i < size; i++) {
							servers[0].delete();
						}
						
						if (checked) {
							size = configs.length;
							for (int i = 0; i < size; i++) {
								configs[i].delete(true, true, monitor);
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