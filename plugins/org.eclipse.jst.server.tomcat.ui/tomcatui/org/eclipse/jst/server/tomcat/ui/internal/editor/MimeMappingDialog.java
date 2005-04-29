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
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jst.server.tomcat.core.internal.MimeMapping;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.Messages;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
/**
 * Dialog to add or modify mime mappings.
 */
public class MimeMappingDialog extends Dialog {
	protected MimeMapping map;
	protected boolean isEdit;

	/**
	 * MimeMappingDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 */
	public MimeMappingDialog(Shell parentShell) {
		this(parentShell, new MimeMapping("", ""));
		isEdit = false;
	}

	/**
	 * MimeMappingDialog constructor.
	 * 
	 * @param parentShell a shell
	 * @param map a mime mapping
	 */
	public MimeMappingDialog(Shell parentShell, MimeMapping map) {
		super(parentShell);
		this.map = map;
		isEdit = true;
	}

	/**
	 *
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setImage(TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_MIME_MAPPING));
		if (isEdit)
			newShell.setText(Messages.configurationEditorMimeMapppingDialogTitleEdit);
		else
			newShell.setText(Messages.configurationEditorMimeMapppingDialogTitleAdd);
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
	 * @param parent the parent composite to contain the dialog area
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
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(composite, ContextIds.CONFIGURATION_EDITOR_MAPPING_DIALOG);
	
		new Label(composite, SWT.NONE).setText(Messages.configurationEditorMimeMapppingDialogMimeType);
		final Text type = new Text(composite, SWT.BORDER);
		GridData data = new GridData();
		data.widthHint = 150;
		type.setLayoutData(data);
		type.setText(map.getMimeType());
		type.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				map = new MimeMapping(map.getExtension(), type.getText());
				validate();
			}
		});
		whs.setHelp(type, ContextIds.CONFIGURATION_EDITOR_MAPPING_DIALOG_TYPE);
	
		new Label(composite, SWT.NONE).setText(Messages.configurationEditorMimeMapppingDialogMimeExtension);
		final Text extension = new Text(composite, SWT.BORDER);
		data = new GridData();
		data.widthHint = 150;
		extension.setLayoutData(data);
		extension.setText(map.getExtension());
		extension.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				map = new MimeMapping(extension.getText(), map.getMimeType());
				validate();
			}
		});
		whs.setHelp(extension, ContextIds.CONFIGURATION_EDITOR_MAPPING_DIALOG_EXTENSION);
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}
	
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		validate();

		return control;
	}

	protected void validate() {
		boolean ok = true;
		if (map.getExtension() == null || map.getExtension().length() < 1)
			ok = false;
		if (map.getMimeType() == null || map.getMimeType().length() < 1)
			ok = false;
		getButton(IDialogConstants.OK_ID).setEnabled(ok);
	}

	/**
	 * Return the mime mapping.
	 *
	 * @return org.eclipse.jst.server.tomcat.MimeMapping
	 */
	public MimeMapping getMimeMapping() {
		return map;
	}
}