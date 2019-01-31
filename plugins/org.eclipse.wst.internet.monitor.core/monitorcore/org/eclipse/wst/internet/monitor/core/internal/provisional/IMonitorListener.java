/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.core.internal.provisional;
/**
 * Listener for global changes affecting monitors.
 * <p>
 * Clients should implement this interface and register
 * their listener via {@link MonitorCore#addMonitorListener(IMonitorListener)}.
 * </p>
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
