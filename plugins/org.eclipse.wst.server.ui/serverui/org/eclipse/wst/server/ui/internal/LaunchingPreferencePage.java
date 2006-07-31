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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.RestartServerJob;
import org.eclipse.wst.server.core.internal.ServerPreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
/**
 * The preference page that holds server launching properties.
 */
public class LaunchingPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	protected ServerPreferences preferences;
	protected ServerUIPreferences uiPreferences;

	protected Button publishBeforeStart;
	protected Button autoRestart;

	protected byte saveEditors;
	protected Button[] saveButtons;
	protected int launchMode;
	protected Button[] launchModeButtons;
	protected int launchMode2;
	protected Button[] launchMode2Buttons;
	protected int breakpointEnablement;
	protected Button[] breakpointButtons;

	/**
	 * ServerPreferencesPage constructor comment.
	 */
	public LaunchingPreferencePage() {
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
		
		publishBeforeStart = new Button(composite, SWT.CHECK);
		publishBeforeStart.setText(Messages.prefAutoPublish);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		publishBeforeStart.setLayoutData(data);
		publishBeforeStart.setSelection(preferences.isAutoPublishing());
		whs.setHelp(publishBeforeStart, ContextIds.PREF_GENERAL_PUBLISH_BEFORE_START);
		
		autoRestart = new Button(composite, SWT.CHECK);
		autoRestart.setText(Messages.prefAutoRestart);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		autoRestart.setLayoutData(data);
		autoRestart.setSelection(preferences.isAutoRestarting());
		whs.setHelp(autoRestart, ContextIds.PREF_GENERAL_AUTO_RESTART);
			
		Label label = new Label(composite, SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 3;
		label.setLayoutData(data);
		
		createSaveEditorGroup(composite);
		
		createLaunchModeGroup(composite);
		
		createLaunchMode2Group(composite);
		
		createBreakpointsGroup(composite);
		
		setSaveEditorStatus(uiPreferences.getSaveEditors());
		setLaunchModeStatus(uiPreferences.getLaunchMode());
		setLaunchMode2Status(uiPreferences.getLaunchMode2());
		setBreakpointsStatus(uiPreferences.getEnableBreakpoints());
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}

	protected void createSaveEditorGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.prefSaveEditors);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		group.setLayoutData(data);
		
		String[] messages = new String[] {
			Messages.prefSaveEditorsAlways, Messages.prefSaveEditorsNever,
			Messages.prefSaveEditorsPrompt
		};
		
		final byte[] options = new byte[] {
			ServerUIPreferences.SAVE_EDITORS_ALWAYS,
			ServerUIPreferences.SAVE_EDITORS_NEVER,
			ServerUIPreferences.SAVE_EDITORS_PROMPT
		};
		
		Button[] buttons = new Button[3];
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		for (int i = 0; i < 3; i++) {
			buttons[i] = new Button(group, SWT.RADIO);
			buttons[i].setText(messages[i]);
			final int b = i;
			buttons[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					saveEditors = options[b];
				}
			});
			whs.setHelp(buttons[i], ContextIds.PREF_GENERAL_SAVE_EDITORS);
		}
		saveButtons = buttons;
	}

	protected void setSaveEditorStatus(byte status) {
		saveEditors = status;
		saveButtons[0].setSelection(saveEditors == ServerUIPreferences.SAVE_EDITORS_ALWAYS);
		saveButtons[1].setSelection(saveEditors == ServerUIPreferences.SAVE_EDITORS_NEVER);
		saveButtons[2].setSelection(saveEditors == ServerUIPreferences.SAVE_EDITORS_PROMPT);
	}

	protected void createLaunchModeGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.prefLaunchMode);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		group.setLayoutData(data);
		
		String[] messages = new String[] {
			Messages.prefLaunchModeRestart, Messages.prefLaunchModeContinue,
			Messages.prefLaunchModePrompt
		};
		
		final byte[] options = new byte[] {
			ServerUIPreferences.LAUNCH_MODE_RESTART,
			ServerUIPreferences.LAUNCH_MODE_CONTINUE,
			ServerUIPreferences.LAUNCH_MODE_PROMPT
		};
		
		Button[] buttons = new Button[3];
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		for (int i = 0; i < 3; i++) {
			buttons[i] = new Button(group, SWT.RADIO);
			buttons[i].setText(messages[i]);
			final int b = i;
			buttons[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					launchMode = options[b];
				}
			});
			whs.setHelp(buttons[i], ContextIds.PREF_GENERAL_LAUNCH_MODE);
		}
		launchModeButtons = buttons;
	}

	protected void setLaunchModeStatus(int mode) {
		launchMode = mode;
		launchModeButtons[0].setSelection(launchMode == ServerUIPreferences.LAUNCH_MODE_RESTART);
		launchModeButtons[1].setSelection(launchMode == ServerUIPreferences.LAUNCH_MODE_CONTINUE);
		launchModeButtons[2].setSelection(launchMode == ServerUIPreferences.LAUNCH_MODE_PROMPT); 
	}

	protected void createLaunchMode2Group(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.prefLaunchMode2);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		group.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		group.setLayoutData(data);
		
		String[] messages = new String[] {
			Messages.prefLaunchMode2Restart, Messages.prefLaunchMode2Breakpoints,
			Messages.prefLaunchMode2Continue, Messages.prefLaunchMode2Prompt
		};
		
		final byte[] options = new byte[] {
			ServerUIPreferences.LAUNCH_MODE2_RESTART,
			ServerUIPreferences.LAUNCH_MODE2_DISABLE_BREAKPOINTS,
			ServerUIPreferences.LAUNCH_MODE2_CONTINUE,
			ServerUIPreferences.LAUNCH_MODE2_PROMPT
		};
		
		Button[] buttons = new Button[4];
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		for (int i = 0; i < 4; i++) {
			buttons[i] = new Button(group, SWT.RADIO);
			buttons[i].setText(messages[i]);
			final int b = i;
			buttons[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					launchMode2 = options[b];
				}
			});
			whs.setHelp(buttons[i], ContextIds.PREF_GENERAL_LAUNCH_MODE_DEBUG);
		}
		launchMode2Buttons = buttons;
	}

	protected void setLaunchMode2Status(int mode) {
		launchMode2 = mode;
		launchMode2Buttons[0].setSelection(launchMode2 == ServerUIPreferences.LAUNCH_MODE2_RESTART);
		launchMode2Buttons[1].setSelection(launchMode2 == ServerUIPreferences.LAUNCH_MODE2_DISABLE_BREAKPOINTS);
		launchMode2Buttons[2].setSelection(launchMode2 == ServerUIPreferences.LAUNCH_MODE2_CONTINUE);
		launchMode2Buttons[3].setSelection(launchMode2 == ServerUIPreferences.LAUNCH_MODE2_PROMPT);
	}

	protected void createBreakpointsGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.prefBreakpoints);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		group.setLayoutData(data);
		
		String[] messages = new String[] {
			Messages.prefBreakpointsAlways, Messages.prefBreakpointsNever,
			Messages.prefBreakpointsPrompt
		};
		
		final byte[] options = new byte[] {
			ServerUIPreferences.ENABLE_BREAKPOINTS_ALWAYS,
			ServerUIPreferences.ENABLE_BREAKPOINTS_NEVER,
			ServerUIPreferences.ENABLE_BREAKPOINTS_PROMPT
		};
		
		Button[] buttons = new Button[3];
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		for (int i = 0; i < 3; i++) {
			buttons[i] = new Button(group, SWT.RADIO);
			buttons[i].setText(messages[i]);
			final int b = i;
			buttons[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					breakpointEnablement = options[b];
				}
			});
			whs.setHelp(buttons[i], ContextIds.PREF_GENERAL_ENABLE_BREAKPOINTS);
		}
		breakpointButtons = buttons;
	}

	protected void setBreakpointsStatus(int mode) {
		breakpointEnablement = mode;
		breakpointButtons[0].setSelection(breakpointEnablement == ServerUIPreferences.ENABLE_BREAKPOINTS_ALWAYS);
		breakpointButtons[1].setSelection(breakpointEnablement == ServerUIPreferences.ENABLE_BREAKPOINTS_NEVER);
		breakpointButtons[2].setSelection(breakpointEnablement == ServerUIPreferences.ENABLE_BREAKPOINTS_PROMPT); 
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
		
		setSaveEditorStatus(uiPreferences.getDefaultSaveEditors());
		setLaunchModeStatus(uiPreferences.getDefaultLaunchMode());
		setLaunchMode2Status(uiPreferences.getDefaultLaunchMode2());
		setBreakpointsStatus(uiPreferences.getDefaultEnableBreakpoints());
		
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		preferences.setAutoPublishing(publishBeforeStart.getSelection());
		preferences.setAutoRestarting(autoRestart.getSelection());
		uiPreferences.setSaveEditors(saveEditors);
		uiPreferences.setLaunchMode(launchMode);
		uiPreferences.setLaunchMode2(launchMode2);
		uiPreferences.setEnableBreakpoints(breakpointEnablement);
		
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
				if (server.getServerState() == IServer.STATE_STARTED && server.getServerRestartState()) {
					String mode = server.getMode();
					if (server.canRestart(mode).isOK()) {
						RestartServerJob job = new RestartServerJob(server, mode);
						job.schedule();
					}
				}
			}
		}
	}
}