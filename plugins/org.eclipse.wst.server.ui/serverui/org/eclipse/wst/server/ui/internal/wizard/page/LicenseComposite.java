/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
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
import org.eclipse.wst.server.ui.internal.wizard.fragment.LicenseWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

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
/**
 * A composite used to accept a license.
 */
public class LicenseComposite extends Composite {
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
		setLayout(new LicenseLayout());
		
		licenseText = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.WRAP);
		licenseText.setBackground(licenseText.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		
		accept = new Button(this, SWT.RADIO);
		accept.setText(Messages.wizLicenseAccept);
		accept.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				accepted = accept.getSelection();
				taskModel.putObject(LicenseWizardFragment.LICENSE_ACCEPT, new Boolean(accepted));
				wizard.update();
			}
		});
		
		decline = new Button(this, SWT.RADIO);
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
	
	public void setVisibleAcceptReject(boolean visible){
		accept.setVisible(visible);
		decline.setVisible(visible);
	}
}