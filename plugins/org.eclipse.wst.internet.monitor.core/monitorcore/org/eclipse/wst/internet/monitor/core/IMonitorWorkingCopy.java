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

import org.eclipse.core.runtime.CoreException;
/**
 * Represents a working copy of a monitor. A working copy is a copy that the
 * attributes can be changed.
 * IMonitorWorkingCopy is thread-safe. However, working copies instances
 * should be short-lived to reduce the chance of multiple working copies
 * being created by different clients and one client overwritting changes
 * made to the other working copy. 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @see IMonitor
 * @since 1.0
 * 
 * [issue : CS - it sounds like this is something useful for creating monitors. 
 * Shouldn't this be called a IMonitorConfiguration?  Is there an advantage to making
 * this actually seem to be a IMonitor?  Perhaps some UI convenience?  
 * Is a IMonitorWorkingCopy actually 'useable' for monitoring .. or is it really just a configuration? ]
 */
public interface IMonitorWorkingCopy extends IMonitor {
	/**
	 * Returns the original monitor that this working copy corresponds to, or
	 * <code>null</code> if this working copy was just created from
	 * MonitorCore.createMonitor().
	 * 
	 * @return the original monitor, or <code>null</code> if this working copy
	 *    was just created
	 */
	public IMonitor getOriginal();

	/**
	 * Sets the local port number of the client to be monitored.
	 * 
	 * @param port the local (client) port number
	 * @see IMonitor#getLocalPort()
	 */
	public void setLocalPort(int port);

	/**
	 * Sets the remote host name of the server to be monitored.
	 * 
	 * @param host the new remote host name
	 * @see IMonitor#getRemoteHost()
	 */
	public void setRemoteHost(String host);

	/**
	 * Sets the remote port number of the server to be monitored.
	 * 
	 * @param port the new remote port number
	 * @see IMonitor#getRemotePort()
	 */
	public void setRemotePort(int port);

	/**
	 * Sets the protocol to be used to read network
	 * traffic between the server and the client.
	 * 
	 * @param protocolId the protocol id
	 * @see IMonitor#getProtocol()
	 */
	public void setProtocol(String protocolId);

	/**
	 * Saves the changes made to this working copy.
	 * For a brand new working copy (created by
	 * {@link MonitorCore#createMonitor()}, and not yet saved), this method
	 * creates a new monitor instance with attributes matching this working copy.
	 * For a working copy cloned from an existing monitor instance (by
	 * {@link IMonitor#createWorkingCopy()}), this method stops the existing
	 * monitor (using {@link MonitorCore#stopMonitor(IMonitor)}) if necessary,
	 * and then sets the attributes of the monitor instance to match this
	 * working copy (the monitor instance is returned).
	 * <p>
	 * Saving a working copy for a monitor that was already deleted will cause
	 * the monitor to get recreated (with any changes in the working copy).
	 * </p>
	 * <p>
	 * This method throws a CoreException if there is a problem saving the
	 * monitor. No validation checks occur when saving the monitor. This can be
	 * done by calling IMonitor.validate() prior to saving.
	 * </p>
	 *
	 * @return the affected monitor
	 * @throws CoreException thrown if a problem occurs while saving the monitor 
	 */
	public IMonitor save() throws CoreException;
}