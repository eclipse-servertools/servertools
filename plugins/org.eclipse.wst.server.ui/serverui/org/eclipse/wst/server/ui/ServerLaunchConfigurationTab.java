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
package org.eclipse.wst.server.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.help.WorkbenchHelp;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

public class ServerLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
	protected String[] serverTypeIds;

	protected Combo serverCombo;
	
	protected Label runtimeLabel;
	protected Label runtimeLocation;
	
	protected IServer server;
	
	// list of servers that are in combo
	protected List servers;

	// flag to be used to decide whether to enable combo in launch config dialog
	// after the user requests a launch, they cannot change it
	private static final String READ_ONLY = "read-only";

	public ServerLaunchConfigurationTab() {
		this(new String[] {"*"});
	}

	public ServerLaunchConfigurationTab(String[] serverTypeIds) {
		this.serverTypeIds = serverTypeIds;
	}

	/**
	 * @see ILaunchConfigurationTab#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.numColumns = 1;
		composite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(data);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(ServerUIPlugin.getResource("%serverLaunchServer"));
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		serverCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		serverCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		serverCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				handleServerSelection();
			}
		});
		WorkbenchHelp.setHelp(serverCombo, ContextIds.LAUNCH_CONFIGURATION_SERVER_COMBO);

		runtimeLabel = new Label(composite, SWT.NONE);
		runtimeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		runtimeLocation = new Label(composite, SWT.NONE);
		runtimeLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// initialize
		IServer[] servers2 = ServerCore.getResourceManager().getServers();
		servers = new ArrayList();
		if (servers2 != null) {
			int size = servers2.length;
			for (int i = 0; i < size; i++) {
				IServer server2 = servers2[i];
				if (isSupportedServer(server2.getServerType().getId())) {
					serverCombo.add(server2.getName());
					servers.add(server2);
				}
			}
		}

		// select first item in list
		if (serverCombo.getItemCount() > 0)
			serverCombo.select(0);

		handleServerSelection();

		serverCombo.forceFocus();

		Dialog.applyDialogFont(composite);
		setControl(composite);
	}
	
	protected boolean isSupportedServer(String serverTypeId) {
		if (serverTypeIds == null)
			return true;
		int size = serverTypeIds.length;
		for (int i = 0; i < size; i++) {
			if (matches(serverTypeId, serverTypeIds[i]))
				return true;
		}
		return false;
	}
	
	protected static boolean matches(String a, String b) {
		if (a == null || b == null || "*".equals(a) || "*".equals(b) || a.startsWith(b) || b.startsWith(a))
			return true;
		return false;
	}
	
	protected void handleServerSelection() {
		if (servers.isEmpty())
			server = null;
		else
			server = (IServer) servers.get(serverCombo.getSelectionIndex());
		IRuntime runtime = null;
		if (server != null) {
			runtime = server.getRuntime();
			runtimeLocation.setText("Location: " + server.getHost());
		} else
			runtimeLocation.setText("Location:");
			
		if (runtime != null)
			runtimeLabel.setText("Runtime: " + runtime.getName());
		else
			runtimeLabel.setText("Runtime:");

		if (server == null)
			setErrorMessage(ServerUIPlugin.getResource("%noServerSelected"));
		else if (server.getServerState() != IServer.STATE_STOPPED)
			setErrorMessage(ServerUIPlugin.getResource("%errorServerAlreadyRunning"));
		else
			setErrorMessage(null);
		/*if (server != null) {
		 server.setLaunchDefaults(configuration);
		 }*/
		updateLaunchConfigurationDialog();
	}

	protected void setErrorMessage(String s) {
		super.setErrorMessage(s);
	}
	
	protected void updateLaunchConfigurationDialog() {
		super.updateLaunchConfigurationDialog();
	}

	/**
	 * @see ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		setErrorMessage(null);
		if (serverCombo != null) {	
			serverCombo.setEnabled(true);
			if (serverCombo.getItemCount() > 0)
				serverCombo.select(0);
		}
		
		if (servers != null) {
			server = (IServer) servers.get(serverCombo.getSelectionIndex());
			if (server != null) {
				server.setLaunchDefaults(configuration, null);
			}
		}
	}

	/**
	 * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		serverCombo.setEnabled(true);
		//remove error message that other instances may have set
		setErrorMessage(null);

		try {
			String serverId = configuration.getAttribute(IServer.ATTR_SERVER_ID, "");
			if (!serverId.equals("")) {
				server = ServerCore.getResourceManager().getServer(serverId);

				if (server == null) { //server no longer exists				
					setErrorMessage(ServerUIPlugin.getResource("%errorInvalidServer"));
					//serverCombo.clearSelection();  //appears to be broken...doesn't work with read only?												
					serverCombo.setEnabled(false);
					return;
				}

				serverCombo.setText(server.getName());
				if (server.getServerState() != IServer.STATE_STOPPED)
					setErrorMessage(ServerUIPlugin.getResource("%errorServerAlreadyRunning"));
			} else {
				if (serverCombo.getItemCount() > 0)
					serverCombo.select(0);
			}
			//flag should only be set if launch has been attempted on the config
			if (configuration.getAttribute(READ_ONLY, false))
				serverCombo.setEnabled(false);
		} catch (CoreException e) {
			// ignore
		}
	}

	/**
	 * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (server != null)
			configuration.setAttribute(IServer.ATTR_SERVER_ID, server.getId());
		else
			configuration.setAttribute(IServer.ATTR_SERVER_ID, (String)null);
	}

	/**
	 * @see ILaunchConfigurationTab#isValid() 
	 */
	public boolean isValid() {
		if (serverCombo.getSelectionIndex() != -1)
			return true;
		return false;
	}

	/**
	 * @see ILaunchConfigurationTab#isValid(ILaunchConfiguration) 
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		try {
			String id = launchConfig.getAttribute(IServer.ATTR_SERVER_ID, "");
			if (!id.equals("")) {
				IServer server2 = ServerCore.getResourceManager().getServer(id);
				if (server2 == null)
					return false;
				if (server2.getServerState() == IServer.STATE_STOPPED)
					return true;
			}
		} catch (CoreException e) {
			// ignore
		}
		return false;
	}

	public Image getImage() {
		return ImageResource.getImage(ImageResource.IMG_SERVER);
	}

	/**
	 * @see ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return ServerUIPlugin.getResource("%serverLaunchConfigurationTab");
	}
}