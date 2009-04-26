/***************************************************************************************************
 * Copyright (c) 2005, 2009 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * SWT Utilities. Used for creating the UI elements for generic server UIs.
 * 
 * @author Gorkem Ercan
 */
public class SWTUtil {
	private static FontMetrics fontMetrics;

	protected static void initializeDialogUnits(Control testControl) {
		// Compute and store a font metric
		GC gc = new GC(testControl);
		gc.setFont(JFaceResources.getDialogFont());
		fontMetrics = gc.getFontMetrics();
		gc.dispose();
	}

	/**
	 * Returns a width hint for a button control.
	 */
	protected static int getButtonWidthHint(Button button) {
		int widthHint = Dialog.convertHorizontalDLUsToPixels(fontMetrics,
				IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT,
				true).x);
	}

	/**
	 * Create a new button with the standard size.
	 * 
	 * @param comp
	 *            the component to add the button to
	 * @param label
	 *            the button label
	 * @return a button
	 */
	public static Button createButton(Composite comp, String label) {
		Button b = new Button(comp, SWT.PUSH);
		b.setText(label);
		if (fontMetrics == null)
			initializeDialogUnits(comp);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		data.widthHint = getButtonWidthHint(b);
		b.setLayoutData(data);
		return b;
	}

	/**
	 * Convert DLUs to pixels.
	 * 
	 * @param comp
	 *            a component
	 * @param x
	 *            pixels
	 * @return dlus
	 */
	public static int convertHorizontalDLUsToPixels(Composite comp, int x) {
		if (fontMetrics == null)
			initializeDialogUnits(comp);
		return Dialog.convertHorizontalDLUsToPixels(fontMetrics, x);
	}

	/**
	 * Convert DLUs to pixels.
	 * 
	 * @param comp
	 *            a component
	 * @param y
	 *            pixels
	 * @return dlus
	 */
	public static int convertVerticalDLUsToPixels(Composite comp, int y) {
		if (fontMetrics == null)
			initializeDialogUnits(comp);
		return Dialog.convertVerticalDLUsToPixels(fontMetrics, y);
	}

	/**
	 * Creates a label, text and a button to open a directory dialog.
	 * 
	 * @param title
	 * @param value
	 * @param parent
	 * @return Text that holds value
	 * 
	 */
	public static Text createLabeledPath(String title, String value, final Composite parent) {
		return SWTUtil.createLabeledPath(title, value, parent, null);
	}

	/**
	 * Creates a label, text and a button to open a directory dialog. This
	 * method creates Forum UI compatible widgets.
	 * 
	 * @param title
	 * @param value
	 * @param parent
	 * @param toolkit
	 * @return Text that holds value
	 */
	public static Text createLabeledPath(String title, String value, final Composite parent, FormToolkit toolkit) {
		Label label = null;
		Text cText = null;
		Button button = null;

		if (toolkit == null) {
            label = new Label(parent, SWT.NONE);
            label.setText(title);
			cText = new Text(parent, SWT.SHADOW_IN | SWT.BORDER);
			button = SWTUtil.createButton(parent,
					GenericServerUIMessages.serverTypeGroup_label_browse);
		} else {
			createFormLabel(title, parent, toolkit);
			cText = toolkit.createText(parent, value);
			button = toolkit.createButton(parent,
					GenericServerUIMessages.serverTypeGroup_label_browse,
					SWT.PUSH);
		}

		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalSpan = 1;

		final Text text = cText;
		text.setLayoutData(gridData);
		text.setText(value);
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(parent.getShell());
				dlg.setFilterPath(text.getText().replace('\\', '/'));
				String res = dlg.open();
				if (res != null) {
					text.setText(res.replace('\\', '/'));
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		return text;
	}

	/**
	 * Creates a label, text and a button thats opens a file dialog
	 * 
	 * @param title
	 * @param value
	 * @param parent
	 * @return Text that holds the value
	 */
	public static Text createLabeledFile(String title, String value, final Composite parent) {
		return SWTUtil.createLabeledFile(title, value, parent, null);
	}

	/**
	 * Creates a label, text and a button thats opens a file dialog. This method
	 * is used for creating Form UI compatible widgets
	 * 
	 * @param title
	 * @param value
	 * @param parent
	 * @param toolkit
	 * @return
	 */
	public static Text createLabeledFile(String title, String value, final Composite parent, FormToolkit toolkit) {

		Label label;
		Text cText;
		Button button;
		if (toolkit == null) {
			label = new Label(parent, SWT.NONE);
			label.setText(title);
			cText = new Text(parent, SWT.SHADOW_IN | SWT.BORDER);
			button = SWTUtil.createButton(parent,
					GenericServerUIMessages.serverTypeGroup_label_browse);
		} else {
			createFormLabel(title, parent, toolkit);
			cText = toolkit.createText(parent, value);
			button = toolkit.createButton(parent,
					GenericServerUIMessages.serverTypeGroup_label_browse,
					SWT.PUSH);
		}
		final Text text = cText;
		GridData gridData = new GridData(SWT.FILL,SWT.BEGINNING,true,false);
		gridData.horizontalSpan = 1;
		text.setLayoutData(gridData);
		text.setText(value);
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(parent.getShell());
				dlg.setFileName(text.getText().replace('\\', '/'));
				String res = dlg.open();
				if (res != null) {
					text.setText(res.replace('\\', '/'));
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		return text;
	}

	/**
	 * Creates a label and a text
	 * 
	 * @param title
	 * @param value
	 * @param parent
	 * @return Text object that holds the value
	 */
	public static Text createLabeledText(String title, String value, Composite parent) {
		return SWTUtil.createLabeledText( title, value, parent, null );
	}

	/**
	 * Creates a label and a text. This method created form ui compatible
	 * widgets
	 * 
	 * @param title
	 * @param value
	 * @param parent
	 * @param toolkit
	 * @return Text control that holds the value
	 */
	public static Text createLabeledText(String title, String value, Composite parent, FormToolkit toolkit) {

		Label label;
		Text text;
		if (toolkit == null) {
			label = new Label(parent, SWT.NONE);
			label.setText(title);
			text = new Text(parent, SWT.SHADOW_IN | SWT.BORDER);
		} else {
			createFormLabel(title, parent, toolkit);
			text = toolkit.createText(parent, value);
		}

		GridData gridData = new GridData(SWT.FILL,SWT.BEGINNING,true,false);
		gridData.horizontalSpan = 2;
		text.setLayoutData(gridData);
		text.setText(value);

		return text;
	}

	/**
	 * Creates a CHECK style button and label
	 * 
	 * @param title
	 * @param value
	 * @param parent
	 * @return Check Button
	 */
	public static Button createLabeledCheck(String title, boolean value, Composite parent) {
		return SWTUtil.createLabeledCheck(title, value, parent, null);
	}

	/**
	 * Creates a CHECK style button and label. This method is form ui compatible
	 * 
	 * @param title
	 * @param value
	 * @param parent
	 * @param toolkit
	 * @return
	 */
	public static Button createLabeledCheck(String title, boolean value, Composite parent, FormToolkit toolkit) {
		Label label;
		Button button;
		if (toolkit == null) {
			label = new Label(parent, SWT.NONE);
			label.setText(title);
			button = new Button(parent, SWT.CHECK);
		} else {
			createFormLabel(title, parent, toolkit);
			button = toolkit.createButton(parent, null, SWT.CHECK);
		}

		GridData gridData = new GridData(SWT.FILL,SWT.BEGINNING,true,false);
		gridData.horizontalSpan = 2;
		button.setLayoutData(gridData);
		button.setSelection(value);
		return button;
	}

	/**
	 * Creates a label and an editable Combo.
	 * 
	 * @param title
	 * @param values
	 * @param parent
	 * @return Combo
	 */
	public static Combo createLabeledCombo(String title, String[] values,Composite parent) {
		return SWTUtil.createLabeledCombo(title, values, parent, null);
	}

	/**
	 * Creates a label and an editable Combo
	 * 
	 * @param title
	 * @param values
	 * @param parent
	 * @param toolkit
	 * @return Combo
	 */
	public static Combo createLabeledCombo(String title, String[] values, Composite parent, FormToolkit toolkit) {

		Label label;

		if (toolkit == null) {
			label = new Label(parent, SWT.NONE);
			label.setText(title);
		} else {
			createFormLabel(title, parent, toolkit);
		}
		Combo combo = new Combo(parent, SWT.READ_ONLY);
		if (toolkit != null) {
			toolkit.adapt(combo, true, true);
		}

		GridData gridData = new GridData(SWT.FILL,SWT.BEGINNING,true,false);
		gridData.horizontalSpan = 2;
		combo.setLayoutData(gridData);

		for (int i = 0; i < values.length; i++) {
			combo.add(values[i]);
		}
		if (combo.getItemCount() > 0)
			combo.select(0);
		return combo;
	}
	
	/**
	 * Creates a label and an editable Combo.
	 * 
	 * @param title
	 * @param values
	 * @param parent
	 * @return Combo
	 */
	public static Combo createLabeledEditableCombo(String title, String[] values,String value,Composite parent) {
		return SWTUtil.createLabeledEditableCombo(title, values,value, parent, null);
	}

	/**
	 * Creates a label and an editable Combo
	 * 
	 * @param title
	 * @param values
	 * @param parent
	 * @param toolkit
	 * @return Combo
	 */
	public static Combo createLabeledEditableCombo(String title, String[] values,String value, Composite parent, FormToolkit toolkit) {

		Label label;

		if (toolkit == null) {
			label = new Label(parent, SWT.NONE);
			label.setText(title);
		} else {
			createFormLabel(title, parent, toolkit);
		}
		Combo combo = new Combo(parent, SWT.SHADOW_IN | SWT.BORDER);
		if (toolkit != null) {
			toolkit.adapt(combo, true, true);
		}

		GridData gridData = new GridData(SWT.FILL,SWT.BEGINNING,true,false);
		gridData.horizontalSpan = 2;
		combo.setLayoutData(gridData);

		for (int i = 0; i < values.length; i++) {
			combo.add(values[i]);
		}
		if (combo.getItemCount() > 0)
			combo.select(0);
		combo.setText(value);
		return combo;
	}

	private static Label createFormLabel(String title, Composite parent, FormToolkit toolkit) {
		Label label;
		label = toolkit.createLabel(parent, title);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}
}