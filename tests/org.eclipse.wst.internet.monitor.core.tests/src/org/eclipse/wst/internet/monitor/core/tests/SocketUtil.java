/*******************************************************************************
 * Copyright (c) 2003, 2009 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.core.tests;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Random;
/**
 * A utility class for socket-related function.
 */
public class SocketUtil {
	private static final Random rand = new Random(System.currentTimeMillis());

	/**
	 * Static utility class - cannot create an instance.
	 */
	private SocketUtil() {
		// cannot create
	}

	/**
	 * Finds an unused local port between the given from and to values.
	 * 
	 * @param low lowest possible port number
	 * @param high highest possible port number
	 * @return an unused port number, or <code>-1</code> if no used ports could be found
	 * @since 1.1
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
	 * Return a random local port number in the given range.
	 * 
	 * @param low lowest possible port number
	 * @param high highest possible port number
	 * @return a random port number in the given range
	 */
	private static int getRandomPort(int low, int high) {
		return rand.nextInt(high - low) + low;
	}

	/**
	 * Checks to see if the given local port number is being used. 
	 * Returns <code>true</code> if the given port is in use, and <code>false</code>
	 * otherwise. Retries every 500ms for "count" tries.
	 *
	 * @param port the port number to check
	 * @param count the number of times to retry
	 * @return boolean <code>true</code> if the port is in use, and
	 *    <code>false</code> otherwise
	 * @since 1.1
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
	 * Checks to see if the given local port number is being used.
	 * Returns <code>true</code> if the given port is in use, and <code>false</code>
	 * otherwise.
	 * 
	 * @param port the port number to check
	 * @return boolean <code>true</code> if the port is in use, and
	 *    <code>false</code> otherwise
	 * @since 1.1
	 */
	public static boolean isPortInUse(int port) {
		ServerSocket s = null;
		try {
			s = new ServerSocket(port, 0);
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
}