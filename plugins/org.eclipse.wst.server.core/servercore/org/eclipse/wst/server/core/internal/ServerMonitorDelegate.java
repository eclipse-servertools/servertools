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

import org.eclipse.core.runtime.CoreException;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;
/**
 * An interface to a TCP/IP monitor.
 */
public abstract class ServerMonitorDelegate {
	/**
	 * Start monitoring the given port, and return the port number to
	 * tunnel requests through. The monitorPort is the new port to use, or
	 * -1 to pick a random port.
	 * 
	 * @param server a server
	 * @param port a port
	 * @param monitorPort the port used for monitoring
	 * @return the port used for monitoring
	 * @throws CoreException if anything goes wrong
	 */
	public abstract int startMonitoring(IServer server, ServerPort port, int monitorPort) throws CoreException;

	/**
	 * Stop monitoring the given port.
	 * 
	 * @param server a server
	 * @param port a port
	 */
	public abstract void stopMonitoring(IServer server, ServerPort port);
}