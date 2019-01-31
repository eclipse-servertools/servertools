/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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
package org.eclipse.wst.server.http.core.internal;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.wst.server.core.IServer;

/**
 * Thread used to ping server to test when it is started.
 */
public class PingThread {
	// delay before pinging starts
	private static final int PING_DELAY = 2000;

	// delay between pings
	private static final int PING_INTERVAL = 250;

	// maximum number of pings before giving up
	private int maxPings = 56; // total: 16 seconds + connection time

	private boolean stop = false;

	private String url;

	private IServer server;

	private HttpServerBehaviour behaviour;

	/**
	 * Create a new PingThread.
	 * 
	 * @param server
	 * @param url
	 * @param maxPings
	 * @param behaviour
	 */
	public PingThread(IServer server, String url, int maxPings,
			HttpServerBehaviour behaviour) {
		super();
		this.server = server;
		this.url = url;
		this.maxPings = maxPings;
		this.behaviour = behaviour;
		Thread t = new Thread() {
			public void run() {
				ping();
			}
		};
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Ping the server until it is started. Then set the server state to
	 * STATE_STARTED.
	 */
	protected void ping() {
		int count = 0;
		try {
			Thread.sleep(PING_DELAY);
		} catch (Exception e) {
			// ignore
		}
		while (!stop) {
			try {
				if (count == maxPings) {
					try {
						server.stop(false);
					} catch (Exception e) {
						Trace.trace(Trace.FINEST, "Ping: could not stop server");
					}
					stop = true;
					break;
				}
				count++;

				Trace.trace(Trace.FINEST, "Ping: pinging");
				URL pingUrl = new URL(url);
				URLConnection conn = pingUrl.openConnection();
				((HttpURLConnection) conn).getResponseCode();

				// ping worked - server is up
				if (!stop) {
					Trace.trace(Trace.FINEST, "Ping: success");
					Thread.sleep(200);
					behaviour.setServerStarted();
				}
				stop = true;
			} catch (FileNotFoundException fe) {
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					// ignore
				}
				behaviour.setServerStarted();
				stop = true;
			} catch (Exception e) {
				Trace.trace(Trace.FINEST, "Ping: failed");
				// pinging failed
				if (!stop) {
					try {
						Thread.sleep(PING_INTERVAL);
					} catch (InterruptedException e2) {
						// ignore
					}
				}
			}
		}
	}

	/**
	 * Tell the pinging to stop.
	 */
	public void stop() {
		Trace.trace(Trace.FINEST, "Ping: stopping");
		stop = true;
	}
}