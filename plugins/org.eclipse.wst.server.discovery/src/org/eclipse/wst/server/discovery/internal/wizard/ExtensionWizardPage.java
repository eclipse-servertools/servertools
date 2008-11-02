/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal.wizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.internal.p2.ui.dialogs.AcceptLicensesWizardPage;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.server.discovery.internal.Messages;
import org.eclipse.wst.server.discovery.internal.model.IExtension;

public class ExtensionWizardPage extends WizardPage {
	private ExtensionComposite comp;
	//private LicenseWizardPage licensePage;
	private AcceptLicensesWizardPage licensePage;
	private IExtension extension;

	//protected ExtensionWizardPage(LicenseWizardPage licenseWizardPage) {
	protected ExtensionWizardPage(AcceptLicensesWizardPage licenseWizardPage) {
		super("extension");
		this.licensePage = licenseWizardPage;
		setTitle(Messages.wizNewInstallableServerTitle);
		setDescription(Messages.wizNewInstallableServerDescription);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		composite.setLayout(layout);
		//WorkbenchHelp.setHelp(this, ContextIds.SELECT_CLIENT_WIZARD);
		
		Label label = new Label(composite, SWT.WRAP);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		data.widthHint = 225;
		label.setLayoutData(data);
		label.setText(Messages.wizNewInstallableServerMessage);
		
		comp = new ExtensionComposite(composite, SWT.NONE, new ExtensionComposite.ExtensionSelectionListener() {
			public void extensionSelected(IExtension sel) {
				handleSelection(sel);
			}
		});
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		comp.setLayoutData(data);
		
		Dialog.applyDialogFont(composite);
		setControl(composite);
	}

	protected void handleSelection(IExtension sel) {
		extension = sel;
		if (extension == null)
			licensePage.update(new IInstallableUnit[0], null);
		else {
			IProgressMonitor monitor = new NullProgressMonitor(); 
			licensePage.update(extension.getIUs(), extension.getProvisioningPlan(monitor));
		}
		/*if (extension == null)
			licensePage.setLicense(null);
		else
			licensePage.setLicense(extension.getLicense());*/
		setPageComplete(extension != null);
	}

	public IExtension getExtension() {
		return extension;
	}
}