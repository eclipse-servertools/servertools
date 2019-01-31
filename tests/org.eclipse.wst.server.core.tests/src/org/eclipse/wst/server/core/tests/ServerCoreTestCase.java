/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

public class ServerCoreTestCase extends TestCase {
	public void testGetRuntimesExtension() throws Exception {
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			for (IRuntime runtime : runtimes)
				System.out.println(runtime.getId() + " - " + runtime.getName());
		}
	}
	
	public void testGetServersExtension() throws Exception {
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			for (IServer server : servers)
				System.out.println(server.getId() + " - " + server.getName());
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