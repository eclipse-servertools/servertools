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

import java.io.*;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
/**
 * Monitor server I/O thread.
 */
public class TCPIPThread extends Thread {
	private static final int BUFFER = 2048;
	protected InputStream in;
	protected OutputStream out;
	protected boolean isRequest;
	
	protected Connection conn;
	protected Request request;

	/**
	 * Create a new TCP/IP thread.
	 * 
	 * @param conn
	 * @param request
	 * @param in
	 * @param out
	 * @param isRequest
	 */
	public TCPIPThread(Connection conn, Request request, InputStream in, OutputStream out, boolean isRequest) {
		super();
		this.conn = conn;
		this.request = request;
		this.in = in;
		this.out = out;
		this.isRequest = isRequest;
		setPriority(Thread.NORM_PRIORITY + 1);
		setDaemon(true);
	}

	/**
	 * Listen for input, save it, and pass to the output stream.
	 */
	public void run() {
		try {
			byte[] b = new byte[BUFFER];
			int n = in.read(b);
			while (n > 0) {
				out.write(b, 0, n);
				if (b != null && n > 0) {
					byte[] x = null;
					if (n == BUFFER)
						x = b;
					else {
						x = new byte[n];
						System.arraycopy(b, 0, x, 0, n);
					}
					if (isRequest)
						request.addToRequest(x);
					else
						request.addToResponse(x);
				}
				n = in.read(b);
				Thread.yield();
			}
			out.flush();
		} catch (IOException e) {
			// ignore
		} finally {
			//request.fireChangedEvent();
			if (!isRequest)
				conn.close();
		}
	}
}