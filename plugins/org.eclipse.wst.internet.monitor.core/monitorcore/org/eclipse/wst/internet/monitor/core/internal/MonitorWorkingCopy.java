/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Tianchao Li (Tianchao.Li@gmail.com) - Start monitors by default 
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitorWorkingCopy;
/**
 * 
 */
public class MonitorWorkingCopy extends Monitor implements IMonitorWorkingCopy {
	protected Monitor monitor;
	
	/**
	 * Create a new monitor working copy. (used for initial creation)
	 */
	public MonitorWorkingCopy() {
		// do nothing
	}

	/**
	 * Create a new monitor working copy. (used for working copies)
	 * 
	 * @param monitor the monitor this working copy is for
	 */
	public MonitorWorkingCopy(Monitor monitor) {
		this.monitor = monitor;
		setInternal(monitor);
	}

	/** (non-Javadoc)
	 * @see IMonitorWorkingCopy#getOriginal()
	 */
	public IMonitor getOriginal() {
		return monitor;
	}

	/**
	 * Set the id.
	 * 
	 * @param newId the id
	 */
	public void setId(String newId) {
		id = newId;
	}

	/** (non-Javadoc)
	 * @see IMonitorWorkingCopy#setRemoteHost(String)
	 */
	public void setRemoteHost(String host) {
		remoteHost = host;
	}

	/** (non-Javadoc)
	 * @see IMonitorWorkingCopy#setRemotePort(int)
	 */
	public void setRemotePort(int port) {
		remotePort = port;
	}

	/** (non-Javadoc)
	 * @see IMonitorWorkingCopy#setLocalPort(int)
	 */
	public void setLocalPort(int port) {
		localPort = port;
	}

	/** (non-Javadoc)
	 * @see IMonitorWorkingCopy#setProtocol(String)
	 */
	public void setProtocol(String protocolId2) {
		protocolId = protocolId2;
	}

	/** (non-Javadoc)
	 * @see IMonitorWorkingCopy#setTimeout(int)
	 */
	public void setTimeout(int timeout2) {
		timeout = timeout2;
	}

	/** (non-Javadoc)
	 * @see IMonitorWorkingCopy#setAutoStart(boolean)
	 */
	public void setAutoStart(boolean startByDefault) {
		autoStart = startByDefault;
	}

	/**
	 * @see IMonitor#isWorkingCopy()
	 */
	public boolean isWorkingCopy() {
		return true;
	}
	
	/**
	 * @see IMonitor#createWorkingCopy()
	 */
	public IMonitorWorkingCopy createWorkingCopy() {
		return this;
	}

	/**
	 * @see IMonitorWorkingCopy#save()
	 */
	public synchronized IMonitor save() {
		MonitorManager mm = MonitorManager.getInstance();
		if (monitor != null) {
			//boolean restart = false;
			if (monitor.isRunning()) {
				//restart = true;
				mm.stopMonitor(monitor);
			}
			monitor.setInternal(this);
			mm.monitorChanged(monitor);
			//if (restart)
			//	mm.startMonitor(monitor);
		} else {
			monitor = new Monitor();
			monitor.setInternal(this);
			mm.addMonitor(monitor);
		}
		return monitor;
	}
}