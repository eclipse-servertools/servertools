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
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
/**
 * Helper class which implements all of the IServerListener
 * interface with empty methods.
 */
public class ServerAdapter implements IServerListener {
	/**
	 * ServerAdapter constructor comment.
	 */
	public ServerAdapter() {
		super();
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
	 * Called when the server isRestartNeeded() property changes.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 */
	public void restartStateChange(IServer server) {
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
	 * Notification when the state of a module has changed.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 */
	public void moduleStateChange(IServer server, IModule module) {
		// do nothing
	}

	public void modulesChanged(IServer server) {
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublishListener#moduleStateChange(org.eclipse.wst.server.core.IServer2, java.util.List, org.eclipse.wst.server.core.model.IModule)
	 */
	public void moduleStateChange(IServer server, IModule[] parents, IModule module) {
		// do nothing
	}

}