package org.eclipse.wst.server.ui.internal.view.servers;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.util.Iterator;

import org.eclipse.jface.action.Action;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IServerPort;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Shell;

/**
 * Monitor a server port.
 */
public class MonitorServerPortAction extends Action {
	protected Shell shell;
	protected IServer server;
	protected IServerPort port;
	protected IMonitoredServerPort monitoredPort;
	protected boolean checked;
	
	public MonitorServerPortAction(Shell shell, IServer server, IServerPort port) {
		super(ServerUIPlugin.getResource("%actionMonitorPort", new String[] { port.getPort() + "", port.getName() }));
		
		this.shell = shell;
		this.server = server;
		this.port = port;
		
		Iterator iterator = ServerCore.getServerMonitorManager().getMonitoredPorts(server).iterator();
		while (iterator.hasNext()) {
			IMonitoredServerPort msp = (IMonitoredServerPort) iterator.next();
			if (port.equals(msp.getServerPort()) && msp.isStarted() &&
					(msp.getContentTypes() == null || (port.getContentTypes() != null && msp.getContentTypes().length == port.getContentTypes().length)))
				monitoredPort = msp;
		}

		checked = monitoredPort != null && monitoredPort.isStarted();
		setChecked(checked);
	}

	/**
	 * Enable or disable monitoring.
	 */
	public void run() {
		if (checked) {
			ServerCore.getServerMonitorManager().removeMonitor(monitoredPort);
		} else {
			IServerMonitorManager smm = ServerCore.getServerMonitorManager();
			if (monitoredPort == null)
				monitoredPort = smm.createMonitor(server, port, -1, null);
			
			try {
				smm.startMonitor(monitoredPort);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not monitor", e);
			}
		}
	}
}