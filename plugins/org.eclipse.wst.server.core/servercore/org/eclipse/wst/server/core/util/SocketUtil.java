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
package org.eclipse.wst.server.core.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.eclipse.wst.server.core.internal.Trace;
/**
 * A utility class for socket-related function. It's main purposes are to find
 * unused ports, check whether a port is in use, and check whether a given
 * address is a local(host) address.
 * 
 * @since 1.0
 */
public class SocketUtil {
	private static final Random rand = new Random(System.currentTimeMillis());

	private static List localHostCache;

	private static List addressCache;

	private static Object lock = new Object();

	/**
	 * Static utility class - cannot create an instance.
	 */
	private SocketUtil() {
		// cannot create
	}

	/**
	 * Finds an unused port between the given from and to values.
	 * 
	 * @param low lowest possible port number
	 * @param high highest possible port number
	 * @return an usused port number, or <code>-1</code> if no used ports could be found
	 */
	public static int findUnusedPort(int low, int high) {
		if (high < low)
			return -1;
		
		for (int i = 0; i < 10; i++) {
			int port = getRandomPort(low, high);
			if (!isPortInUse(port))
				return port;
		}
		return -1;
	}

	/**
	 * Return a random port number in the given range.
	 * 
	 * @param low lowest possible port number
	 * @param high highest possible port number
	 * @return a random port number in the given range
	 */
	private static int getRandomPort(int low, int high) {
		return rand.nextInt(high - low) + low;
	}

	/**
	 * Checks to see if the given port number is being used. 
	 * Returns <code>true</code> if the given port is in use, and <code>false</code>
	 * otherwise. Retries every 500ms for "count" tries.
	 *
	 * @param port the port number to check
	 * @param count the number of times to retry
	 * @return boolean <code>true</code> if the port is in use, and
	 *    <code>false</code> otherwise
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
	 * Checks to see if the given port number is being used.
	 * Returns <code>true</code> if the given port is in use, and <code>false</code>
	 * otherwise.
	 *
	 * @param port the port number to check
	 * @return boolean <code>true</code> if the port is in use, and
	 *    <code>false</code> otherwise
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

	/**
	 * Checks if the given host (name, fully qualified name, or IP address) is
	 * referring to the local machine.
	 * <p>
	 * The first time this method is called (or the first call after each time
	 * the network configuration has changed, e.g. by the user switching from a
	 * wired connection to wireless) a background process is used to cache the
	 * network information. On most machines the network information will be found
	 * quickly and the results of this call will be returned immediately.
	 * </p><p>
	 * On machines where the network configuration of the machine is bad or the
	 * network has problems, this first method call will take at most 250ms, but
	 * the results may be incorrect (incomplete).
	 * </p><p>
	 * All subsequent calls (until the network configuration changes) will
	 * return very quickly. If the background process is still running it will
	 * continue to fill the cache and each subsequent call to this method may be
	 * more correct.
	 * </p>
	 * 
	 * @param host a hostname or IP address
	 * @return <code>true</code> if the given host is localhost, and
	 *    <code>false</code> otherwise
	 */
	public static boolean isLocalhost(String host) {
		if (host == null)
			return false;
		
		if ("localhost".equals(host) || "127.0.0.1".equals(host))
			return true;
		
		// check if cache is ok
		try {
			// get network interfaces
			final List currentAddresses = new ArrayList();
			currentAddresses.add(InetAddress.getLocalHost());
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			while (nis.hasMoreElements()) {
				NetworkInterface inter = (NetworkInterface) nis.nextElement();
				Enumeration ias = inter.getInetAddresses();
				while (ias.hasMoreElements())
					currentAddresses.add(ias.nextElement());
			}
			
			// check if cache is empty or old and refill it if necessary
			if (addressCache == null || !addressCache.containsAll(currentAddresses) || !currentAddresses.containsAll(addressCache)) {
				addressCache = currentAddresses;
				final List addressList = new ArrayList(currentAddresses.size() * 3);
				Iterator iter = currentAddresses.iterator();
				while (iter.hasNext()) {
					InetAddress addr = (InetAddress) iter.next();
					String a = addr.getHostAddress();
					if (a != null && !addressList.contains(a))
						addressList.add(a);
				}
				synchronized (lock) {
					localHostCache = addressList;
				}
				
				Thread cacheThread = new Thread("Caching localhost information") {
					public void run() {
						Iterator iter = currentAddresses.iterator();
						while (iter.hasNext()) {
							InetAddress addr = (InetAddress) iter.next();
							String host = addr.getHostName();
							String host2 = addr.getCanonicalHostName();
							synchronized (lock) {
								if (host != null && !addressList.contains(host))
									addressList.add(host);
								if (host2 != null && !addressList.contains(host2))
									addressList.add(host2);
							}
						}
					}
				};
				cacheThread.setDaemon(true);
				cacheThread.setPriority(Thread.NORM_PRIORITY - 1);
				cacheThread.start();
				cacheThread.join(250);
			}
		} catch (Exception e) {
			// ignore
			Trace.trace(Trace.WARNING, "Localhost caching failure", e);
		}
		
		if (localHostCache == null)
			return false;
		
		synchronized (lock) {
			Iterator iterator = localHostCache.iterator();
			while (iterator.hasNext()) {
				if (host.equals(iterator.next()))
					return true;
			}
		}
		
		return false;
	}
}