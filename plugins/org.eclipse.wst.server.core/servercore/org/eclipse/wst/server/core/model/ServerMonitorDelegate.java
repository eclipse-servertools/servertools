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
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerPort;
/**
 * An interface to a TCP/IP monitor.
 */
public abstract class ServerMonitorDelegate {
	/**
	 * Start monitoring the given port, and return the port number to
	 * tunnel requests through. The monitorPort is the new port to use, or
	 * -1 to pick a random port.
	 * 
	 * @param port
	 * @return
	 */
	public abstract int startMonitoring(IServer server, IServerPort port, int monitorPort) throws CoreException;

	/**
	 * Stop monitoring the given port.
	 * @param port
	 */
	public abstract void stopMonitoring(IServer server, IServerPort port);
}