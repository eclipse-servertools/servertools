/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
  *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/

package org.eclipse.wst.server.tests.performance.common;

import java.util.List;
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

public abstract class ServerPerformanceTestCase extends PerformanceTestCase
{
  protected void closeIntro()
  {
    IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
    IIntroPart introPart = introManager.getIntro();
    if (introPart != null)
      introManager.closeIntro(introPart);
  }

  protected IRuntimeWorkingCopy createRuntime(String runtimeTypeId, String runtimeTypeLocation) throws CoreException
  {
    IRuntimeWorkingCopy runtimeCopy = ServerCore.getRuntimeType(runtimeTypeId).createRuntime(runtimeTypeId);
    runtimeCopy.setLocation(new Path(runtimeTypeLocation));
    runtimeCopy.setLocked(false);
    runtimeCopy.setTestEnvironment(true);
    runtimeCopy.save(null);
    return runtimeCopy;
  }

  protected IServer createServer(String serverTypeId) throws CoreException
  {
    NullProgressMonitor monitor = new NullProgressMonitor();
    IServerType serverType = ServerCore.getServerType(serverTypeId);
    IServerWorkingCopy serverCopy = serverType.createServer(serverTypeId, null, monitor);
    serverCopy.saveAll(monitor);
    serverCopy.release();
    return serverCopy;
  }

  protected IServer getFirstServer(String serverTypeId)
  {
    List servers = ServerCore.getResourceManager().getServers();
    assertTrue(servers.size() > 0);
    IServer server = (IServer)servers.get(0);
    assertNotNull(server);
    assertTrue(server.getServerType().getId().equals(serverTypeId));
    return server;
  }
}