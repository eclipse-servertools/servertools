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
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerMonitorManager {
	/**
	 * Returns the monitor that is currently being used.
	 *  
	 * @return
	 */
	//public IServerMonitor getCurrentServerMonitor();
	
	/**
	 * Switch to use a different server monitor. All existing monitors will be
	 * removed from the current monitor and added to the new monitor.
	 * 
	 * @param newMonitor
	 * @throws CoreException
	 */
	//public void setServerMonitor(IServerMonitor newMonitor) throws CoreException;

	/**
	 * Returns the array of ports that are currently being monitored.
	 *
	 * @return
	 */
	public IMonitoredServerPort[] getMonitoredPorts(IServer server);

	/**
	 * Starts monitoring the given port, and returns the new port # to use that will
	 * route to the monitored port.
	 * 
	 * @param server
	 * @param port
	 * @param monitorPort - the new port number to use for monitoring, or -1 to pick a new port
	 * @param content
	 * @return monitor port number
	 */
	public IMonitoredServerPort createMonitor(IServer server, IServerPort port, int monitorPort, String[] content);

	/**
	 * Stop monitoring the given port. Throws a CoreException if there was a problem
	 * stopping the monitoring
	 *
	 * @param port
	 */
	public void removeMonitor(IMonitoredServerPort msp);

	/**
	 * Start the monitor. If the msp port is -1, it will be updated to the port that is actually in use.
	 * 
	 * @param port
	 * @throws CoreException
	 */
	public void startMonitor(IMonitoredServerPort msp) throws CoreException;

	/**
	 * Stop monitoring.
	 * 
	 * @param port
	 */
	public void stopMonitor(IMonitoredServerPort port);

	/**
	 * Returns the monitored port to use when making requests to the given server, port number,
	 * and content. Returns the existing port number if the port is not being monitored.
	 * 
	 * @param server
	 * @param port
	 * @param content
	 * @return the monitored port number
	 */
	public int getMonitoredPort(IServer server, int port, String contentType);
}