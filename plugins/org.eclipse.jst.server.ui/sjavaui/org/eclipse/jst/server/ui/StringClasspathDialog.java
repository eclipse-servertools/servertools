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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
 * Dialog to add a string classpath.
 */
public class StringClasspathDialog extends Dialog {
	protected String cp;
	protected Button okay;
	protected boolean isForceAbsolutePath = true;
	protected boolean isEdit;
	
	/**
	 * SystemPropertyDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 */
	public StringClasspathDialog(Shell parentShell) {
		this(parentShell, "");
		isEdit = false;
	}
	
	public StringClasspathDialog(Shell parentShell, boolean curIsForceAbsolutePath) {
		this(parentShell);
		isForceAbsolutePath = curIsForceAbsolutePath;
	}
	
	/**
	 * SystemPropertyDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 */
	public StringClasspathDialog(Shell parentShell, String cp) {
		super(parentShell);
		this.cp = cp;
		isEdit = true;
	}

	/**
	 * Notifies that the cancel button of this dialog has been pressed.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method sets
	 * this dialog's return code to <code>Window.CANCEL</code>
	 * and closes the dialog. Subclasses may override if desired.
	 * </p>
	 */
	protected void cancelPressed() {
		cp = null;
		super.cancelPressed();
	}

	/**
	 *
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (isEdit)
			newShell.setText(JavaServerUIPlugin.getResource("%javaEditStringDialogTitle"));
		else 
		    newShell.setText(JavaServerUIPlugin.getResource("%javaAddStringDialogTitle"));
	}

	/**
	 * Adds buttons to this dialog's button bar.
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method adds 
	 * standard ok and cancel buttons using the <code>createButton</code>
	 * framework method. Subclasses may override.
	 * </p>
	 *
	 * @param parent the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		okay = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		okay.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
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
		WorkbenchHelp.setHelp(composite, ContextIds.JAVA_CLASSPATH_STRING_DIALOG);
	
		new Label(composite, SWT.NONE).setText(JavaServerUIPlugin.getResource("%javaAddStringDialogPath"));
		final Text nameText = new Text(composite, SWT.BORDER);
		GridData data = new GridData();
		data.widthHint = 225;
		nameText.setLayoutData(data);
		nameText.setText(cp);
	
		final Label invalid = new Label(composite, SWT.NONE);
		invalid.setText("");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		invalid.setLayoutData(data);
	
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				cp = nameText.getText();
	
				IPath path = new Path(cp);
				if (cp != null && cp.length() == 0) {
					invalid.setText("");
					okay.setEnabled(false);
				} else if (!path.isAbsolute() && isForceAbsolutePath) {
					invalid.setText(JavaServerUIPlugin.getResource("%javaAddStringDialogInvalidPath"));
					okay.setEnabled(false);
				} else {
					invalid.setText("");
					okay.setEnabled(true);
				}
			}
		});
		WorkbenchHelp.setHelp(nameText, ContextIds.JAVA_CLASSPATH_STRING_DIALOG_PATH);

		Dialog.applyDialogFont(composite);
		return composite;
	}

	/**
	 * Return the classpath.
	 *
	 * @return java.lang.String
	 */
	public String getClasspath() {
		return cp;
	}
}