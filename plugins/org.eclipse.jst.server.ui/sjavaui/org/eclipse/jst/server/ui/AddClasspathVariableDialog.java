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
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jst.server.internal.ui.ContextIds;
import org.eclipse.jst.server.internal.ui.JavaServerUIPlugin;
import org.eclipse.jst.server.internal.ui.Trace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * Dialog to add a Java classpath variable entry.
 */
public class AddClasspathVariableDialog extends Dialog {
	protected IClasspathEntry entry;

	protected Combo variableCombo;
	protected Text extension;
	protected CLabel resolvedPath;

	/**
	 * AddClasspathVariableDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 */
	public AddClasspathVariableDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * AddClasspathVariableDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 */
	public AddClasspathVariableDialog(Shell parentShell, IClasspathEntry entry) {
		super(parentShell);
		this.entry = entry;
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
		entry = null;
		super.cancelPressed();
	}

	protected IPath chooseExternalJarFile(Shell shell, String initialPath) {
		FileDialog dialog = new FileDialog(shell, SWT.SINGLE);
		dialog.setText(JavaServerUIPlugin.getResource("%javaAddExternalJarDialog"));
		dialog.setFilterExtensions(new String[] {"*.jar;*.zip"});
		dialog.setFilterPath(initialPath);
		String res = dialog.open();
	
		if (res != null)
			return new Path(res).makeAbsolute();
	
		return null;
	}

	/**
	 *
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(JavaServerUIPlugin.getResource("%javaAddVariableDialogTitle"));
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
		layout.numColumns = 3;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		WorkbenchHelp.setHelp(composite, ContextIds.JAVA_CLASSPATH_VARIABLE_DIALOG);
	
		new Label(composite, SWT.NONE).setText(JavaServerUIPlugin.getResource("%javaAddVariableDialogName"));
	
		variableCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		String[] variables = JavaCore.getClasspathVariableNames();
		variableCombo.setItems(variables);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.widthHint = 200;
		variableCombo.setLayoutData(data);
		variableCombo.select(0);
		WorkbenchHelp.setHelp(composite, ContextIds.JAVA_CLASSPATH_VARIABLE_DIALOG_VARIABLE);
	
		new Label(composite, SWT.NONE).setText("");
	
		new Label(composite, SWT.NONE).setText(JavaServerUIPlugin.getResource("%javaAddVariableDialogExtension"));
		extension = new Text(composite, SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		extension.setLayoutData(data);
		WorkbenchHelp.setHelp(composite, ContextIds.JAVA_CLASSPATH_VARIABLE_DIALOG_EXTENSION);
	
		Button browse = new Button(composite, SWT.NONE);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		browse.setLayoutData(data);
		browse.setText(JavaServerUIPlugin.getResource("%javaBrowse"));
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String initialPath = null;
				String text = variableCombo.getText();
				IPath varPath = new Path(text);
				IPath resolved = JavaCore.getResolvedVariablePath(varPath);
				if (resolved != null)
					initialPath = resolved.toOSString();
	
				IPath path = chooseExternalJarFile(getShell(), initialPath);
				if (path != null) {
					if (resolved != null && resolved.isPrefixOf(path)) {
						path = path.removeFirstSegments(resolved.segmentCount());
						path = path.setDevice(null);
					}
					extension.setText(path.toString());
				}
			}
		});
		WorkbenchHelp.setHelp(composite, ContextIds.JAVA_CLASSPATH_VARIABLE_DIALOG_EXTENSION_BROWSE);
	
		new Label(composite, SWT.NONE).setText(JavaServerUIPlugin.getResource("%javaAddVariableDialogResolved"));
	
		resolvedPath = new CLabel(composite, SWT.NONE);
		resolvedPath.setText("");
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		resolvedPath.setLayoutData(data);
	
		if (entry != null) {
			String var = entry.getPath().segment(0);
			int size = variables.length;
			for (int i = 0; i < size; i++) {
				if (variables[i].equals(var))
					variableCombo.select(i);
			}
			
			String rest = entry.getPath().removeFirstSegments(1).toString();
			extension.setText(rest);
		}
		
		variableCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleSelection();
			}
		});
		extension.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleSelection();
			}
		});

		handleSelection();
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}

	/**
	 * Return the classpath entry.
	 *
	 * @return 
	 */
	public IClasspathEntry getClasspathEntry() {
		return entry;
	}

	/**
	 * 
	 */
	protected void handleSelection() {
		try {
			int index = variableCombo.getSelectionIndex();
			String var = variableCombo.getItem(index);
	
			String path = extension.getText();
			if (path != null && !path.startsWith("/"))
				path = "/" + path;
	
			entry = JavaCore.newVariableEntry(new Path(var + path), null, null);
	
			IPath resolved = JavaCore.getResolvedVariablePath(new Path(var + path));
			if (resolved == null)
				resolvedPath.setText("");
			else
				resolvedPath.setText(resolved.toString());
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "JavaUI selection error", e);
		}
	}
}