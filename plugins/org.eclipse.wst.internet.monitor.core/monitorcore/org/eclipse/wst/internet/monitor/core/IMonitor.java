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
 * Represents the monitor that monitors the network traffic between a server and a client.
 * The global list of known monitors is available via {@link MonitorCore.getMonitors()}.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @see IMonitorWorkingCopy
 * @since 1.0
 */
public interface IMonitor {
	/**
	 * Returns the id of this monitor.
	 * Each monitor has a distinct, fixed id. Ids are intended to be used internally as keys;
	 * they are not intended to be shown to end users.
	 * 
	 * @return the element id
	 */
	public String getId();

	/**
	 * Returns the remote host name of the server to be monitored. 
	 * 
	 * @return the remote host name
	 */
	public String getRemoteHost();
	
	/**
	 * Returns the remote port number of the server to be monitored.
	 *  
	 * @return the remote port number
	 */
	public int getRemotePort();
	
	/**
	 * Returns the local port number of the client that monitor traffic will used.
	 *  
	 * @return the local port number
	 */
	public int getLocalPort();
	
	/**
	 * Returns the protocol adapter that the monitor will be used to read the monitor traffic.
	 *   
	 * @return the protocol adapter
	 */
	public IProtocolAdapter getProtocolAdapter();
	
	/**
	 * Returns whether this monitor is currently running.
	 * 
	 * @return <code>true</code> if the monitor is currently running; otherwise, return <code>false</code>.
	 */
	public boolean isRunning();
	
	/**
	 * Deletes this monitor. The monitor will no longer be available to users.
	 * This method has no effect if the monitor has already been deleted.
	 */
	public void delete();
	
	/**
	 * Returns whether this monitor is a working copy. Monitors which return
	 * <code>true</code> to this method can be safely cast to 
	 * <code>org.eclipse.wst.internet.monitor.core.IMonitorWorkingCopy</code>
	 * 
	 * @return whether this monitor is a working copy
	 */
	public boolean isWorkingCopy();
	
	/**
	 * Returns a working copy of this monitor. Changes to the working copy will be
	 * applied to this monitor when saved.
	 * 
	 * @return a working copy of this monitor
	 */
	public IMonitorWorkingCopy getWorkingCopy();
}