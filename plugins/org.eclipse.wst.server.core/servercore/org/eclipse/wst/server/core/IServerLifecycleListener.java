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
package org.eclipse.wst.server.core;
/**
 * Listener interface for server changes.
 */
public interface IServerLifecycleListener {
	/**
	 * A new server has been created.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void serverAdded(IServer server);

	/**
	 * An existing server has been updated or modified.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void serverChanged(IServer server);

	/**
	 * A existing server has been removed.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void serverRemoved(IServer server);
}