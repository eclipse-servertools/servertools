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
package org.eclipse.wst.server.ui.internal.wizard.page;

import org.eclipse.jface.dialogs.Dialog;

import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.wizard.fragment.LicenseWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
/**
 * A composite used to accept a license.
 */
public class LicenseComposite extends Composite {
	protected TaskModel taskModel;
	protected IWizardHandle wizard;
	protected Text licenseText;
	protected Button accept;
	protected Button decline;
	protected boolean accepted;

	/**
	 * Create a new LicenseComposite.
	 * 
	 * @param parent a parent composite
	 * @param taskModel a task model
	 * @param wizard the wizard this composite is contained in
	 */
	public LicenseComposite(Composite parent, TaskModel taskModel, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.taskModel = taskModel;
		this.wizard = wizard;
		
		createControl();
	}

	/**
	 * Creates the UI of the page.
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		setLayout(layout);
		//WorkbenchHelp.setHelp(this, ContextIds.SELECT_CLIENT_WIZARD);
		
		licenseText = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.WRAP);
		GridData data = new GridData(GridData.FILL_BOTH);
		//data.widthHint = 200;
		licenseText.setLayoutData(data);
		licenseText.setBackground(licenseText.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		
		accept = new Button(this, SWT.RADIO);
		accept.setText(Messages.wizLicenseAccept);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalIndent = 15;
		data.verticalIndent = 5;
		accept.setLayoutData(data);
		accept.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				accepted = accept.getSelection();
				taskModel.putObject(LicenseWizardFragment.LICENSE_ACCEPT, new Boolean(accepted));
				wizard.update();
			}
		});
		
		decline = new Button(this, SWT.RADIO);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalIndent = 15;
		data.verticalIndent = 2;
		decline.setLayoutData(data);
		decline.setText(Messages.wizLicenseDecline);
		
		updateLicense();
		Dialog.applyDialogFont(this);
	}

	public void updateLicense() {
		String license = (String) taskModel.getObject(LicenseWizardFragment.LICENSE);
		if (license == null)
			license = LicenseWizardFragment.LICENSE_UNKNOWN;
		
		Object acc = taskModel.getObject(LicenseWizardFragment.LICENSE_ACCEPT);
		if (acc == null) {
			accepted = false;
			accept.setSelection(false);
			decline.setSelection(false);
		}
		
		if (LicenseWizardFragment.LICENSE_NONE.equals(license)) {
			licenseText.setText(Messages.wizLicenseNone);
			accept.setEnabled(false);
			decline.setEnabled(false);
			accepted = true;
		} else if (LicenseWizardFragment.LICENSE_UNKNOWN.equals(license)) {
			licenseText.setText(Messages.wizLicenseNone);
			accept.setEnabled(true);
			decline.setEnabled(true);
		} else {
			licenseText.setText(license);
			accept.setEnabled(true);
			decline.setEnabled(true);
		}
		taskModel.putObject(LicenseWizardFragment.LICENSE_ACCEPT, new Boolean(accepted));
	}
}