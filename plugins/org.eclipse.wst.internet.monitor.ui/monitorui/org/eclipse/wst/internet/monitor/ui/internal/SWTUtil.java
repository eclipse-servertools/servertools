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
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
/**
 * SWT Utility class.
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
		int widthHint = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}
	
	public static Button createButton(Composite comp, String label) {
		Button b = new Button(comp, SWT.PUSH);
		b.setText(label);
		if (fontMetrics == null)
			initializeDialogUnits(comp);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.widthHint = getButtonWidthHint(b);
		b.setLayoutData(data);
		return b;
	}
	
	public static Button createCheckbox(Composite comp, String txt, boolean isSelected){
		Button button = new Button(comp, SWT.CHECK);
		button.setText(txt);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalIndent = 10;
		button.setLayoutData(data);
		button.setSelection(isSelected);
		return button;
	}
	
	public static Label createLabel(Composite comp, String txt) {
		Label label = new Label(comp, SWT.NONE);
		label.setText(txt);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
		return label;
	}
}