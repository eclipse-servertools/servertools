/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.InputStream;
import java.net.Socket;

import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;

/**
 * Thread used if the connection to the server fails.
 */
public class FailedConnectionThread extends Thread {
	private static final int BUFFER = 2048;
	protected Monitor monitor;
	protected Socket socket;
	protected String error;

	public FailedConnectionThread(Monitor monitor, Socket socket, String error) {
		super();
		this.monitor = monitor;
		this.socket = socket;
		this.error = error;
	}

	public void run() {
		Request request = new Request(monitor, IProtocolAdapter.TCPIP_PROTOCOL_ID, monitor.getLocalPort(), monitor.getRemoteHost(), monitor.getRemotePort());
		String err = error;
		if (err == null)
			err = Messages.errorConnectToServer;
		request.addToResponse(err.getBytes());
		
		try {
			InputStream in = socket.getInputStream();
			byte[] b = new byte[BUFFER];
			while (in.available() > 0) {
				int n = in.read(b);
				byte[] c = new byte[n];
				System.arraycopy(b, 0, c, 0, n);
				request.addToRequest(c);
			}
		} catch (Exception e) {
			// ignore
		} finally {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			} catch (Exception ex) {
				// ignore
			}
		}
	}
}