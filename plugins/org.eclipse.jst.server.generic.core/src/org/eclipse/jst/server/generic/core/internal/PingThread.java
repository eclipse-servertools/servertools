/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.ServerType;
import org.eclipse.wst.server.core.util.SocketUtil;

/**
 * Thread used to ping server to test when it is started.
 * 
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
	private GenericServerBehaviour genericServer;

	/**
	 * Create a new PingThread.
	 * 
	 * @param server
	 * @param url
	 * @param maxPings
	 * @param genericServer
	 */
	public PingThread(IServer server, String url, GenericServerBehaviour genericServer) {
		super();
		this.server = server;
		this.url = url;
		this.maxPings = guessMaxPings(genericServer);
		this.genericServer = genericServer;
		Thread t = new Thread() {
			public void run() {
				ping();
			}
		};
		t.setDaemon(true);
		t.start();
	}
    
	private int guessMaxPings(GenericServerBehaviour server)
    {
    	int maxpings=60;
    	int startTimeout = ((ServerType)server.getServer().getServerType()).getStartTimeout();
    	if(startTimeout>0)
    		maxpings=startTimeout/PING_INTERVAL;
    	return maxpings;
    }
	private boolean isRemote(){
		return (server.getServerType().supportsRemoteHosts()&& !SocketUtil.isLocalhost(server.getHost()) );
	}
	/**
	 * Ping the server until it is started. Then set the server
	 * state to STATE_STARTED.
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
				if (count == maxPings && !isRemote()) {
					try {
						server.stop(false);
					} catch (Exception e) {
						Trace.trace(Trace.FINEST, "Ping: could not stop server");
					}
					stop = true;
					break;
				}
				if(!isRemote())
					count++;
				
				Trace.trace(Trace.FINEST, "Ping: pinging");
				URL pingUrl = new URL(url);
				URLConnection conn = pingUrl.openConnection();
				((HttpURLConnection)conn).getResponseCode();
	
				// ping worked - server is up
				if (!stop) {
					Trace.trace(Trace.FINEST, "Ping: success");
					Thread.sleep(200);
					genericServer.setServerStarted();
				}
				if(!isRemote())
					stop = true;
			} catch (FileNotFoundException fe) {
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					// ignore
				}
				genericServer.setServerStarted();
				if(!isRemote())
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
