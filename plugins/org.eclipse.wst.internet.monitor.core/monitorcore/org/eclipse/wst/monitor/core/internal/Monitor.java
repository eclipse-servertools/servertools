/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.monitor.core.internal;

import org.eclipse.wst.monitor.core.*;
/**
 * 
 */
public class Monitor implements IMonitor {
	private static final String MEMENTO_ID = "id";
	private static final String MEMENTO_LOCAL_PORT = "local-port";
	private static final String MEMENTO_REMOTE_HOST = "remote-host";
	private static final String MEMENTO_REMOTE_PORT = "remote-port";
	private static final String MEMENTO_TYPE_ID = "type-id";

	protected String id;
	protected String remoteHost;
	protected int remotePort = 80;
	protected int localPort = 80;
	protected IProtocolAdapter type;
	
	public Monitor() {
		type = MonitorPlugin.getInstance().getDefaultType();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.monitor.internal.IMonitor#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.monitor.internal.IMonitor#getRemoteHost()
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.monitor.internal.IMonitor#getRemotePort()
	 */
	public int getRemotePort() {
		return remotePort;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.monitor.internal.IMonitor#getLocalPort()
	 */
	public int getLocalPort() {
		return localPort;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.monitor.internal.IMonitor#isHTTPEnabled()
	 */
	public IProtocolAdapter getProtocolAdapter() {
		return type;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.monitor.internal.IMonitor#isRunning()
	 */
	public boolean isRunning() {
		return MonitorManager.getInstance().isRunning(this);
	}
	
	public void delete() {
		MonitorManager.getInstance().removeMonitor(this);
	}

	public boolean isWorkingCopy() {
		return false;
	}
	
	public IMonitorWorkingCopy getWorkingCopy() {
		return new MonitorWorkingCopy(this);
	}
	
	protected void setInternal(IMonitor monitor) {
		id = monitor.getId();
		remoteHost = monitor.getRemoteHost();
		remotePort = monitor.getRemotePort();
		localPort = monitor.getLocalPort();
		type = monitor.getProtocolAdapter();
	}
	
	protected void save(IMemento memento) {
		memento.putString(MEMENTO_ID, id);
		memento.putString(MEMENTO_TYPE_ID, type.getId());
		memento.putInteger(MEMENTO_LOCAL_PORT, localPort);
		memento.putString(MEMENTO_REMOTE_HOST, remoteHost);
		memento.putInteger(MEMENTO_REMOTE_PORT, remotePort);
	}

	protected void load(IMemento memento) {
		id = memento.getString(MEMENTO_ID);
		type = MonitorPlugin.getInstance().getProtocolAdapter(memento.getString(MEMENTO_TYPE_ID));
		Integer temp = memento.getInteger(MEMENTO_LOCAL_PORT);
		if (temp != null)
			localPort = temp.intValue();
		remoteHost = memento.getString(MEMENTO_REMOTE_HOST);
		temp = memento.getInteger(MEMENTO_REMOTE_PORT);
		if (temp != null)
			remotePort = temp.intValue();
	}
}