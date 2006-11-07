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
package org.eclipse.wst.server.core.tests;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

public class ServerCoreTestCase extends TestCase {
	public void testGetRuntimesExtension() throws Exception {
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			int size = runtimes.length;
			for (int i = 0; i < size; i++)
				System.out.println(runtimes[i].getId() + " - " + runtimes[i].getName());
		}
	}
	
	public void testGetServersExtension() throws Exception {
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++)
				System.out.println(servers[i].getId() + " - " + servers[i].getName());
		}
	}
	
	public void testFindRuntimes0Extension() throws Exception {
		try {
			ServerCore.findRuntime(null);
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void testFindRuntimes1Extension() throws Exception {
		assertTrue(ServerCore.findRuntime("x") == null);
	}

	public void testFindServers0Extension() throws Exception {
		try {
			ServerCore.findServer(null);
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testFindServers1Extension() throws Exception {
		assertTrue(ServerCore.findServer("x") == null);
	}

	public void testAddRuntimeLifecycleListener() {
		ServerCore.addRuntimeLifecycleListener(null);
	}

	public void testRemoveRuntimeLifecycleListener() {
		ServerCore.removeRuntimeLifecycleListener(null);
	}

	public void testAddServerLifecycleListener() {
		ServerCore.addServerLifecycleListener(null);
	}

	public void testRemoveServerLifecycleListener() {
		ServerCore.removeServerLifecycleListener(null);
	}
}