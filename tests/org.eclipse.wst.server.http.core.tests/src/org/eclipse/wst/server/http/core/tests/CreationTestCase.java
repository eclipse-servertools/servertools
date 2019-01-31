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
package org.eclipse.wst.server.http.core.tests;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.http.core.internal.HttpServer;

import junit.framework.Test;
import junit.framework.TestCase;

public class CreationTestCase extends TestCase {
	private static IServer server;

	public CreationTestCase() {
		super();
	}

	public static Test suite() {
		return new OrderedTestSuite(CreationTestCase.class);
	}

	public void test00CreateServer() throws Exception {
		// Find the v6 runtime that can be started (not stub).
		IRuntimeType runtimeType = ServerCore
				.findRuntimeType("com.ibm.etools.publishing.server.runtime");
		assertTrue("V6 runtime type is not found", runtimeType != null);

		// Find the V6 runtime location.
		// String userWasV6RuntimeLocation = System.getProperty("was.runtime.v6");
		// IPath runtimeLocation = userWasV6RuntimeLocation == null ?
		// WASRuntimeLocator.getRuntimeLocation(WASRuntimeLocator.BASE_V6) : new
		// Path(userWasV6RuntimeLocation);
		// assertTrue("Cannot find the runtime location", runtimeLocation !=
		// null);

		// Create the runtime
		// IRuntimeWorkingCopy curRuntimeWc = runtimeType.createRuntime(null);
		// ServerUtil.setRuntimeDefaultName(curRuntimeWc);
		// //curRuntimeWc.setLocation(runtimeLocation);
		// curRuntimeWc.save(null);

		// Get the created runtime.
		// IRuntime curRuntime = curRuntimeWc.getOriginal();
		// assertTrue("No created runtime is found.", curRuntime != null);
		//

		// Create the server.
		IServerType serverType = ServerCore.findServerType(HttpServer.ID);
		assertTrue(serverType != null);

		IServerWorkingCopy serverWc = null;
		serverWc = serverType.createServer("HTTP Server", null, new NullProgressMonitor());
		server = serverWc.save(true, null);
		assertNotNull(server);
	}

	public void test01DeleteServer() throws Exception {
		assertNotNull("Cannot delete server since no server is avaiable.", server);
		server.delete();
	}

	public void test02CreateServer() throws Exception {
		// Find the v6 runtime that can be started (not stub).
		IRuntimeType runtimeType = ServerCore
				.findRuntimeType("com.ibm.etools.publishing.static.server.runtime");
		assertTrue("V6 runtime type is not found", runtimeType != null);

		// Create the server.
		IServerType serverType = ServerCore
				.findServerType("com.ibm.etools.publishing.static.server");
		assertTrue("No Static server type has be defined.", serverType != null);

		IServerWorkingCopy serverWc = null;
		serverWc = serverType
				.createServer("Static Server", null, new NullProgressMonitor());
		server = serverWc.save(true, null);

		assertNotNull("The created server cannot be found.", server);
	}

	public void test03DeleteServer() throws Exception {
		assertNotNull("Cannot delete server since no server is avaiable.", server);
		server.delete();
	}
}