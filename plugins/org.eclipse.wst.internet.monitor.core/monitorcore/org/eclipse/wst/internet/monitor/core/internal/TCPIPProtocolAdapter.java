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
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.wst.internet.monitor.core.IMonitor;
/**
 * 
 */
public class TCPIPProtocolAdapter extends ProtocolAdapterDelegate {
	public void connect(IMonitor monitor, Socket in, Socket out) throws IOException {
		Request request = new Request((Monitor) monitor, IProtocolAdapter.TCPIP_PROTOCOL_ID, monitor.getLocalPort(), monitor.getRemoteHost(), monitor.getRemotePort());
		Connection conn = new Connection(in, out);
		DefaultThread requestThread = new DefaultThread(conn, request, in.getInputStream(), out.getOutputStream(), true);
		requestThread.start();
		new DefaultThread(conn, request, out.getInputStream(), in.getOutputStream(), false).start();
	}
}