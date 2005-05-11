/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.IOException;
import java.net.Socket;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
/**
 * Abstract base class for protocol adapter delegates, which provide the
 * implementation behind a particular protocol adapter.
 * A protocol adapter watches the message traffic passing between client and
 * server; it parses the messages and reports them in the form of 
 * Request objects.
 * <p>
 * This abstract class is intended to be subclassed only by clients
 * to extend the <code>protocolAdapters</code> extension point.
 * The subclass must have a public 0-argument constructor, which will be used
 * automatically to instantiate the delegate when required. 
 * </p>
 * <p>
 * There is only one delegate created per protocol, and this delegate must
 * be able to handle multiple monitor instances. This means that the delegate
 * typically will not have instance state, or must synchronize and keep the
 * state separate.
 * </p>
 * <p>
 * [issue: The HTTP and TCP/IP delegate implementations create threads which
 * shuffle info between sockets. If the monitor is changed or deleted, how do
 * these threads go away? Methinks that delegates in general should be forced
 * to implement disconnect(IMonitor) as well. This method would be called by the
 * system when the monitor is changed or deleted, or when the plug-in is
 * being shut down.]
 * </p>
 * 
 * @since 1.0
 */
public abstract class ProtocolAdapterDelegate {
	/**
	 * Establishes an ongoing connection between client and server being
	 * monitored by the given monitor. The <code>in</code> socket is associated
	 * with the monitor's client. The <code>out</code> socket is associated with
	 * the monitor's server. Different adapaters handle different network
	 * protocols.
	 * <p>
	 * Subclasses must implement this method to achieve the following:
	 * <ul>
	 * <li>Client to server communication -
	 * Opening an input stream on the <code>in</code> socket, opening an output
	 * stream on the <code>out</code> socket, and establishing a mechanism that
	 * will pass along all bytes received on the input stream to the output
	 * stream.</li>
	 * <li>Server to client communication - Opening an input stream on the
	 * <code>out</code> socket, opening an output stream on the <code>in</code>
	 * socket, and establishing a mechanism that will pass along all bytes
	 * received on the input stream to the output stream.</li>
	 * <li>Parsing the protocol-specific message traffic to create and report
	 * request objects for each message passed between client and server.</li>
	 * <li>Closing the input and output sockets and otherwise cleaning up
	 * afterwards.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param monitor the monitor that uses this protocol adapter
	 * @param in the input socket of the monitor client
	 * @param out the output socket of the monitor server
	 * @throws IOException if an exception occur when opening the streams of the
	 *    input or output sockets
	 */
	public abstract void connect(IMonitor monitor, Socket in, Socket out) throws IOException;
	
	/**
	 * Called if the monitor is changed or deleted, or the plugin is shutting down.
	 * The delegate must clean up the connections and threads created to respond to
	 * this monitor. 
	 * 
	 * @param monitor
	 * @throws IOException
	 */
	public abstract void disconnect(IMonitor monitor) throws IOException;
}