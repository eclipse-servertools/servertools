/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;
/**
 * Listens for messages from the servers. This class keeps
 * track of server instances current state and any clients
 * that are waiting to run on the server. 
 */
public class ServerListener extends ServerLifecycleAdapter implements IServerListener {
	// static instance
	protected static ServerListener listener;

	/**
	 * ServerListener constructor comment.
	 */
	private ServerListener() {
		super();
	}
	
	/**
	 * Get the static instance.
	 *
	 * @return org.eclipse.wst.server.core.internal.plugin.ServerListener
	 */
	public static ServerListener getInstance() {
		if (listener == null)
			listener = new ServerListener();
		return listener;
	}
	
	/**
	 * Called when the server configuration's sync state changes.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 */
	public void configurationSyncStateChange(IServer server) {
		// do nothing
	}

	/**
	 * Notification when the server state has changed.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 */
	public void serverStateChange(IServer server) {
		// do nothing
	}

	/**
	 * Notification when the server state has changed.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 */
	public void modulesChanged(IServer server) {
		// do nothing
	}

	/**
	 * Notification when the state of a module has changed.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 */
	public void moduleStateChange(IServer server, IModule module) {
		// do nothing
	}

	/**
	 * Called when the server isRestartNeeded() property changes.
	 *
	 * @param instance org.eclipse.wst.server.core.model.IServer
	 */
	public void restartStateChange(IServer server) {
		/*if (server.isRestartNeeded() == false)
			return;
		
		byte state = server.getServerState();
		if (state != IServer2.STATE_STARTED && state != IServer2.STATE_STARTED_DEBUG && state != IServer.STATE_STARTED_PROFILE)
			return;
	
		if (ServerCore.getServerPreferences().isAutoRestarting()) {
			try {
				server.restart();
			} catch (CoreException e) {
				Trace.trace(Trace.SEVERE, "Error restarting server", e);
			}
		}*/
	}

	/**
	 * A new resource has been added.
	 *
	 * @param event org.eclipse.wst.server.core.model.IServerResource
	 */
	public void serverAdded(IServer server) {
		server.addServerListener(this);
	}

	/**
	 * A existing resource has been removed.
	 *
	 * @param event org.eclipse.wst.server.core.model.IServerResource
	 */
	public void serverRemoved(IServer server) {
		server.removeServerListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleStateChange(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule)
	 */
	public void moduleStateChange(IServer server, IModule[] parents, IModule module) {
		// do nothing
	}
}