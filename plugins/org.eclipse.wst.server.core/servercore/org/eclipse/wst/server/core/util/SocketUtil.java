/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Random;

import org.eclipse.wst.server.core.internal.Trace;

import sun.net.spi.nameservice.dns.DNSNameService;
/**
 * 
 */
public class SocketUtil {
	private static final Random fgRandom = new Random(System.currentTimeMillis());

	/**
	 * Finds an unused port between the given from and to values.
	 * 
	 * @param host
	 * @param searchFrom
	 * @param searchTo
	 * @return
	 */
	public static int findUnusedPort(int searchFrom, int searchTo) {
		for (int i = 0; i < 10; i++) {
			int port = getRandomPort(searchFrom, searchTo);
			if (!isPortInUse(port))
				return port;
		}
		return -1;
	}

	private static int getRandomPort(int low, int high) {
		return (int)(fgRandom.nextFloat()*(high-low))+low;
	}

	/**
	 * Returns true if this port is in use. Retries every 500ms for "count" tries.
	 *
	 * @return boolean
	 * @param port int
	 * @param count int
	 */
	public static boolean isPortInUse(int port, int count) {
		boolean inUse = isPortInUse(port);
		while (inUse && count > 0) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// ignore
			}
			inUse = isPortInUse(port);
			count --;
		}
	
		return inUse;
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
		} catch (IOException e) {
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
	
	private static String dnsHostname; 

	public static boolean isLocalhost(String host) {
		if (host == null)
			return false;
		try {
			if ("localhost".equals(host) || "127.0.0.1".equals(host))
				return true;
			
			InetAddress localHostaddr = InetAddress.getLocalHost();
			if (localHostaddr.getHostName().equals(host) || host.equals(localHostaddr.getCanonicalHostName()))
				return true;
			
			if (localHostaddr.getHostAddress().equals(host))
				return true;
			
			if (dnsHostname == null)
				try {
					DNSNameService dns = new DNSNameService();
					dnsHostname = dns.getHostByAddr(localHostaddr.getAddress());
				} catch (Throwable t) {
					dnsHostname = "*****************";
				}
			
			if (dnsHostname != null && dnsHostname.equals(host))
				return true;
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error checking for localhost", e);
		}
		return false;
	}
}