/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
/**
 * Helper class which implements the IServerListener interface
 * with empty methods.
 */
public abstract class ServerAdapter implements IServerListener {
	/**
	 * Called when the server configuration's sync state changes.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 */
	public void configurationSyncStateChange(IServer server) {
		// do nothing
	}

	/**
	 * Called when the server isRestartNeeded() property changes.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 */
	public void restartStateChange(IServer server) {
		// do nothing
	}

	/**
	 * Notification when the server state has changed.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 */
	public void serverStateChange(IServer server) {
		// do nothing
	}

	/**
	 * Called when the modules tree of this server has changed.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 */
	public void modulesChanged(IServer server) {
		// do nothing
	}
	
	/**
	 * Fired when a module on this server needs to be published
	 * or no longer needs to be published, or it's state has
	 * changed.
	 *
	 * @param server org.eclipse.wst.server.IServer
	 * @param parents org.eclipse.wst.server.IModule[]
	 * @param module org.eclipse.wst.server.IModule
	 */
	public void moduleStateChange(IServer server, IModule[] parents, IModule module) {
		// do nothing
	}
}