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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.discovery.internal.Messages;

public class LicenseWizardPage extends WizardPage {
	class LicenseLayout extends Layout {
		private static final int INDENT = 15;
		private static final int SPACING = 5;
		
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			if (wHint < 200)
				wHint = 200;
			if (hHint < 300)
				hHint = 300;
			return new Point(wHint, hHint);
		}

		protected void layout(Composite composite, boolean flushCache) {
			Control[] children = composite.getChildren();
			Point p1 = children[1].computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
			Point p2 = children[2].computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
			
			Rectangle r = composite.getClientArea();
			
			children[2].setBounds(r.x + INDENT, r.y + r.height - p2.y, r.width - INDENT, p2.y);
			children[1].setBounds(r.x + INDENT, r.y + r.height - p2.y - p1.y - SPACING, r.width - INDENT, p1.y);
			children[0].setBounds(r.x, r.y, r.width, r.height - p1.y - p2.y - SPACING * 3);
		}
	}

	protected Text licenseText;
	protected Button accept;
	protected Button decline;
	//protected boolean accepted;

	protected LicenseWizardPage() {
		super("license");
		setTitle(Messages.wizLicenseTitle);
		setDescription(Messages.wizLicenseDescription);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new LicenseLayout());
		
		licenseText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.WRAP);
		licenseText.setBackground(licenseText.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		
		accept = new Button(composite, SWT.RADIO);
		accept.setText(Messages.wizLicenseAccept);
		accept.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//accepted = accept.getSelection();
				//getWizard().getContainer().updateButtons();
				setPageComplete(accept.getSelection());
			}
		});
		
		decline = new Button(composite, SWT.RADIO);
		decline.setText(Messages.wizLicenseDecline);
		
		Dialog.applyDialogFont(composite);
		setControl(composite);
	}

	/*public boolean isPageComplete() {
		return isLicenseAccepted();
	}

	public boolean isLicenseAccepted() {
		return accepted;
	}*/

	public void setLicense(String license) {
		if (license == null)
			licenseText.setText(Messages.wizLicenseNone);
		else
			licenseText.setText(license);
		//accepted = false;
		accept.setSelection(false);
		decline.setSelection(false);
		setPageComplete(false);
		//getWizard().getContainer().updateButtons();
	}

	/*public void updateLicense() {
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
	}*/
}