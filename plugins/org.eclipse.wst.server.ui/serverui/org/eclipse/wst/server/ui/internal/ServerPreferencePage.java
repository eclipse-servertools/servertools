/**********************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.wst.server.core.internal.ServerPreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
/**
 * The preference page that holds server properties.
 */
public class ServerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	protected ServerPreferences preferences;
	protected ServerUIPreferences uiPreferences;

	protected Button promptIrreversible;

	protected Button showOnActivity;

	protected Button syncOnStartup;

	protected Button autoPublishOnAction;
	protected Button autoPublishLocal;
	protected Spinner autoPublishLocalTime;
	protected Button autoPublishRemote;
	protected Spinner autoPublishRemoteTime;

	protected Combo machineSpeedCombo;

	/**
	 * ServerPreferencesPage constructor comment.
	 */
	public ServerPreferencePage() {
		super();
	
		preferences = ServerPreferences.getInstance();
		uiPreferences = ServerUIPlugin.getPreferences();
	}
	
	/**
	 * Create the preference options.
	 *
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @return org.eclipse.swt.widgets.Control
	 */
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(parent, ContextIds.PREF_GENERAL);
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		composite.setLayoutData(data);
		
		showOnActivity = new Button(composite, SWT.CHECK);
		showOnActivity.setText(Messages.prefShowOnActivity);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		showOnActivity.setLayoutData(data);
		showOnActivity.setSelection(uiPreferences.getShowOnActivity());
		whs.setHelp(showOnActivity, ContextIds.PREF_GENERAL_SHOW_ON_ACTIVITY);
		
		syncOnStartup = new Button(composite, SWT.CHECK);
		syncOnStartup.setText(Messages.prefSyncStartup);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		syncOnStartup.setLayoutData(data);
		syncOnStartup.setSelection(preferences.isSyncOnStartup());
		whs.setHelp(syncOnStartup, ContextIds.PREF_GENERAL_SYNC_STARTUP);
		
		autoPublishLocal = new Button(composite, SWT.CHECK);
		autoPublishLocal.setText(Messages.prefAutoPublishLocal);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		autoPublishLocal.setLayoutData(data);
		autoPublishLocal.setSelection(preferences.getAutoPublishLocal());
		whs.setHelp(autoPublishLocal, ContextIds.PREF_GENERAL_AUTOPUBLISH_LOCAL);
		
		final Label autoPublishLocalTimeLabel = new Label(composite, SWT.NONE);
		autoPublishLocalTimeLabel.setText(Messages.prefAutoPublishLocalTime);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		data.horizontalIndent = 20;
		autoPublishLocalTimeLabel.setLayoutData(data);
		autoPublishLocalTimeLabel.setEnabled(autoPublishLocal.getSelection());
		
		autoPublishLocalTime = new Spinner(composite, SWT.BORDER);
		autoPublishLocalTime.setMinimum(0);
		autoPublishLocalTime.setMaximum(120);
		autoPublishLocalTime.setSelection(preferences.getAutoPublishLocalTime());
		autoPublishLocalTime.setEnabled(autoPublishLocal.getSelection());
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.widthHint = 60;
		autoPublishLocalTime.setLayoutData(data);
		whs.setHelp(autoPublishLocalTime, ContextIds.PREF_GENERAL_AUTOPUBLISH_LOCAL);
		
		autoPublishLocal.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				autoPublishLocalTimeLabel.setEnabled(autoPublishLocal.getSelection());
				autoPublishLocalTime.setEnabled(autoPublishLocal.getSelection());
			}
		});
		
		autoPublishRemote = new Button(composite, SWT.CHECK);
		autoPublishRemote.setText(Messages.prefAutoPublishRemote);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		autoPublishRemote.setLayoutData(data);
		autoPublishRemote.setSelection(preferences.getAutoPublishRemote());
		whs.setHelp(autoPublishRemote, ContextIds.PREF_GENERAL_AUTOPUBLISH_REMOTE);
		
		final Label autoPublishRemoteTimeLabel = new Label(composite, SWT.NONE);
		autoPublishRemoteTimeLabel.setText(Messages.prefAutoPublishRemoteTime);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		data.horizontalIndent = 20;
		autoPublishRemoteTimeLabel.setLayoutData(data);
		autoPublishRemoteTimeLabel.setEnabled(autoPublishRemote.getSelection());
		
		autoPublishRemoteTime = new Spinner(composite, SWT.BORDER);
		autoPublishRemoteTime.setMinimum(0);
		autoPublishRemoteTime.setMaximum(120);
		autoPublishRemoteTime.setSelection(preferences.getAutoPublishRemoteTime());
		autoPublishRemoteTime.setEnabled(autoPublishRemote.getSelection());
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.widthHint = 60;
		autoPublishRemoteTime.setLayoutData(data);
		whs.setHelp(autoPublishRemoteTime, ContextIds.PREF_GENERAL_AUTOPUBLISH_REMOTE);
		
		autoPublishRemote.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				autoPublishRemoteTimeLabel.setEnabled(autoPublishRemote.getSelection());
				autoPublishRemoteTime.setEnabled(autoPublishRemote.getSelection());
			}
		});
		
		promptIrreversible = new Button(composite, SWT.CHECK);
		promptIrreversible.setText(Messages.prefPromptIrreversible);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		promptIrreversible.setLayoutData(data);
		promptIrreversible.setSelection(uiPreferences.getPromptBeforeIrreversibleChange());
		whs.setHelp(promptIrreversible, ContextIds.PREF_GENERAL_PROMPT_IRREVERSIBLE);
		
		Label label = new Label(composite, SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 3;
		label.setLayoutData(data);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.prefMachineSpeed);
		
		machineSpeedCombo = new Combo(composite, SWT.READ_ONLY);
		String[] items = new String[] {
			Messages.prefMachineSpeedVerySlow,
			Messages.prefMachineSpeedSlow,
			Messages.prefMachineSpeedAverage,
			Messages.prefMachineSpeedFast,
			Messages.prefMachineSpeedVeryFast
		};
		machineSpeedCombo.setItems(items);
		machineSpeedCombo.select((preferences.getMachineSpeed() - 1) / 2);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		machineSpeedCombo.setLayoutData(data);
		whs.setHelp(machineSpeedCombo, ContextIds.PREF_GENERAL_TIMEOUT_DELAY);
		
		Dialog.applyDialogFont(composite);
		
		return composite;
	}

	/**
	 * Initializes this preference page using the passed workbench.
	 *
	 * @param workbench the current workbench
	 */
	public void init(IWorkbench workbench) {
		// do nothing
	}

	/**
	 * Performs special processing when this page's Defaults button has been pressed.
	 */
	protected void performDefaults() {
		promptIrreversible.setSelection(uiPreferences.getDefaultPromptBeforeIrreversibleChange());
		showOnActivity.setSelection(uiPreferences.getDefaultShowOnActivity());
		
		syncOnStartup.setSelection(preferences.getDefaultSyncOnStartup());
		autoPublishLocal.setSelection(preferences.getDefaultAutoPublishLocal());
		autoPublishLocalTime.setSelection(preferences.getDefaultAutoPublishLocalTime());
		autoPublishRemote.setSelection(preferences.getDefaultAutoPublishRemote());
		autoPublishRemoteTime.setSelection(preferences.getDefaultAutoPublishRemoteTime());
		
		machineSpeedCombo.select((preferences.getDefaultMachineSpeed() - 1) / 2);
		
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		preferences.setSyncOnStartup(syncOnStartup.getSelection());
		uiPreferences.setPromptBeforeIrreversibleChange(promptIrreversible.getSelection());
		uiPreferences.setShowOnActivity(showOnActivity.getSelection());
		
		preferences.setAutoPublishLocal(autoPublishLocal.getSelection());
		preferences.setAutoPublishLocalTime(autoPublishLocalTime.getSelection());
		preferences.setAutoPublishRemote(autoPublishRemote.getSelection());
		preferences.setAutoPublishRemoteTime(autoPublishRemoteTime.getSelection());
		
		preferences.setMachineSpeed(machineSpeedCombo.getSelectionIndex() * 2 + 1);
		
		return true;
	}
}