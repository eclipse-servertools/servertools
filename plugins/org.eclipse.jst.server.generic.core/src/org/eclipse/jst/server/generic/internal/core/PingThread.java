package org.eclipse.jst.server.generic.internal.core;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.debug.core.ILaunchManager;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerState;
/**
 * Thread used to ping server to test when it is started.
 */
public class PingThread extends Thread {
	// delay before pinging starts
	private static final int PING_DELAY = 2000;

	// delay between pings
	private static final int PING_INTERVAL = 250;

	// maximum number of pings before giving up
	private static final int MAX_PINGS = 56; // total: 16 seconds

	private boolean stop = false;
	private String mode;
	private String url = "";
	private GenericServer serverType;
	private IServerState control;

	public PingThread(GenericServer serverType, IServerState control, String url, String mode) {	super();
		this.control = control;
		this.url = url;
		this.mode = mode;
		this.serverType = serverType;
		setDaemon(true);
	}
	
	/**
	 * Ping the server until it is started. Then set the server
	 * state to SERVER_STARTED.
	 */
	public void run() {
		int count = 0;
		try {
			sleep(PING_DELAY);
		} catch (Exception e) { }
		while (!stop) {
			try {
				if (count == MAX_PINGS) {
					serverType.stop();
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
					if (ILaunchManager.DEBUG_MODE.equals(mode))
						control.setServerState(IServer.SERVER_STARTED_DEBUG);
					else if (ILaunchManager.PROFILE_MODE.equals(mode))
						control.setServerState(IServer.SERVER_STARTED_PROFILE);
					else
						control.setServerState(IServer.SERVER_STARTED);
				}
				stop = true;
			} catch (FileNotFoundException fe) {
				try { Thread.sleep(200); } catch (Exception e) { }
				if (ILaunchManager.DEBUG_MODE.equals(mode))
					control.setServerState(IServer.SERVER_STARTED_DEBUG);
				else if (ILaunchManager.PROFILE_MODE.equals(mode))
					control.setServerState(IServer.SERVER_STARTED_PROFILE);
				else
					control.setServerState(IServer.SERVER_STARTED);
				stop = true;
			} catch (Exception e) {
				Trace.trace(Trace.FINEST, "Ping: failed");
				// pinging failed
				if (!stop) {
					try {
						sleep(PING_INTERVAL);
					} catch (InterruptedException e2) { }
				}
			}
		}
	}
	
	/**
	 * Tell the pinging to stop.
	 */
	public void stopPinging() {
		//Trace.trace("Ping: stopping");
		stop = true;
	}
}