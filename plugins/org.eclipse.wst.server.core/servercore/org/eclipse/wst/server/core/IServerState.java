/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
 * Interface providing privileged access for setting the state of 
 * a server instance.
 * <p>
 * Objects of this type are passed to IServerDelegate.initialize.
 * </p>
 * <p>
 * [issue: This is pure SPI. Should be in org.eclipse.wst.server.core.model package.]
 * </p>
 * <p>
 * [issue: This API is vulnerable to abuse. Any client with an IServer
 * can cast it to an IServerState and call these methods. A more secure
 * way to achieve the same thing is to have IServerState provide an extra
 * getServer() method rather than extend IServer. That way, casting an
 * IServer is not an option.]
 * </p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @see org.eclipse.wst.server.core.model.IServerDelegate#initialize(IServerState)
 * @since 1.0
 */
public interface IServerState extends IServer {
	
	/**
	 * Sets the current state of this server.
	 * <p>
	 * [issue: byte is rarely used in Java. Use int instead.]
	 * </p>
	 *
	 * @param state one of the server state (<code>SERVER_XXX</code>)
	 * constants declared on {@link IServer}
	 * @see IServer#getServerState()
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