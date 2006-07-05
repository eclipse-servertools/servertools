/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitorListener;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitorWorkingCopy;
import org.eclipse.wst.internet.monitor.core.internal.provisional.MonitorCore;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.internal.ServerMonitorDelegate;
import org.eclipse.wst.server.core.util.SocketUtil;
/**
 * 
 */
public class DefaultMonitorDelegate extends ServerMonitorDelegate {
	protected Map monitors = new HashMap();
	protected IMonitorListener listener;

	private void addListener() {
		if (listener != null)
			return;
		
		listener = new IMonitorListener() {
			public void monitorAdded(IMonitor monitor) {
				// ignore
			}

			public void monitorChanged(IMonitor monitor) {
				// ignore
			}

			public void monitorRemoved(IMonitor monitor) {
				if (monitor == null)
					return;
				
				Object monKey = null;
				Iterator iterator = monitors.keySet().iterator();
				while (iterator.hasNext()) {
					Object key = iterator.next();
					Object value = monitors.get(key);
					if (monitor.equals(value))
						monKey = key;
				}
				if (monKey != null)
					monitors.remove(monKey);
				if (monitors.isEmpty()) {
					MonitorCore.removeMonitorListener(listener);
					listener = null;
				}
			}
		};
		MonitorCore.addMonitorListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerMonitorDelegate#startMonitoring(org.eclipse.wst.server.core.ServerPort)
	 */
	public int startMonitoring(IServer server, ServerPort port, int monitorPort) throws CoreException {
		try {
			IMonitor monitor = (IMonitor) monitors.get(port);
			int mport = -1;
			if (monitor == null) {
				mport = monitorPort;
				if (mport == -1)
					mport = SocketUtil.findUnusedPort(5000, 15000);
				
				// should search for a monitor first ..
				IMonitorWorkingCopy wc = MonitorCore.createMonitor();
				wc.setLocalPort(mport);
				wc.setRemoteHost(server.getHost());
				wc.setRemotePort(port.getPort());
				if ("HTTP".equals(port.getProtocol()))
					wc.setProtocol("HTTP");
				monitor = wc.save();
				addListener();
			} else
				mport = monitor.getLocalPort();
			monitor.start();
			monitors.put(port, monitor);
			return mport;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not start monitoring", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorStartingMonitor, e.getLocalizedMessage()), null));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerMonitorDelegate#stopMonitoring(org.eclipse.wst.server.core.ServerPort)
	 */
	public void stopMonitoring(IServer server, ServerPort port) {
		try {
			IMonitor monitor = (IMonitor) monitors.get(port);
			if (monitor != null)
				monitor.stop();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not stop monitoring", e);
		}
	}
}