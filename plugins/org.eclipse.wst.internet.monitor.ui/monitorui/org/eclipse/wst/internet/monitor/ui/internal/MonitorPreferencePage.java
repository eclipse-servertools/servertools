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
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.WorkbenchHelp;
/**
 * The preference page that holds monitor properties.
 */
public class MonitorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	protected Button displayButton;

	/**
	 * MonitorPreferencePage constructor comment.
	 */
	public MonitorPreferencePage() {
		super();
		noDefaultAndApplyButton();
	}

	/**
	 * Create the preference options.
	 *
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @return org.eclipse.swt.widgets.Control
	 */
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		composite.setLayoutData(data);
		WorkbenchHelp.setHelp(composite, ContextIds.PREF);
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText(MonitorUIPlugin.getResource("%preferenceDescription"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		label.setLayoutData(data);
	
		displayButton = new Button(composite, SWT.CHECK);
		displayButton.setText(MonitorUIPlugin.getResource("%prefShowView"));
		displayButton.setSelection(MonitorUIPlugin.getShowOnActivityPreference());
		WorkbenchHelp.setHelp(displayButton, ContextIds.PREF_SHOW);
		
		label = new Label(composite, SWT.NONE);
		label.setText("");
	
		MonitorComposite monitorComp = new MonitorComposite(composite, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		monitorComp.setLayoutData(data);
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}

	/**
	 * Initializes this preference page using the passed desktop.
	 *
	 * @param desktop the current desktop
	 */
	public void init(IWorkbench workbench) {
		// do nothing
	}

	/**
	 * Performs special processing when this page's Defaults button has been pressed.
	 * <p>
	 * This is a framework hook method for sublcasses to do special things when
	 * the Defaults button has been pressed.
	 * Subclasses may override, but should call <code>super.performDefaults</code>.
	 * </p>
	 */
	protected void performDefaults() {
		displayButton.setSelection(MonitorUIPlugin.getDefaultShowOnActivityPreference());
		super.performDefaults();
	}

	/** 
	 * Method declared on IPreferencePage.
	 * Subclasses should override
	 */
	public boolean performOk() {
		MonitorUIPlugin.setShowOnActivityPreference(displayButton.getSelection());
		MonitorUIPlugin.getInstance().savePluginPreferences();
		return true;
	}
}