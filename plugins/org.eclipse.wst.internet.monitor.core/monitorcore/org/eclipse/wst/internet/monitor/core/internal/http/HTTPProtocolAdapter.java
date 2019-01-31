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
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.internet.monitor.core.internal.Connection;
import org.eclipse.wst.internet.monitor.core.internal.ProtocolAdapterDelegate;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
/**
 * 
 */
public class HTTPProtocolAdapter extends ProtocolAdapterDelegate {
	protected Map<IMonitor, Connection> map = new HashMap<IMonitor, Connection>();

	/**
	 * @see ProtocolAdapterDelegate#connect(IMonitor, Socket, Socket)
	 */
	public void connect(IMonitor monitor, Socket in, Socket out) throws IOException {
		Connection conn2 = new Connection(in, out);
		map.put(monitor, conn2);
		HTTPConnection conn = new HTTPConnection(monitor);
		HTTPThread request = new HTTPThread(conn2, in.getInputStream(), out.getOutputStream(), conn, true, monitor.getRemoteHost(), monitor.getRemotePort());
		HTTPThread response = new HTTPThread(conn2, out.getInputStream(), in.getOutputStream(), conn, false, "localhost", monitor.getLocalPort(), request);
		request.start();
		response.start();
	}

	/**
	 * @see ProtocolAdapterDelegate#disconnect(IMonitor)
	 */
	public void disconnect(IMonitor monitor) throws IOException {
		try {
			Connection conn = map.get(monitor);
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			// ignore
		}
	}
}
