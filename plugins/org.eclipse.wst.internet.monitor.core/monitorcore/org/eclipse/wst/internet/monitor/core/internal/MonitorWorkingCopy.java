/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import org.eclipse.wst.internet.monitor.core.*;
/**
 * 
 */
public class MonitorWorkingCopy extends Monitor implements IMonitorWorkingCopy {
	protected Monitor monitor;
	
	// creation
	public MonitorWorkingCopy() {
		// do nothing
	}

	// working copy
	public MonitorWorkingCopy(Monitor monitor) {
		this.monitor = monitor;
		setInternal(monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.IMonitorWorkingCopy#getOriginal()
	 */
	public IMonitor getOriginal() {
		return monitor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.IMonitorWorkingCopy#setRemoteHost(java.lang.String)
	 */
	public void setId(String newId) {
		id = newId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.IMonitorWorkingCopy#setRemoteHost(java.lang.String)
	 */
	public void setRemoteHost(String host) {
		remoteHost = host;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.IMonitorWorkingCopy#setRemotePort(int)
	 */
	public void setRemotePort(int port) {
		remotePort = port;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.IMonitorWorkingCopy#setLocalPort(int)
	 */
	public void setLocalPort(int port) {
		localPort = port;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.internal.IMonitorWorkingCopy#setProtocolAdapter(IProtocolAdapter)
	 */
	public void setProtocol(String protocolId2) {
		protocolId = protocolId2;
	}
	
	public boolean isWorkingCopy() {
		return true;
	}
	
	public IMonitorWorkingCopy createWorkingCopy() {
		return this;
	}

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