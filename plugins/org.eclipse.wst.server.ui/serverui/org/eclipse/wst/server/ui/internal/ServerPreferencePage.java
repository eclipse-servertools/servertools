/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
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
import org.eclipse.swt.widgets.Group;
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
	protected Button publishBeforeStart;
	protected Button autoRestart;

	protected Button promptIrreversible;
	
	protected Button showOnActivity;
	
	protected byte saveEditors;
	
	protected Button saveNever;
	protected Button savePrompt;
	protected Button saveAuto;

	protected ServerPreferences preferences;
	protected ServerUIPreferences uiPreferences;
	
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
		
		publishBeforeStart = new Button(composite, SWT.CHECK);
		publishBeforeStart.setText(Messages.prefAutoPublish);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		publishBeforeStart.setLayoutData(data);
		publishBeforeStart.setSelection(preferences.isAutoPublishing());
		whs.setHelp(publishBeforeStart, ContextIds.PREF_GENERAL_PUBLISH_BEFORE_START);
		
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
		//WorkbenchHelp.setHelp(autoPublishRemote, ContextIds.);
		
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
		
		autoPublishRemote.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				autoPublishRemoteTimeLabel.setEnabled(autoPublishRemote.getSelection());
				autoPublishRemoteTime.setEnabled(autoPublishRemote.getSelection());
			}
		});
		
		autoRestart = new Button(composite, SWT.CHECK);
		autoRestart.setText(Messages.prefAutoRestart);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		autoRestart.setLayoutData(data);
		autoRestart.setSelection(preferences.isAutoRestarting());
		whs.setHelp(autoRestart, ContextIds.PREF_GENERAL_AUTO_RESTART);
		
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
		
		// save editors group
		Group saveEditorGroup = new Group(composite, SWT.NONE);
		saveEditorGroup.setText(Messages.prefSaveEditorsGroup);
		
		layout = new GridLayout();
		layout.numColumns = 3;
		saveEditorGroup.setLayout(layout);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		saveEditorGroup.setLayoutData(data);
		
		saveNever = new Button(saveEditorGroup, SWT.RADIO);
		saveNever.setText(Messages.prefSaveEditorsNever);
		saveNever.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveEditors = ServerUIPreferences.SAVE_EDITORS_NEVER;
			}
		});
		whs.setHelp(saveNever, ContextIds.PREF_GENERAL_SAVE_EDITORS);
		
		savePrompt = new Button(saveEditorGroup, SWT.RADIO);
		savePrompt.setText(Messages.prefSaveEditorsPrompt);
		savePrompt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveEditors = ServerUIPreferences.SAVE_EDITORS_PROMPT;
			}
		});
		whs.setHelp(savePrompt, ContextIds.PREF_GENERAL_SAVE_EDITORS);
		
		saveAuto = new Button(saveEditorGroup, SWT.RADIO);
		saveAuto.setText(Messages.prefSaveEditorsAutosave);
		saveAuto.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveEditors = ServerUIPreferences.SAVE_EDITORS_AUTO;
			}
		});
		whs.setHelp(saveAuto, ContextIds.PREF_GENERAL_SAVE_EDITORS);
		
		label = new Label(composite, SWT.NONE);
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
		
		setSaveEditorStatus(uiPreferences.getSaveEditors());
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}
	
	protected void setSaveEditorStatus(byte status) {
		saveEditors = status;
		saveNever.setSelection(saveEditors == ServerUIPreferences.SAVE_EDITORS_NEVER);
		savePrompt.setSelection(saveEditors == ServerUIPreferences.SAVE_EDITORS_PROMPT);
		saveAuto.setSelection(saveEditors == ServerUIPreferences.SAVE_EDITORS_AUTO); 
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
		autoRestart.setSelection(preferences.isDefaultAutoRestarting());
		publishBeforeStart.setSelection(preferences.isDefaultAutoPublishing());
		promptIrreversible.setSelection(uiPreferences.getDefaultPromptBeforeIrreversibleChange());
		showOnActivity.setSelection(uiPreferences.getDefaultShowOnActivity());
		
		autoPublishLocal.setSelection(preferences.getDefaultAutoPublishLocal());
		autoPublishLocalTime.setSelection(preferences.getDefaultAutoPublishLocalTime());
		autoPublishRemote.setSelection(preferences.getDefaultAutoPublishRemote());
		autoPublishRemoteTime.setSelection(preferences.getDefaultAutoPublishRemoteTime());
		
		machineSpeedCombo.select((preferences.getDefaultMachineSpeed() - 1) / 2);
		
		setSaveEditorStatus(uiPreferences.getDefaultSaveEditors());
	
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		preferences.setAutoPublishing(publishBeforeStart.getSelection());
		preferences.setAutoRestarting(autoRestart.getSelection());
		uiPreferences.setSaveEditors(saveEditors);
		uiPreferences.setPromptBeforeIrreversibleChange(promptIrreversible.getSelection());
		uiPreferences.setShowOnActivity(showOnActivity.getSelection());
		
		preferences.setAutoPublishLocal(autoPublishLocal.getSelection());
		preferences.setAutoPublishLocalTime(autoPublishLocalTime.getSelection());
		preferences.setAutoPublishRemote(autoPublishRemote.getSelection());
		preferences.setAutoPublishRemoteTime(autoPublishRemoteTime.getSelection());
		
		preferences.setMachineSpeed(machineSpeedCombo.getSelectionIndex() * 2 + 1);
	
		// auto restart any servers that are ready for restart
		if (autoRestart.getSelection())
			autoRestartAll();
	
		return true;
	}

	/**
	 * Automatically restart any servers that require it.
	 */
	protected static void autoRestartAll() {
		Trace.trace(Trace.FINEST, "Auto restarting all dirty servers");
	
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				IServer server = servers[i];
				if (server.getServerRestartState()) {
					String mode = server.getMode();
					if (server.canRestart(mode).isOK())
						try {
							Trace.trace(Trace.FINEST, "Attempting to auto restart " + server.getName());
							server.restart(mode, (IProgressMonitor)null);
						} catch (Exception e) {
							Trace.trace(Trace.SEVERE, "Error restarting: " + server, e);
						}
				}
			}
		}
	}
}