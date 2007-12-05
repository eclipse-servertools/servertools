/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.core.internal.ILaunchableAdapter;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerLaunchConfigurationDelegate;
/**
 * Run on Server launch configuration tab.
 */
public class RunOnServerLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
	//private ILaunchConfigurationWorkingCopy wc;

	protected IServer server;
	protected IModule module;
	protected ModuleArtifactDelegate moduleArtifact;
	protected ILaunchableAdapter launchableAdapter;
	protected IClient client;

	protected Label serverLabel;
	protected Label moduleArtifactLabel;
	protected Label clientLabel;

	/**
	 * Create a new launch configuration tab.
	 */
	public RunOnServerLaunchConfigurationTab() {
		super();
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
		
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.serverLaunchServer);
		serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.serverLaunchArtifact);
		moduleArtifactLabel = new Label(composite, SWT.NONE);
		moduleArtifactLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.serverLaunchClient);
		clientLabel = new Label(composite, SWT.NONE);
		clientLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Dialog.applyDialogFont(composite);
		setControl(composite);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		setErrorMessage(null);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		setErrorMessage(null);
		
		try {
			String serverId = configuration.getAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_SERVER_ID, (String)null);
			String moduleArt = configuration.getAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_MODULE_ARTIFACT, (String)null);
			String moduleArtifactClass = configuration.getAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_MODULE_ARTIFACT_CLASS, (String)null);
			String laId = configuration.getAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_LAUNCHABLE_ADAPTER_ID, (String)null);
			String clientId = configuration.getAttribute(RunOnServerLaunchConfigurationDelegate.ATTR_CLIENT_ID, (String)null);
			
			try {
				server = ServerCore.findServer(serverId);
			} catch (IllegalArgumentException e) {
				// ignore
			}
			module = null;
			moduleArtifact = null;
			try {
				launchableAdapter = ServerPlugin.findLaunchableAdapter(laId);
			} catch (IllegalArgumentException e) {
				// ignore
			}
			try {
				client = ServerPlugin.findClient(clientId);
			} catch (IllegalArgumentException e) {
				// ignore
			}
			
			try {
				Class c = Class.forName(moduleArtifactClass);
				moduleArtifact = (ModuleArtifactDelegate) c.newInstance();
				moduleArtifact.deserialize(moduleArt);
				module = moduleArtifact.getModule();
			} catch (Throwable t) {
				Trace.trace(Trace.WARNING, "Could not load module artifact delegate class");
			}
			
			if (server == null)
				serverLabel.setText("unknown");
			else
				serverLabel.setText(server.getName());
			
			if (moduleArtifact == null)
				moduleArtifactLabel.setText("unknown");
			else
				moduleArtifactLabel.setText(moduleArtifact.getName());
			
			if (client == null)
				clientLabel.setText("unknown");
			else
				clientLabel.setText(client.getName());
		} catch (CoreException e) {
			// ignore
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// do nothing
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

	/*
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return ImageResource.getImage(ImageResource.IMG_ETOOL_RUN_ON_SERVER);
	}

	/*
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return Messages.serverLaunchConfigurationTab;
	}
}