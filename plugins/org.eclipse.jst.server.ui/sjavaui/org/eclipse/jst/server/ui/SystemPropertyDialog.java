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
package org.eclipse.jst.server.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jst.server.core.internal.SystemProperty;
import org.eclipse.jst.server.internal.ui.ContextIds;
import org.eclipse.jst.server.internal.ui.JavaServerUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
/**
 * Dialog to add or modify system properties.
 */
public class SystemPropertyDialog extends Dialog {
	protected SystemProperty sp;
	protected boolean isEdit;
	
	protected Text nameText;
	protected Text valueText;
	
	private Button okButton;
	
	/**
	 * SystemPropertyDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 */
	public SystemPropertyDialog(Shell parentShell) {
		this(parentShell, new SystemProperty("", ""));
		isEdit = false;
	}

	/**
	 * SystemPropertyDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 */
	public SystemPropertyDialog(Shell parentShell, SystemProperty sp) {
		super(parentShell);
		this.sp = sp;
		isEdit = true;
	}

	/**
	 *
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (isEdit)
			newShell.setText(JavaServerUIPlugin.getResource("%javaSystemPropertyEditDialog"));
		else
			newShell.setText(JavaServerUIPlugin.getResource("%javaSystemPropertyAddDialog"));
	}

	/**
	 * Creates and returns the contents of the upper part 
	 * of this dialog (above the button bar).
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method
	 * creates and returns a new <code>Composite</code> with
	 * standard margins and spacing. Subclasses should override.
	 * </p>
	 *
	 * @param the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		WorkbenchHelp.setHelp(composite, ContextIds.JAVA_SYSTEM_PROPERTY_DIALOG);
	
		new Label(composite, SWT.NONE).setText(JavaServerUIPlugin.getResource("%javaSystemPropertyName"));
		nameText = new Text(composite, SWT.BORDER);
		GridData data = new GridData();
		data.widthHint = 200;
		nameText.setLayoutData(data);
		nameText.setText(sp.getName());
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				sp = new SystemProperty(nameText.getText(), sp.getValue());
				validateFields();
			}
		});
		WorkbenchHelp.setHelp(nameText, ContextIds.JAVA_SYSTEM_PROPERTY_DIALOG_NAME);
	
		new Label(composite, SWT.NONE).setText(JavaServerUIPlugin.getResource("%javaSystemPropertyValue"));
		valueText = new Text(composite, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		valueText.setLayoutData(data);
		valueText.setText(sp.getValue());
		valueText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				sp = new SystemProperty(sp.getName(), valueText.getText());
				validateFields();
			}
		});
		WorkbenchHelp.setHelp(valueText, ContextIds.JAVA_SYSTEM_PROPERTY_DIALOG_VALUE);
	
		Dialog.applyDialogFont(composite);
		return composite;
	}

	/**
	 * Return the system property.
	 *
	 * @return org.eclipse.jst.server.ui.SystemProperty
	 */
	public SystemProperty getSystemProperty() {
		return sp;
	}

	/**
	 * Initialize the Ok button
	 *
	 * @param the parent composite to contain the dialog area
	 * @return the dialog area control
	 */	
	protected Control createButtonBar(Composite parent) {
		Control buttonControl = super.createButtonBar(parent);
		// Initialize the ok button state after the ok button is created.
		validateFields();

		return buttonControl;
	}

	/**
	 * Enable Ok button
	 *
	 * @param boolean value used to enable Ok button
	 */
	private void setOkButtonEnabled(boolean curIsEnabled) {
		if (okButton == null) {
			okButton = getButton(IDialogConstants.OK_ID);
		}
		if (okButton != null) {
			okButton.setEnabled(curIsEnabled);
		}
	}

	/**
	 * Enables the Ok button for the required fields that require a value
	 *
	 */
	protected void validateFields() {
		boolean result = true;

		if (nameText != null) {
			String curName = nameText.getText();
			result &= (curName != null && curName.length() > 0);
		}

		setOkButtonEnabled(result);
	}
}