/*******************************************************************************
 * Copyright (c) 2005, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.tests.performance.tomcat50;

import java.util.List;
import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.tests.RuntimeLocation;
import org.eclipse.jst.server.tomcat.core.tests.module.ModuleHelper;
import org.eclipse.wst.server.core.*;

public abstract class AbstractTomcatServerTestCase extends TestCase {
	protected static IServer server;

	protected IServer getServer() throws Exception {
		if (server == null) {
			server = createServer();
			
			// test save all
			IServerWorkingCopy wc = server.createWorkingCopy();
			wc.saveAll(false, null);
		}
		
		return server;
	}

	protected abstract String getServerTypeId();

	public static void deleteServer() {
		if (server == null)
			return;
		try {
			server.getRuntime().delete();
			server.delete();
		} catch (Exception e) {
			// ignore
		}
	}

	protected IRuntime createRuntime() throws Exception {
		IServerType st = ServerCore.findServerType(getServerTypeId());
		IRuntimeWorkingCopy wc = st.getRuntimeType().createRuntime(null, null);
		wc.setLocation(new Path(RuntimeLocation.runtimeLocation));
		return wc.save(true, null);
	}

	public IServer createServer() throws Exception {
		IServerType st = ServerCore.findServerType(getServerTypeId());
		IRuntime runtime = createRuntime();
		IServerWorkingCopy wc = st.createServer(null, null, runtime, null);
		
		ServerPort[] ports = wc.getServerPorts(null);
		TomcatServer tomcatServer = wc.getAdapter(TomcatServer.class);
		ITomcatConfigurationWorkingCopy configuration = (ITomcatConfigurationWorkingCopy) tomcatServer.getServerConfiguration();
		// if no ports from the server, use the configuration
		if (ports == null || ports.length == 0) {
			List portsList = configuration.getServerPorts();
			if (portsList != null && portsList.size() > 0) {
				ports = (ServerPort[])portsList.toArray(new ServerPort[portsList.size()]);
			}
		}
		if (ports != null) {
			int size = ports.length;
			for (int i = 0; i < size; i++) {
				configuration.modifyServerPort(ports[i].getId(), 22100 + i);
			}
		}
		
		return wc.save(true, null);
	}

	public void testCreateServer() throws Exception {
		getServer();
		int size = CreateModulesTestCase.NUM_MODULES;
		IModule[] modules = new IModule[size];
		for (int i = 0; i < size; i++) {
			modules[i] = ModuleHelper.getModule(CreateModulesTestCase.WEB_MODULE_NAME + i);
		}
		IServerWorkingCopy wc = getServer().createWorkingCopy();
		wc.modifyModules(modules, null, null);
		wc.save(true, null);
	}
}
