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
package org.eclipse.wst.internet.monitor.core.internal.http;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.wst.internet.monitor.core.IMonitor;
import org.eclipse.wst.internet.monitor.core.internal.Connection;
import org.eclipse.wst.internet.monitor.core.internal.ProtocolAdapterDelegate;
/**
 * 
 */
public class HTTPProtocolAdapter extends ProtocolAdapterDelegate {
	public void connect(IMonitor monitor, Socket in, Socket out) throws IOException {
		Connection conn2 = new Connection(in, out);
		HTTPConnection conn = new HTTPConnection(monitor);
		HTTPThread request = new HTTPThread(conn2, in.getInputStream(), out.getOutputStream(), conn, true, monitor.getRemoteHost(), monitor.getRemotePort());
		HTTPThread response = new HTTPThread(conn2, out.getInputStream(), in.getOutputStream(), conn, false, "localhost", monitor.getLocalPort(), request);
		request.start();
		response.start();
	}
}