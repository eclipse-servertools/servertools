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
 * Listener for global changes affecting monitors.
 * <p>
 * Clients should implement this interface and register
 * their listener via {@linkMonitorCore#addMonitorListener(IMonitorListener)}.
 * </p>
 * 
 * @since 1.0
 */
public interface IMonitorListener {
	
	/**
	 * Notification that the given monitor has been created (added to the
	 * global list of known monitors).
	 * 
	 * @param monitor the newly-created monitor
	 */
	public void monitorAdded(IMonitor monitor);
	
	/**
	 * Notification that the given monitor has been changed.
     * Note that the monitor is never a working copy.
	 * 
	 * @param monitor the monitor that has been changed
	 */
	public void monitorChanged(IMonitor monitor);
	
	/**
	 * Notification that the given monitor has been deleted (removed
	 * from the global list of known monitors).
	 * 
	 * @param monitor the monitor that has been deleted
	 */
	public void monitorRemoved(IMonitor monitor);
}