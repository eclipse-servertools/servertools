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
package org.eclipse.wst.server.tests.performance.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.test.performance.PerformanceTestCase;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;

public abstract class ServerPerformanceTestCase extends PerformanceTestCase {
	protected void closeIntro() {
		IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
		IIntroPart introPart = introManager.getIntro();
		if (introPart != null)
			introManager.closeIntro(introPart);
	}

	protected IRuntimeWorkingCopy createRuntime(String runtimeTypeId, String runtimeTypeLocation) throws CoreException {
		if (runtimeTypeId == null)
			throw new IllegalArgumentException();
		IRuntimeWorkingCopy runtimeCopy = ServerCore.findRuntimeType(runtimeTypeId).createRuntime(runtimeTypeId, null);
		runtimeCopy.setLocation(new Path(runtimeTypeLocation));
		runtimeCopy.setReadOnly(false);
		runtimeCopy.save(false, null);
		return runtimeCopy;
	}

	protected IServer createServer(String serverTypeId) throws CoreException {
		if (serverTypeId == null)
			throw new IllegalArgumentException();
		NullProgressMonitor monitor = new NullProgressMonitor();
		IServerType serverType = ServerCore.findServerType(serverTypeId);
		IServerWorkingCopy serverCopy = serverType.createServer(serverTypeId, null, monitor);
		return serverCopy.saveAll(false, monitor);
	}

	protected IServer getFirstServer(String serverTypeId) {
		IServer[] servers = ServerCore.getServers();
		assertTrue(servers.length > 0);
		IServer server = servers[0];
		assertNotNull(server);
		assertTrue(server.getServerType().getId().equals(serverTypeId));
		return server;
	}
}