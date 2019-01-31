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
package org.eclipse.wst.server.core;
/**
 * Listener interface for changes to servers.
 * <p>
 * This interface is fired whenever a server is added, modified, or removed.
 * All events are fired post-change, so that all server tools API called as a
 * result of the event will return the updated results. (for example, on
 * serverAdded the new server will be in the global list of servers
 * ({@link ServerCore#getServers()}), and on serverRemoved the server will
 * not be in the list.
 * </p>
 * 
 * @see ServerCore
 * @see IServer
 * @since 1.0
 */
public interface IServerLifecycleListener {
	/**
	 * A new server has been created.
	 *
	 * @param server the new server
	 */
	public void serverAdded(IServer server);

	/**
	 * An existing server has been updated or modified.
	 *
	 * @param server the modified server
	 */
	public void serverChanged(IServer server);

	/**
	 * A existing server has been removed.
	 *
	 * @param server the removed server
	 */
	public void serverRemoved(IServer server);
}