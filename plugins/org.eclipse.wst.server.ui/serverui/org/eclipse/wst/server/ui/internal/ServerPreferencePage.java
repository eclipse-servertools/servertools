/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerPreferences;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.IServerUIPreferences;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.WorkbenchHelp;
/**
 * The preference page that holds server properties.
 */
public class ServerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	protected Button publishDetailsButton;
	protected Button publishBeforeStart;
	protected Button autoRestart;

	protected Button promptIrreversible;
	
	protected byte saveEditors;
	
	protected Button saveNever;
	protected Button savePrompt;
	protected Button saveAuto;
	
	protected Button createInWorkspace;

	protected IServerPreferences preferences;
	protected IServerUIPreferences uiPreferences;

	/**
	 * ServerPreferencesPage constructor comment.
	 */
	public ServerPreferencePage() {
		super();
	
		preferences = ServerCore.getServerPreferences();
		uiPreferences = ServerUICore.getPreferences();
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
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		composite.setLayoutData(data);
		WorkbenchHelp.setHelp(composite, ContextIds.PREF_GENERAL);
		
		publishBeforeStart = new Button(composite, SWT.CHECK);
		publishBeforeStart.setText(ServerUIPlugin.getResource("%prefPublishBeforeStarting"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		publishBeforeStart.setLayoutData(data);
		publishBeforeStart.setSelection(preferences.isAutoPublishing());
		WorkbenchHelp.setHelp(publishBeforeStart, ContextIds.PREF_GENERAL_PUBLISH_BEFORE_START);
		
		publishDetailsButton = new Button(composite, SWT.CHECK);
		publishDetailsButton.setText(ServerUIPlugin.getResource("%prefShowPublishingDetails"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		publishDetailsButton.setLayoutData(data);
		publishDetailsButton.setSelection(uiPreferences.getShowPublishingDetails());
		WorkbenchHelp.setHelp(publishDetailsButton, ContextIds.PREF_GENERAL_SHOW_PUBLISHING_DETAILS);
	
		autoRestart = new Button(composite, SWT.CHECK);
		autoRestart.setText(ServerUIPlugin.getResource("%prefAutoRestart"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		autoRestart.setLayoutData(data);
		autoRestart.setSelection(preferences.isAutoRestarting());
		WorkbenchHelp.setHelp(autoRestart, ContextIds.PREF_GENERAL_AUTO_RESTART);
		
		promptIrreversible = new Button(composite, SWT.CHECK);
		promptIrreversible.setText(ServerUIPlugin.getResource("%prefPromptIrreversible"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		promptIrreversible.setLayoutData(data);
		promptIrreversible.setSelection(uiPreferences.getPromptBeforeIrreversibleChange());
		WorkbenchHelp.setHelp(promptIrreversible, ContextIds.PREF_GENERAL_PROMPT_IRREVERSIBLE);
		
		createInWorkspace = new Button(composite, SWT.CHECK);
		createInWorkspace.setText(ServerUIPlugin.getResource("%prefCreateInWorkspace"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		createInWorkspace.setLayoutData(data);
		createInWorkspace.setSelection(preferences.isCreateResourcesInWorkspace());
		WorkbenchHelp.setHelp(createInWorkspace, ContextIds.PREF_GENERAL_CREATE_IN_WORKSPACE);
		
		// save editors group
		Group saveEditorGroup = new Group(composite, SWT.NONE);
		saveEditorGroup.setText(ServerUIPlugin.getResource("%prefSaveEditorsGroup"));
		
		layout = new GridLayout();
		layout.numColumns = 3;
		saveEditorGroup.setLayout(layout);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		saveEditorGroup.setLayoutData(data);
		
		saveNever = new Button(saveEditorGroup, SWT.RADIO);
		saveNever.setText(ServerUIPlugin.getResource("%prefSaveEditorsNever"));
		saveNever.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveEditors = IServerUIPreferences.SAVE_EDITORS_NEVER;
			}
		});
		WorkbenchHelp.setHelp(saveNever, ContextIds.PREF_GENERAL_SAVE_EDITORS);
		
		savePrompt = new Button(saveEditorGroup, SWT.RADIO);
		savePrompt.setText(ServerUIPlugin.getResource("%prefSaveEditorsPrompt"));
		savePrompt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveEditors = IServerUIPreferences.SAVE_EDITORS_PROMPT;
			}
		});
		WorkbenchHelp.setHelp(savePrompt, ContextIds.PREF_GENERAL_SAVE_EDITORS);
		
		saveAuto = new Button(saveEditorGroup, SWT.RADIO);
		saveAuto.setText(ServerUIPlugin.getResource("%prefSaveEditorsAutosave"));
		saveAuto.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				saveEditors = IServerUIPreferences.SAVE_EDITORS_AUTO;
			}
		});
		WorkbenchHelp.setHelp(saveAuto, ContextIds.PREF_GENERAL_SAVE_EDITORS);
		
		setSaveEditorStatus(uiPreferences.getSaveEditors());
		
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}
	
	protected void setSaveEditorStatus(byte status) {
		saveEditors = status;
		saveNever.setSelection(saveEditors == IServerUIPreferences.SAVE_EDITORS_NEVER);
		savePrompt.setSelection(saveEditors == IServerUIPreferences.SAVE_EDITORS_PROMPT);
		saveAuto.setSelection(saveEditors == IServerUIPreferences.SAVE_EDITORS_AUTO); 
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
		publishDetailsButton.setSelection(uiPreferences.getDefaultShowPublishingDetails());
		autoRestart.setSelection(preferences.isDefaultAutoRestarting());
		publishBeforeStart.setSelection(preferences.isDefaultAutoPublishing());
		promptIrreversible.setSelection(uiPreferences.getDefaultPromptBeforeIrreversibleChange());
		createInWorkspace.setSelection(preferences.isDefaultCreateResourcesInWorkspace());
		
		setSaveEditorStatus(uiPreferences.getDefaultSaveEditors());
	
		super.performDefaults();
	}

	/**
	 * Method declared on IPreferencePage.
	 * Subclasses should override
	 */
	public boolean performOk() {
		uiPreferences.setShowPublishingDetails(publishDetailsButton.getSelection());
		preferences.setAutoPublishing(publishBeforeStart.getSelection());
		preferences.setAutoRestarting(autoRestart.getSelection());
		uiPreferences.setSaveEditors(saveEditors);
		uiPreferences.setPromptBeforeIrreversibleChange(promptIrreversible.getSelection());
		preferences.setCreateResourcesInWorkspace(createInWorkspace.getSelection());
	
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
	
		IServer[] servers = ServerCore.getResourceManager().getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				IServer server = servers[i];
				if (server.getServerRestartState()) {
					String mode = server.getMode();
					if (server.canRestart(mode))
						try {
							Trace.trace(Trace.FINEST, "Attempting to auto restart " + server.getName());
							server.restart(mode);
						} catch (Exception e) {
							Trace.trace(Trace.SEVERE, "Error restarting: " + server, e);
						}
				}
			}
		}
	}
}