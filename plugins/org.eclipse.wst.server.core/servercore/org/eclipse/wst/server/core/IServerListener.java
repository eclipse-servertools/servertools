/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

/**
 * This interface is used by the server to broadcast a change of state.
 * Usually, the change of state will be caused by some user action,
 * (e.g. requesting to start a server) however, it is equally fine for
 * a server to broadcast a change of state through no direct user action.
 * (e.g. stopping because the server crashed) This information can be
 * used to inform the user of the change or update the UI.
 *
 * <p>Note: The server listener event MUST NOT directly be used to modify
 * the server's state via one of the server's method. For example, a server
 * stopped event cannot directly trigger a start(). Doing this may cause
 * the thread to hang.</p>
 */
public interface IServerListener {
	/**
	 * Called when the server configuration's sync state changes.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 */
	public void configurationSyncStateChange(IServer server);

	/**
	 * Called when the server isRestartNeeded() property changes.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 */
	public void restartStateChange(IServer server);

	/**
	 * Notification when the server state has changed.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 */
	public void serverStateChange(IServer server);

	/**
	 * Called when the modules tree of this server has changed.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 */
	public void modulesChanged(IServer server);

	/**
	 * Fired when a module on this server needs to be published
	 * or no longer needs to be published, or it's state has
	 * changed.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 * @param parents org.eclipse.wst.server.IModule[]
	 * @param module org.eclipse.wst.server.IModule
	 */
	public void moduleStateChange(IServer server, IModule[] parents, IModule module);
}