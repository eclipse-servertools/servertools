/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

	protected static final Object lock = new Object();

	private static Set<String> localHostCache;
	private static Set<String> notLocalHostCache = new HashSet<String>();
	private static Map<String, CacheThread> threadMap = new HashMap<String, CacheThread>();

	private static Set<InetAddress> addressCache;

	static class CacheThread extends Thread {
		private Set<InetAddress> currentAddresses;
		private Set<String> addressList;
		private String host;
		private Set<String> nonAddressList;
		private Map threadMap2;

		public CacheThread(String host, Set<InetAddress> currentAddresses, Set<String> addressList, Set<String> nonAddressList, Map threadMap2) {
			super("Caching localhost information");
			this.host = host;
			this.currentAddresses = currentAddresses;
			this.addressList = addressList;
			this.nonAddressList = nonAddressList;
			this.threadMap2 = threadMap2;
		}

		public void run() {
			if (currentAddresses != null) {
				Iterator iter2 = currentAddresses.iterator();
				while (iter2.hasNext()) {
					InetAddress addr = (InetAddress) iter2.next();
					String hostname = addr.getHostName();
					String hostname2 = addr.getCanonicalHostName();
					synchronized (lock) {
						if (hostname != null && !addressList.contains(hostname))
							addressList.add(hostname);
						if (hostname2 != null && !addressList.contains(hostname2))
							addressList.add(hostname2);
					}
				}
			}
			
			try {
				InetAddress[] addrs = InetAddress.getAllByName(host);
				int length = addrs.length;
				for (int j = 0; j < length; j++) {
					InetAddress addr = addrs[0];
					String hostname = addr.getHostName();
					String hostname2 = addr.getCanonicalHostName();
					synchronized (lock) {
						if (addr.isLoopbackAddress()) {
							if (hostname != null && !addressList.contains(hostname))
								addressList.add(hostname);
							if (hostname2 != null && !addressList.contains(hostname2))
								addressList.add(hostname2);
						} else {
							if (hostname != null && !nonAddressList.contains(hostname))
								nonAddressList.add(hostname);
							if (hostname2 != null && !nonAddressList.contains(hostname2))
								nonAddressList.add(hostname2);
						}
					}
				}
			} catch (UnknownHostException e) {
				synchronized (lock) {
					if (host != null && !nonAddressList.contains(host))
						nonAddressList.add(host);
				}
			}
			synchronized (lock) {
				threadMap2.remove(host);
			}
		}
	}

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
	 * @return an unused port number, or <code>-1</code> if no used ports could be found
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
	public static boolean isLocalhost(final String host) {
		if (host == null || host.equals(""))
			return false;
		
		if ("localhost".equals(host) || "127.0.0.1".equals(host))
			return true;
		
		// check simple cases
		try {
			InetAddress localHostaddr = InetAddress.getLocalHost();
			if (localHostaddr.getHostName().equals(host)
					|| host.equals(localHostaddr.getCanonicalHostName())
					|| localHostaddr.getHostAddress().equals(host))
				return true;
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Localhost caching failure", e);
		}
		
		// check for current thread and wait if necessary
		boolean currentThread = false;
		try {
			Thread t = null;
			synchronized (lock) {
				t = threadMap.get(host);
			}
			if (t != null && t.isAlive()) {
				currentThread = true;
				t.join(30);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Localhost caching failure", e);
		}
		
		// check if cache is still ok
		boolean refreshedCache = false;
		try {
			// get network interfaces
			final Set<InetAddress> currentAddresses = new HashSet<InetAddress>();
			currentAddresses.add(InetAddress.getLocalHost());
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			while (nis.hasMoreElements()) {
				NetworkInterface inter = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = inter.getInetAddresses();
				while (ias.hasMoreElements())
					currentAddresses.add(ias.nextElement());
			}
			
			// check if cache is empty or old and refill it if necessary
			if (addressCache == null || !addressCache.containsAll(currentAddresses) || !currentAddresses.containsAll(addressCache)) {
				CacheThread cacheThread = null;
				refreshedCache = true;
				
				synchronized (lock) {
					addressCache = currentAddresses;
					notLocalHostCache = new HashSet<String>();
					localHostCache = new HashSet<String>(currentAddresses.size() * 3);
					
					Iterator iter = currentAddresses.iterator();
					while (iter.hasNext()) {
						InetAddress addr = (InetAddress) iter.next();
						String a = addr.getHostAddress();
						if (a != null && !localHostCache.contains(a))
							localHostCache.add(a);
					}
					
					cacheThread = new CacheThread(host, currentAddresses, localHostCache, notLocalHostCache, threadMap);
					threadMap.put(host, cacheThread);
					cacheThread.setDaemon(true);
					cacheThread.setPriority(Thread.NORM_PRIORITY - 1);
					cacheThread.start();
				}
				cacheThread.join(200);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Localhost caching failure", e);
		}
		
		synchronized (lock) {
			if (localHostCache.contains(host))
				return true;
			if (notLocalHostCache.contains(host))
				return false;
		}
		
		// if the cache hasn't been cleared, maybe we still need to lookup the host  
		if (!refreshedCache && !currentThread) {
			try {
				CacheThread cacheThread = null;
				synchronized (lock) {
					cacheThread = new CacheThread(host, null, localHostCache, notLocalHostCache, threadMap);
					threadMap.put(host, cacheThread);
					cacheThread.setDaemon(true);
					cacheThread.setPriority(Thread.NORM_PRIORITY - 1);
					cacheThread.start();
				}
				cacheThread.join(75);
				
				synchronized (lock) {
					if (localHostCache.contains(host))
						return true;
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Could not find localhost", e);
			}
		}
		
		return false;
	}
}