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
package org.eclipse.wst.monitor.core;
/**
 * Listener that listen to the monitor add, remove and changes.
 * 
 * @since 1.0
 */
public interface IMonitorListener {
	/**
	 * This method is being called when a monitor is added.
	 * 
	 * @param monitor the monitor that has been added
	 */
	public void monitorAdded(IMonitor monitor);
	
	/**
	 * This method is being called when a monitor is changed.
	 * 
	 * @param monitor the monitor that has been changed
	 */
	public void monitorChanged(IMonitor monitor);
	
	/**
	 * This method is being called when a monitor is removed.
	 * 
	 * @param monitor the monitor that has been removed
	 */
	public void monitorRemoved(IMonitor monitor);
}