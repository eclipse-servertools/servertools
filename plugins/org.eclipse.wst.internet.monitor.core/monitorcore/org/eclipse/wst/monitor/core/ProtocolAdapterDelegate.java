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
package org.eclipse.wst.monitor.core;

import java.io.IOException;
import java.net.Socket;
/**
 * A protocol adapter allows the monitor to support a new protocol between a client
 * and server, and manages the message passing between the two.
 * 
 * @since 1.0
 */
public abstract class ProtocolAdapterDelegate {
	/**
	 * [issue: why is this method called "parse"? Parse sounds more like parsing a particular
	 *         message.  Can we call something like "adapt", "translate" or "connect"? ]
	 * Attaches the protocol adapter to the given monitor using the input and output socket.
	 * The adapter is responsible for:
	 *    * opening the input & output streams to pass information from the input socket
	 *      (the client) to the output socket (server).
	 *      [issue: the in/out streams are opened here but I don't see any API to be used for 
	 *              closing the opened steam. Are the opened streams supposed to be cleaned up? ]
	 *    * passing information from the output socket (server) back to the client.
	 *    * creating and populating new org.eclipse.wst.monitor.core.IRequest objects as necessary.
	 * 
	 * @param monitor the monitor that uses this protocol adapter
	 * @param in the input socket of the monitor client
	 * @param out the output socket of the monitor server
	 * @throws IOException if an exception occur when opening the streams of the input or 
	 *         output sockets.   
	 */
	public abstract void parse(IMonitor monitor, Socket in, Socket out) throws IOException;
}