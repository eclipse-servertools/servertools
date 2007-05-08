/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.ext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ProjectProperties;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.model.ServerDelegate;
/**
 * Abstract server test case. Use this harness to test a specific server.
 * All you have to do is extend this class, implement the abstract
 * method(s) and add the test case to your suite.
 * <p>
 * You are welcome to add type-specific tests to this method. The test
 * method numbers (i.e. the XX in testXX()) should be between 200 and 1000.
 * </p>
 */
public abstract class AbstractServerTestCase extends TestCase {
	protected static IProject project;
	protected static ProjectProperties props;
	
	protected static IServer server;
	protected static IServerAttributes serverAttr;
	protected static IServerWorkingCopy serverWC;
	
	private static final PropertyChangeListener pcl = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent arg0) {
			// ignore
		}
	};

	private static final IServerListener sl = new IServerListener() {
		public void serverChanged(ServerEvent event) {
			// ignore
		}
	};

	protected IServer getServer() throws Exception {
		if (server == null) {
			server = createServer();
			
			// test save all
			IServerWorkingCopy wc = server.createWorkingCopy();
			wc.saveAll(false, null);
		}
		
		return server;
	}

	public abstract IServer createServer() throws Exception;
	
	public abstract void deleteServer(IServer server2) throws Exception;

	public void test0000GetProperties() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		if (project != null && !project.exists()) {
			project.create(null);
			project.open(null);
		}
		props = ServerPlugin.getProjectProperties(project);
	}

	public void test0004End() throws Exception {
		project.delete(true, true, null);
	}

	public void test0005Delegate() throws Exception {
		getServer().getAdapter(ServerDelegate.class);
	}

	public void test0005bDelegate() throws Exception {
		getServer().loadAdapter(ServerDelegate.class, null);
	}

	public void test0005cDelegate() throws Exception {
		serverAttr = server;
		serverAttr.getAdapter(ServerDelegate.class);
	}

	public void test0005dDelegate() throws Exception {
		serverAttr.loadAdapter(ServerDelegate.class, null);
	}

	public void test0006Delegate() throws Exception {
		getServer().getAdapter(ServerBehaviourDelegate.class);
	}

	public void test0007Publish() throws Exception {
		getServer().publish(IServer.PUBLISH_FULL, null);
	}

	public void test0008CanRun() throws Exception {
		assertTrue(getServer().canStart(ILaunchManager.RUN_MODE).isOK());
	}

	public void test0009Run() throws Exception {
		getServer().synchronousStart(ILaunchManager.RUN_MODE, null);
	}

	public void test0010CanStop() throws Exception {
		assertTrue(getServer().canStop().isOK());
	}

	public void test0011Stop() throws Exception {
		getServer().synchronousStop(false);
	}

	public void test0012CanDebug() throws Exception {
		assertTrue(getServer().canStart(ILaunchManager.DEBUG_MODE).isOK());
	}

	public void test0013Debug() throws Exception {
		server.synchronousStart(ILaunchManager.DEBUG_MODE, null);
	}

	public void test0014CanStop() {
		assertTrue(server.canStop().isOK());
	}

	public void test0015Stop() {
		server.synchronousStop(false);
	}

	public void test0016GetServerPorts() {
		ServerPort[] ports = server.getServerPorts(null);
		if (ports != null) {
			int size = ports.length;
			for (int i = 0; i < size; i++) {
				ports[i].getId();
				ports[i].getContentTypes();
				ports[i].getName();
				ports[i].getPort();
				ports[i].getProtocol();
				ports[i].isAdvanced();
				ports[i].toString();
				ports[i].equals(null);
				ports[i].hashCode();
			}
		}
	}

	public void test0017GetServerState() {
		server.getServerState();
	}

	public void test0018GetServerPublishState() {
		server.getServerPublishState();
	}

	public void test0019GetServerRestartState() {
		server.getServerRestartState();
	}

	public void test0020GetModuleState() {
		try {
			server.getModuleState(null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test0021GetModulePublishState() {
		try {
			server.getModulePublishState(null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test0022GetModuleRestartState() {
		try {
			server.getModuleRestartState(null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test0023GetMode() {
		server.getMode();
	}

	public void test0024CanPublish() {
		server.canPublish();
	}
	
	public void test0025CanRestart() {
		server.canRestart("run");
	}
	
	public void test0026CanControlModule() {
		try {
			server.canControlModule(null, null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test0027AddServerListener() {
		server.addServerListener(sl);
	}

	public void test0028AddServerListener() {
		server.addServerListener(sl, 0);
	}

	public void test0029RemoveServerListener() {
		server.removeServerListener(sl);
	}

	public void test0030GetServerAttributes() {
		serverAttr = server;
	}
	
	public void test0031GetName() {
		serverAttr.getName();
	}
	
	public void test0032GetId() {
		serverAttr.getId();
	}
	
	public void test0033IsReadOnly() {
		serverAttr.isReadOnly();
	}
	
	public void test0034IsWorkingCopy() {
		serverAttr.isWorkingCopy();
	}
	
	public void test0035GetHost() {
		serverAttr.getHost();
	}
	
	public void test0036GetRuntime() {
		serverAttr.getRuntime();
	}
	
	public void test0037GetServerType() {
		serverAttr.getServerType();
	}

	public void test0038GetServerConfiguration() {
		serverAttr.getServerConfiguration();
	}

	public void test0039CreateWorkingCopy() {
		serverAttr.createWorkingCopy();
	}

	public void test0040GetModules() {
		serverAttr.getModules();
	}

	public void test0041CanModifyModules() {
		try {
			serverAttr.canModifyModules(null, null, null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test0042GetChildModules() {
		try {
			serverAttr.getChildModules(null, null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test0043GetRootModules() {
		try {
			serverAttr.getRootModules(null, null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test0044GetServerPorts() {
		serverAttr.getServerPorts(null);
	}
	
	public void test0045Delete() {
		try {
			IServerAttributes sa = serverAttr.createWorkingCopy();
			sa.delete();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test0046CreateWorkingCopy() {
		serverWC = server.createWorkingCopy();
	}
	
	public void test0047IsDirty() {
		assertFalse(serverWC.isDirty());
	}
	
	public void test0048SetName() {
		serverWC.setName("test");
	}
	
	public void test0049SetHost() {
		serverWC.setHost("www.eclipse.org");
	}
	
	public void test0050SetReadOnly() {
		serverWC.setReadOnly(true);
	}
	
	public void test0051IsDirty() {
		assertTrue(serverWC.isDirty());
	}
	
	public void test0052AddPropertyChangeListener() {
		serverWC.addPropertyChangeListener(pcl);
	}
	
	public void test0053RemovePropertyChangeListener() {
		serverWC.removePropertyChangeListener(pcl);
	}
	
	public void test0054GetOriginal() {
		serverWC.getOriginal();
	}

	public void test0055SetServerConfiguration() {
		serverWC.setServerConfiguration(null);
	}
	
	public void test0056ModifyModules() {
		try {
			serverWC.modifyModules(null, null, null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test0057Clear() {
		serverAttr = null;
		serverWC = null;
	}

	public void test1001Delete() throws Exception {
		deleteServer(getServer());
		server = null;
	}
}