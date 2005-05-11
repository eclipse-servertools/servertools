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

import java.io.InterruptedIOException;
import java.net.*;

import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
/**
 * The actual TCP/IP monitoring server. This is a thread that
 * listens on a port and relays a call to another server.
 */
public class AcceptThread {
	protected IMonitor monitor;

	protected boolean alive = true;
	protected ServerSocket serverSocket;
	
	protected Thread thread;
	
	class ServerThread extends Thread{
		/**
		 * ServerThread accepts incoming connections and delegates to the protocol
		 * adapter to deal with the connection.
		 */
		public void run() {
			// create a new server socket
			try {
				serverSocket = new ServerSocket(monitor.getLocalPort());
				serverSocket.setSoTimeout(2000);
				Trace.trace(Trace.FINEST, "Monitoring localhost:" + monitor.getLocalPort() + " -> " + monitor.getRemoteHost()
						+ ":" + monitor.getRemotePort());
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not start monitoring");
				return;
			}

			while (alive) {
				try {
					// accept the connection from the client
					Socket localSocket = serverSocket.accept();
					
					// connect to the remote server
					Socket remoteSocket = new Socket(monitor.getRemoteHost(), monitor.getRemotePort());

					// relay the call through
					String protocolId = monitor.getProtocol();
					ProtocolAdapter adapter = MonitorPlugin.getInstance().getProtocolAdapter(protocolId);
					adapter.connect(monitor, localSocket, remoteSocket);
				} catch (InterruptedIOException e) {
					// do nothing
				} catch (Exception e) {
					if (alive)
						Trace.trace(Trace.SEVERE, "Error while monitoring", e);
				}
			}
		}
	}

	/**
	 * AcceptThread constructor.
	 * 
	 * @param monitor a monitor
	 */
	public AcceptThread(IMonitor monitor) {
		super();
		this.monitor = monitor;
	}
	
	/**
	 * Start the server.
	 */
	public void startServer() {
		if (thread != null)
			return;
		thread = new ServerThread();
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Returns <code>true</code> if the server is running.
	 * 
	 * @return <code>true</code> if the server is running, and <code>false</code>
	 *    otherwise
	 */
	public boolean isRunning() {
		return (thread != null);
	}

	/**
	 * Correctly close the server socket and shut down the server.
	 */
	public void stopServer() {
		try {
			alive = false;
			thread = null;
			
			String protocolId = monitor.getProtocol();
		   ProtocolAdapter adapter = MonitorPlugin.getInstance().getProtocolAdapter(protocolId);
			adapter.disconnect(monitor);
			serverSocket.close();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error stopping server", e);
		}
	}
	
	/**
	 * Returns true if this port is in use.
	 *
	 * @return boolean
	 * @param port int
	 */
	public static boolean isPortInUse(int port) {
		ServerSocket s = null;
		try {
			s = new ServerSocket(port);
		} catch (SocketException e) {
			return true;
		} catch (Exception e) {
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return false;
	}
}