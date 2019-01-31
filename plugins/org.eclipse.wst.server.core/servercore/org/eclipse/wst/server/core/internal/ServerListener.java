/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
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
	 * A new resource has been added.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void serverAdded(IServer server) {
		server.addServerListener(this);
	}

	/**
	 * A existing resource has been removed.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void serverRemoved(IServer server) {
		server.removeServerListener(this);
	}

	public void serverChanged(ServerEvent event) {
		// do nothing
	}
}