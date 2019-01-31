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

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;

/**
 * An monitored port on a server.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IMonitoredServerPort {
	/**
	 * Returns the server that it being monitored.
	 * 
	 * @return org.eclipse.wst.server.core.IServer
	 */
	public IServer getServer();

	/**
	 * Returns the server port.
	 * 
	 * @return org.eclipse.wst.server.model.IServerPort
	 */
	public ServerPort getServerPort();

	/**
	 * Return the port that is being used to monitor.
	 * 
	 * @return int
	 */
	public int getMonitorPort();

	/**
	 * Returns the content types that are being monitored, or null for all content.
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getContentTypes();

	/**
	 * Returns true if the monitor is currently running/active.
	 * 
	 * @return boolean
	 */
	public boolean isStarted();
}