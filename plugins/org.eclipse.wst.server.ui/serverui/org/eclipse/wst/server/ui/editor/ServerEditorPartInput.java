package org.eclipse.wst.server.ui.editor;
/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2002, 2003 - All Rights Reserved. US Government Users
 * Restricted Rights - Use, duplication or disclosure restricted by GSA ADP
 * Schedule Contract with IBM Corp.
 */
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServerWorkingCopy;

/**
 * 
 */
public class ServerEditorPartInput implements IServerEditorPartInput {
	protected IServerWorkingCopy server;
	protected boolean serverReadOnly;
	protected ICommandManager serverCommandManager;
	
	protected IServerConfigurationWorkingCopy configuration;
	protected boolean configurationReadOnly;
	protected ICommandManager configurationCommandManager;
	
	public ServerEditorPartInput(
			ICommandManager serverCommandManager, IServerWorkingCopy server,  boolean serverReadOnly,
			ICommandManager configurationCommandManager, IServerConfigurationWorkingCopy configuration,  boolean configurationReadOnly) {
		
		this.server = server;
		this.serverReadOnly = serverReadOnly;
		this.serverCommandManager = serverCommandManager;
		
		this.configuration = configuration;
		this.configurationReadOnly = configurationReadOnly;
		this.configurationCommandManager = configurationCommandManager;
	}
	
	public String getName() {
		return "-";
	}

	public String getToolTipText() {
		return "-";
	}

	public boolean exists() {
		return true;
	}
	
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public ImageDescriptor getImageDescriptor() {
		return null;
	}
	
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * Returns the server to be edited.
	 *
	 * @return IServerResource
	 */
	public IServerWorkingCopy getServer() {
		return server;
	}

	/**
	 * Returns true if the server is read-only.
	 * 
	 * @return boolean
	 */
	public boolean isServerReadOnly() {
		return serverReadOnly;
	}
	
	public ICommandManager getServerCommandManager() {
		return serverCommandManager;
	}

	/**
	 * Returns the server configuration to be edited.
	 * 
	 * @return IServerConfiguration
	 */
	public IServerConfigurationWorkingCopy getServerConfiguration() {
		return configuration;
	}

	/**
	 * Returns true if the server configuration is read-only.
	 * 
	 * @return boolean
	 */
	public boolean isServerConfigurationReadOnly() {
		return configurationReadOnly;
	}
	
	public ICommandManager getServerConfigurationCommandManager() {
		return configurationCommandManager;
	}
	
	public String toString() {
		return "ServerEditorPartInput [" + server + ", " + configuration + "]";
	}
}
