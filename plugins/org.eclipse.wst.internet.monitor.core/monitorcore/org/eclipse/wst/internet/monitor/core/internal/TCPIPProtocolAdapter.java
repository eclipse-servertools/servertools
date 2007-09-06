/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
/**
 * 
 */
public class TCPIPProtocolAdapter extends ProtocolAdapterDelegate {
	protected Map<IMonitor, Connection> map = new HashMap<IMonitor, Connection>();
	
	/**
	 * @see ProtocolAdapterDelegate#connect(IMonitor, Socket, Socket)
	 */
	public void connect(IMonitor monitor, Socket in, Socket out) throws IOException {
		Request request = new Request((Monitor) monitor, IProtocolAdapter.TCPIP_PROTOCOL_ID, monitor.getLocalPort(), monitor.getRemoteHost(), monitor.getRemotePort());
		Connection conn = new Connection(in, out);
		map.put(monitor, conn);
		TCPIPThread requestThread = new TCPIPThread(conn, request, in.getInputStream(), out.getOutputStream(), true);
		requestThread.start();
		new TCPIPThread(conn, request, out.getInputStream(), in.getOutputStream(), false).start();
	}

	/**
	 * @see ProtocolAdapterDelegate#disconnect(IMonitor)
	 */
	public void disconnect(IMonitor monitor) throws IOException {
		try {
			Connection conn = map.get(monitor);
			conn.close();
		} catch (Exception e) {
			// ignore
		}
	}
}
