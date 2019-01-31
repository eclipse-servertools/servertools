/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.net.Socket;
/**
 * 
 */
public class Connection {
	protected Socket in;
	protected Socket out;
	
	/**
	 * Creates a new connection.
	 * 
	 * @param in inbound socket
	 * @param out outbound socket
	 */
	public Connection(Socket in, Socket out) {
		this.in = in;
		this.out = out;
	}

	/**
	 * Close the connection.
	 */
	public void close() {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Closing connection");
		}
		try {
			in.getOutputStream().flush();
			in.shutdownInput();
			in.shutdownOutput();
			
			out.getOutputStream().flush();
			out.shutdownInput();
			out.shutdownOutput();
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Connection closed");
			}
		} catch (Exception ex) {
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Error closing connection " + this, ex);
			}
		}
	}
}
