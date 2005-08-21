/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import java.util.List;
import junit.framework.Test;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.tests.module.ModuleTestCase;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.tests.ext.AbstractServerTestCase;

public abstract class AbstractTomcatServerTestCase extends AbstractServerTestCase {
	public static Test suite() {
		return new OrderedTestSuite(AbstractTomcatServerTestCase.class, "TomcatServerTestCase");
	}

	protected abstract String getServerTypeId();

	public void deleteServer(IServer server2) throws Exception {
		server2.getRuntime().delete();
		server2.delete();
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
		TomcatServer tomcatServer = (TomcatServer) wc.getAdapter(TomcatServer.class);
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

	public void test0100CanAddModule() {
		IModule webModule = ModuleTestCase.webModule;
		IStatus status = server.canModifyModules(new IModule[] { webModule }, null, null);
		assertTrue(status.isOK());
	}

	public void test0101HasModule() {
		IModule webModule = ModuleTestCase.webModule;
		IModule[] modules = server.getModules();
		int size = modules.length;
		boolean found = false;
		for (int i = 0; i < size; i++) {
			if (webModule.equals(modules[i]))
				found = true;
		}
		if (found)
			assertTrue(false);
	}

	public void test0102AddModule() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IServerWorkingCopy wc = server.createWorkingCopy();
		wc.modifyModules(new IModule[] { webModule }, null, null);
		wc.save(true, null);
	}

	public void test0103HasModule() {
		IModule webModule = ModuleTestCase.webModule;
		IModule[] modules = server.getModules();
		int size = modules.length;
		boolean found = false;
		for (int i = 0; i < size; i++) {
			if (webModule.equals(modules[i]))
				found = true;
		}
		if (!found)
			assertTrue(false);
	}

	public void test0104RemoveModule() throws Exception {
		IModule webModule = ModuleTestCase.webModule;
		IServerWorkingCopy wc = server.createWorkingCopy();
		wc.modifyModules(null, new IModule[] { webModule }, null);
		wc.save(true, null);
	}

	public void test0105HasModule() {
		IModule webModule = ModuleTestCase.webModule;
		IModule[] modules = server.getModules();
		int size = modules.length;
		boolean found = false;
		for (int i = 0; i < size; i++) {
			if (webModule.equals(modules[i]))
				found = true;
		}
		if (found)
			assertTrue(false);
	}
}