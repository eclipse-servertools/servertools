/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Tianchao Li (Tianchao.Li@gmail.com) - Start monitors by default 
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.provisional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
/**
 * Represents a monitor between a client and server.
 * The monitor watches all network traffic between a local client (talking
 * on a given local port) and a remote server (identified by host and port).
 * The global list of known monitors is available via {@link MonitorCore#getMonitors()}.
 * IMonitor is thread-safe.
 * <p>
 * IMonitors are read-only. To make changes to a monitor, you must create an
 * IMonitorWorkingCopy by calling createWorkingCopy(). Changes to the working copy
 * are applied when the working copy is saved. Monitors and monitor working copies
 * have the following properties:
 *   * equals() returns true for a monitor and it's working copy
 * <p>
 * [issue: Why the built-in assumption that the client is local? A monitor
 * would make just as much sense sitting between a client on a remote machine
 * and a server.]
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @see IMonitorWorkingCopy
 */
public interface IMonitor {
	/**
	 * Returns the id of this monitor.
	 * Each monitor has a distinct, fixed id. Ids are intended to be used internally as keys;
	 * they are not intended to be shown to end users.
	 * 
	 * @return the monitor id
	 */
	public String getId();

	/**
	 * Returns the remote host name of the server being monitored.
	 * 
	 * @return the remote host name
	 */
	public String getRemoteHost();
	
	/**
	 * Returns the remote port number of the server being monitored.
	 *  
	 * @return the remote port number
	 */
	public int getRemotePort();
	
	/**
	 * Returns the local port number of the client being monitored. This
	 * is the port that the client is talking to the remote server on.
	 *  
	 * @return the local port number
	 */
	public int getLocalPort();
	
	/**
	 * Returns the protocol that this monitor uses to read network
	 * traffic.
	 * 
	 * @return the protocol id
	 */
	public String getProtocol();

	/**
	 * Returns the connection timeout. Returns 0 if there is no timeout.
	 * 
	 * @return the timeout
	 */
	public int getTimeout();

	/**
	 * Returns whether this monitor starts by default.
	 * 
	 * @return <code>true</code> if the monitor should be started by default, or
	 *    <code>false</code> otherwise
	 */
	public boolean isAutoStart();

	/**
	 * Returns whether this monitor is currently running. Monitor working
	 * copies will always return false (since they cannot be run).
	 * 
	 * @return <code>true</code> if the monitor is currently running, or
	 *    <code>false</code> otherwise
	 */
	public boolean isRunning();
	
	/**
	 * Deletes this monitor. The monitor will no longer be available to clients.
	 * If the monitor is currently running, it will be stopped first.
	 * This method has no effect if the monitor has already been deleted or if
	 * it is called on a working copy. Clients have no obligation to delete
	 * working copies.
	 */
	public void delete();
	
	/**
	 * Returns whether this monitor is a working copy. Monitors which return
	 * <code>true</code> to this method can be safely cast to
	 * {@link IMonitorWorkingCopy}.
	 * 
	 * @return <code>true</code> if this monitor is a working copy, and
	 * <code>false</code> otherwise
	 */
	public boolean isWorkingCopy();
	
	/**
	 * Returns a working copy for this monitor. If the receiver is not a
	 * working copy, a new working copy will be created and initialized to have
	 * the same attributes as this monitor. If the receiver is a working copy,
	 * this method simply returns the receiver. After configuring attributes on
	 * the working copy, calling {@link IMonitorWorkingCopy#save()} applies
	 * the changes to the original monitor.
	 * 
	 * @return a working copy of this monitor
	 */
	public IMonitorWorkingCopy createWorkingCopy();
	
	/**
	 * Starts the given monitor listening on its client port. This method is
	 * synchronous and the monitor will be running and ready for use by the
	 * time that the method returns. This method has no effect if the monitor
	 * is already running.
	 * <p>
	 * A CoreException is thrown if the monitor is not valid, the local port
	 * is in use, or if another problem occurs when starting the monitor. 
	 * </p>
	 * <p>
	 * This method must not be called on a working copy or after the monitor
	 * has been deleted.
	 * </p>
	 * 
	 * @throws CoreException thrown if the monitor's properties are invalid,
	 *   if it fails to start because the port is in use, or another problem occurs
	 */
	public void start() throws CoreException;

	/**
	 * Stops the given monitor and frees up all underlying operating 
	 * system resources. This method is synchronous and the monitor will be
	 * running and ready for use by the time that the method returns.
	 * This method has no effect if the monitor was not already running.
	 * <p>
	 * After returning from this method, the monitor may be restarted at
	 * any time. This method must not be called on a working copy or after
	 * the monitor has been deleted.
	 * </p>
	 */
	public void stop();

	/**
	 * Adds a request listener.
	 * Once registered, a listener starts receiving notification of 
	 * changes to the global list of requests. The listener continues to receive
	 * notifications until it is removed.
	 * Has no effect if an identical listener is already registered.
	 * <p>
	 * If a listener is added to a working copy, it will automatically be added
	 * to the original monitor. If the monitor does not exist yet (when the working
	 * copy was just created from MonitorCore.createMonitor()), the listener will
	 * be added to the created monitor when (if) the working copy is saved. 
	 * </p>
	 *
	 * @param listener the request listener
	 * @see #removeRequestListener(IRequestListener)
	 */
	public void addRequestListener(IRequestListener listener);
	
	/**
	 * Removes the given request listener. Has no effect if the listener is
	 * not registered.
	 * <p>
	 * If a listener is removed from a working copy, it will automatically be
	 * removed from the corresponding original monitor. Removing a monitor from
	 * a newly created monitor has no effect unless the monitor had already been
	 * added, in which case it is removed from notification and will not be added
	 * to the created monitor when (if) the working copy is saved. 
	 * </p>
	 * 
	 * @param listener the listener
	 * @see #addRequestListener(IRequestListener)
	 */
	public void removeRequestListener(IRequestListener listener);
	
	/**
	 * Validates this monitor. This method should return an error if the monitor
	 * has invalid ports or remote hostname.
	 * <p>
	 * This method is not on the working copy so that the runtime can be validated
	 * at any time.
	 * </p>
	 *
	 * @return a status object with code <code>IStatus.OK</code> if this
	 *   runtime is valid, otherwise a status object indicating what is
	 *   wrong with it
	 */
	public IStatus validate();
}