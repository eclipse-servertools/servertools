package org.eclipse.wst.server.ui.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import java.util.Iterator;
import java.util.Map;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.wst.server.core.IPublishManager;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
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

	protected Combo publisherCombo;
	protected Label publisherDescription;
	protected String[] publisherIds;

	protected Button promptIrreversible;
	
	protected byte saveEditors;
	protected byte repairServers;
	
	protected Button saveNever;
	protected Button savePrompt;
	protected Button saveAuto;
	
	protected Button repairNever;
	protected Button repairPrompt;
	protected Button repairAlways;
	
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
		
		// repair group
		Group repairGroup = new Group(composite, SWT.NONE);
		repairGroup.setText(ServerUIPlugin.getResource("%prefRepairModuleGroup"));

		layout = new GridLayout();
		layout.numColumns = 3;
		repairGroup.setLayout(layout);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		repairGroup.setLayoutData(data);

		repairNever = new Button(repairGroup, SWT.RADIO);
		repairNever.setText(ServerUIPlugin.getResource("%prefRepairModuleNever"));
		repairNever.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				repairServers = IServerPreferences.REPAIR_NEVER;
			}
		});
		WorkbenchHelp.setHelp(repairNever, ContextIds.PREF_GENERAL_REPAIR);

		repairPrompt = new Button(repairGroup, SWT.RADIO);
		repairPrompt.setText(ServerUIPlugin.getResource("%prefRepairModulePrompt"));
		repairPrompt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				repairServers = IServerPreferences.REPAIR_PROMPT;
			}
		});
		WorkbenchHelp.setHelp(repairPrompt, ContextIds.PREF_GENERAL_REPAIR);

		repairAlways = new Button(repairGroup, SWT.RADIO);
		repairAlways.setText(ServerUIPlugin.getResource("%prefRepairModuleAutomatic"));
		repairAlways.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				repairServers = IServerPreferences.REPAIR_ALWAYS;
			}
		});
		WorkbenchHelp.setHelp(repairAlways, ContextIds.PREF_GENERAL_REPAIR);
		
		setRepairStatus(preferences.getModuleRepairStatus());
	
		// publish combo
		new Label(composite, SWT.NONE).setText(ServerUIPlugin.getResource("%prefPublisher"));
		
		publisherCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		publisherCombo.setLayoutData(data);
	
		Map map = ServerCore.getPublishManagers();
		String[] s = new String[map.size()];
		publisherIds = new String[map.size()];
		int i = 0;
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			IPublishManager publisher = (IPublishManager) map.get(id);
			publisherIds[i] = id;
			s[i++] = publisher.getName();
		}
		publisherCombo.setItems(s);
		publisherCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handlePublisherSelection();
			}
		});
		WorkbenchHelp.setHelp(publisherCombo, ContextIds.PREF_GENERAL_PUBLISHER);
	
		new Label(composite, SWT.NONE).setText("");
	
		publisherDescription = new Label(composite, SWT.WRAP);
		publisherDescription.setText("");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.widthHint = 200;
		data.heightHint = 100;
		data.horizontalSpan = 2;
		publisherDescription.setLayoutData(data);
	
		selectPublisher(preferences.getPublishManager());
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}
	
	/**
	 * Return the publisher id at the given index.
	 * 
	 * @return java.lang.String
	 * @param index int
	 */
	protected String getPublisherId(int index) {
		if (index < 0 || index >= publisherIds.length)
			return null;
		return publisherIds[index];
	}
	
	/**
	 * Handle a publisher selection
	 */
	protected void handlePublisherSelection() {
		try {
			int sel = publisherCombo.getSelectionIndex();
			String id = getPublisherId(sel);
			IPublishManager publisher = (IPublishManager) ServerCore.getPublishManagers().get(id);
			publisherDescription.setText(publisher.getDescription());
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error showing publisher description", e);
			publisherDescription.setText("");
		}
	}
	
	protected void setSaveEditorStatus(byte status) {
		saveEditors = status;
		saveNever.setSelection(saveEditors == IServerUIPreferences.SAVE_EDITORS_NEVER);
		savePrompt.setSelection(saveEditors == IServerUIPreferences.SAVE_EDITORS_PROMPT);
		saveAuto.setSelection(saveEditors == IServerUIPreferences.SAVE_EDITORS_AUTO); 
	}
	
	protected void setRepairStatus(byte status) {
		repairServers = status;
		repairNever.setSelection(repairServers == IServerPreferences.REPAIR_NEVER);
		repairPrompt.setSelection(repairServers == IServerPreferences.REPAIR_PROMPT);
		repairAlways.setSelection(repairServers == IServerPreferences.REPAIR_ALWAYS);
	}
	
	/**
	 * Initializes this preference page using the passed desktop.
	 *
	 * @param desktop the current desktop
	 */
	public void init(IWorkbench workbench) { }
	
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
		selectPublisher(preferences.getDefaultPublishManager());
		promptIrreversible.setSelection(uiPreferences.getDefaultPromptBeforeIrreversibleChange());
		createInWorkspace.setSelection(preferences.isDefaultCreateResourcesInWorkspace());
		
		setRepairStatus(preferences.getDefaultModuleRepairStatus());
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
		preferences.setModuleRepairStatus(repairServers);
		uiPreferences.setSaveEditors(saveEditors);
		uiPreferences.setPromptBeforeIrreversibleChange(promptIrreversible.getSelection());
		preferences.setCreateResourcesInWorkspace(createInWorkspace.getSelection());
	
		int sel = publisherCombo.getSelectionIndex();
		preferences.setPublishManager(getPublisherId(sel));
	
		// auto restart any servers that are ready for restart
		if (autoRestart.getSelection())
			autoRestartAll();
	
		return true;
	}

	/**
	 * Select a publisher.
	 * 
	 * @param id java.lang.String
	 */
	protected void selectPublisher(String id) {
		if (id == null || id.length() == 0)
			return;

		for (int i = 0; i < publisherIds.length; i++) {
			if (id.equals(publisherIds[i])) {
				publisherCombo.select(i);
				IPublishManager publisher = (IPublishManager) ServerCore.getPublishManagers().get(id);
				publisherDescription.setText(publisher.getDescription());
				return;
			}
		}
	}
	
	/**
	 * Automatically restart any servers that require it.
	 */
	protected static void autoRestartAll() {
		Trace.trace(Trace.FINEST, "Auto restarting all dirty servers");
	
		Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			if (server.isRestartNeeded()) {
				byte state = server.getServerState();
				String mode = ILaunchManager.RUN_MODE;
				if (state == IServer.SERVER_STARTED_DEBUG)
					mode = ILaunchManager.DEBUG_MODE;
				else if (state == IServer.SERVER_STARTED_PROFILE)
					mode = ILaunchManager.PROFILE_MODE;
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