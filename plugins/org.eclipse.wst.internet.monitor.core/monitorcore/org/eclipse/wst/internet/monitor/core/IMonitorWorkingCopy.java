/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core;
/**
 * Represents a working copy of a monitor. A working copy is a copy that the attributes can be changed.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 1.0
 */
public interface IMonitorWorkingCopy extends IMonitor {
	/**
	 * Set the remote (server) host name.
	 * 
	 * @param host the new host name
	 */
	public void setRemoteHost(String host);

	/**
	 * Set the remote (server) host port number.
	 * 
	 * @param port the new port number
	 */
	public void setRemotePort(int port);

	/**
	 * Set the local (client) port number.
	 * 
	 * @param port the local (client) port number
	 */
	public void setLocalPort(int port);

	/**
	 * Set the protocol adapter. This protocol adapter is responsible for translating 
	 * network traffic between the server and the client.
	 * 
	 * @param type the protocol adapter.
	 * [issue: should this variable be renamed to protocolAdapter in here and in the implementation class?]
	 * 
	 */
	public void setProtocolAdapter(IProtocolAdapter type);

	/**
	 * Saves the changes to this working copy and returns the resulting monitor.
	 *
	 * @return the modified or created monitor
	 */
	public IMonitor save();
}