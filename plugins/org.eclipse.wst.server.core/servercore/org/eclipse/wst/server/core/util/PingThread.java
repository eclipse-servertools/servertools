/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.model.ServerDelegate;
/**
 * Thread used to ping server to test when it is started.
 */
public class PingThread {
	// delay before pinging starts
	private static final int PING_DELAY = 2000;

	// delay between pings
	private static final int PING_INTERVAL = 250;

	// maximum number of pings before giving up
	private static final int MAX_PINGS = 56; // total: 16 seconds

	private boolean stop = false;
	private String url;
	private ServerDelegate server;
	private IServer server2;

	/**
	 * Create a new PingThread.
	 * 
	 * @param server2
	 * @param server
	 * @param url
	 * @param mode
	 */
	public PingThread(IServer server2, ServerDelegate server, String url) {
		super();
		this.server = server;
		this.server2 = server2;
		this.url = url;
		Thread t = new Thread() {
			public void run() {
				run();
			}
		};
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Ping the server until it is started. Then set the server
	 * state to STATE_STARTED.
	 */
	protected void run() {
		int count = 0;
		try {
			Thread.sleep(PING_DELAY);
		} catch (Exception e) {
			// ignore
		}
		while (!stop) {
			try {
				if (count == MAX_PINGS) {
					server2.stop();
					stop = true;
					break;
				}
				Trace.trace(Trace.FINEST, "Ping: pinging");
				URL pingUrl = new URL(url);
				URLConnection conn = pingUrl.openConnection();
				((HttpURLConnection)conn).getResponseCode();
				count++;
	
				// ping worked - server is up
				if (!stop) {
					Trace.trace(Trace.FINEST, "Ping: success");
					Thread.sleep(200);
					server.setServerState(IServer.STATE_STARTED);
				}
				stop = true;
			} catch (FileNotFoundException fe) {
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					// ignore
				}
				server.setServerState(IServer.STATE_STARTED);
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
	public void stopPinging() {
		Trace.trace("Ping: stopping");
		stop = true;
	}
}