/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.ui.PlatformUI;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.ui.internal.*;
/**
 * Server launch configuration tab.
 * 
 * @since 1.0
 */
public class ServerLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
	// flag to be used to decide whether to enable combo in launch config dialog
	// after the user requests a launch, they cannot change it
	private static final String READ_ONLY = "read-only";

	private String[] serverTypeIds;

	private Combo serverCombo;

	private Label runtimeLabel;
	private Label hostname;

	private IServer server;

	// list of servers that are in combo
	private List<IServer> servers;

	private ILaunchConfigurationWorkingCopy wc;

	/**
	 * Create a new server launch configuration tab.
	 */
	public ServerLaunchConfigurationTab() {
		this(new String[] {"*"});
	}

	/**
	 * Create a new server launch configuration tab with the given server type ids.
	 * 
	 * @param serverTypeIds an array of server type ids
	 */
	public ServerLaunchConfigurationTab(String[] serverTypeIds) {
		this.serverTypeIds = serverTypeIds;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns = 2;
		composite.setLayout(layout);

		//GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		//data.widthHint = 200;
		//composite.setLayoutData(data);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.serverLaunchDescription);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = false;
		data.widthHint = 300;
		label.setLayoutData(data);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.serverLaunchServer);
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
		PlatformUI.getWorkbench().getHelpSystem().setHelp(serverCombo, ContextIds.LAUNCH_CONFIGURATION_SERVER_COMBO);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.serverLaunchRuntime);
		runtimeLabel = new Label(composite, SWT.NONE);
		runtimeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.serverLaunchHost);
		hostname = new Label(composite, SWT.NONE);
		hostname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// initialize
		IServer[] servers2 = ServerCore.getServers();
		servers = new ArrayList<IServer>();
		if (servers2 != null) {
			int size = servers2.length;
			for (int i = 0; i < size; i++) {
				IServer server2 = servers2[i];
				if (server2.getServerType() != null && isSupportedServer(server2.getServerType().getId())) {
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

	private boolean isSupportedServer(String serverTypeId) {
		if (serverTypeIds == null)
			return true;
		int size = serverTypeIds.length;
		for (int i = 0; i < size; i++) {
			if (matches(serverTypeId, serverTypeIds[i]))
				return true;
		}
		return false;
	}

	private static boolean matches(String a, String b) {
		if (a == null || b == null || "*".equals(a) || "*".equals(b) || a.startsWith(b) || b.startsWith(a))
			return true;
		return false;
	}

	/**
	 * Called when a server is selected.
	 * This method should not be called directly.
	 */
	protected void handleServerSelection() {
		if (servers.isEmpty())
			server = null;
		else
			server = servers.get(serverCombo.getSelectionIndex());
		IRuntime runtime = null;
		if (server != null) {
			runtime = server.getRuntime();
			hostname.setText(server.getHost());
		} else
			hostname.setText("");
		
		// check if "runtime" property is true or false
		if (runtime != null && server != null && server.getServerType() != null && server.getServerType().hasRuntime())
			runtimeLabel.setText(runtime.getName());
		else
			runtimeLabel.setText("");
		
		try {
			if (wc != null)
				((Server)server).setupLaunchConfiguration(wc, new NullProgressMonitor());
		} catch (Exception e) {
			// ignore
		}
		
		if (server == null)
			setErrorMessage(Messages.errorNoServerSelected);
		else if (server.getServerState() != IServer.STATE_STOPPED)
			setErrorMessage(Messages.errorServerAlreadyRunning);
		else
			setErrorMessage(null);
		
		updateLaunchConfigurationDialog();
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		setErrorMessage(null);
		if (serverCombo != null) {	
			serverCombo.setEnabled(true);
			if (serverCombo.getItemCount() > 0)
				serverCombo.select(0);
		}
		
		if (servers != null) {
			server = servers.get(serverCombo.getSelectionIndex());
			if (server != null)
				((Server) server).setupLaunchConfiguration(configuration, null);
		}
		wc = configuration;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		serverCombo.setEnabled(true);
		// remove error message that other instances may have set
		setErrorMessage(null);
		
		try {
			// bug 137822 - set the ILaunchConfigurationWorkingCopy wc variable before calling the method handleServerSelection()
			//if (configuration instanceof ILaunchConfigurationWorkingCopy)
			//	wc = (ILaunchConfigurationWorkingCopy)configuration;
			
			String serverId = configuration.getAttribute(Server.ATTR_SERVER_ID, "");
			if (serverId != null && !serverId.equals("")) {
				server = ServerCore.findServer(serverId);
				
				if (server == null) { // server no longer exists				
					setErrorMessage(Messages.errorInvalidServer);
					//serverCombo.clearSelection();  // appears to be broken...doesn't work with read only?												
					serverCombo.setEnabled(false);
					return;
				}
				
				serverCombo.setText(server.getName());
				if (server.getServerState() != IServer.STATE_STOPPED)
					setErrorMessage(Messages.errorServerAlreadyRunning);
			} else {
				if (serverCombo.getItemCount() > 0)
					serverCombo.select(0);
			}
			handleServerSelection();
			
			// flag should only be set if launch has been attempted on the config
			if (configuration.getAttribute(READ_ONLY, false))
				serverCombo.setEnabled(false);
		} catch (CoreException e) {
			// ignore
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (server != null)
			configuration.setAttribute(Server.ATTR_SERVER_ID, server.getId());
		else
			configuration.setAttribute(Server.ATTR_SERVER_ID, (String)null);
		wc = configuration;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(ILaunchConfiguration) 
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		try {
			String id = launchConfig.getAttribute(Server.ATTR_SERVER_ID, "");
			if (id != null && !id.equals("")) {
				IServer server2 = ServerCore.findServer(id);
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

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return ImageResource.getImage(ImageResource.IMG_SERVER);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return Messages.serverLaunchConfigurationTab;
	}
}
