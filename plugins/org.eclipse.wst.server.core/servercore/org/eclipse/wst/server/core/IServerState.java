/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.wst.server.core.model.IModule;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerState extends IServer {	
	/**
	 * Set the server state.
	 * 
	 * @param state
	 */
	public void setServerState(byte state);

	/**
	 * Sets the server restart state.
	 *
	 * @param state boolean
	 */
	public void setRestartNeeded(boolean state);

	/**
	 * Sets the configuration sync state.
	 *
	 * @param state byte
	 */
	public void setConfigurationSyncState(byte state);

	/**
	 * Hook to fire an event when a module state changes.
	 * 
	 * @param module
	 * @param state
	 */
	public void updateModuleState(IModule module);
}