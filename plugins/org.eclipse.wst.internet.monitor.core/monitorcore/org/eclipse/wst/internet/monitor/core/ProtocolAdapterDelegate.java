/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core;

import java.io.IOException;
import java.net.Socket;
/**
 * A protocol adapter allows the monitor to support a new protocol between a client
 * and server, and manages the message passing between the two.
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>protocolAdapters</code> extension point.
 * </p>
 * 
 * @since 1.0
 */
public abstract class ProtocolAdapterDelegate {
	/**
	 * Attaches the protocol adapter to the given monitor using the input and output socket.
	 * The adapter is responsible for:
	 *    * opening the input & output streams to pass information from the input socket
	 *      (the client) to the output socket (server).
	 *    * passing information from the output socket (server) back to the client.
	 *    * creating and populating new org.eclipse.wst.internet.monitor.core.IRequest objects as necessary.
	 *    * closing/cleanup on the input and output sockets.
	 * 
	 * @param monitor the monitor that uses this protocol adapter
	 * @param in the input socket of the monitor client
	 * @param out the output socket of the monitor server
	 * @throws IOException if an exception occur when opening the streams of the input or 
	 *         output sockets.   
	 */
	public abstract void connect(IMonitor monitor, Socket in, Socket out) throws IOException;
}