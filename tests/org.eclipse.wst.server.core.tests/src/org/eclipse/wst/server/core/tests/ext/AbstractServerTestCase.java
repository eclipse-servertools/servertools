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
package org.eclipse.wst.server.core.tests.ext;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
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
	protected static IProjectProperties props;
	
	protected static IServer server;
	
	public static Test suite() {
		return new OrderedTestSuite(AbstractServerTestCase.class, "AbstractServerTestCase");
	}
	
	protected IServer getServer() throws Exception {
		if (server == null)
			server = createServer();
		
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
		props = ServerCore.getProjectProperties(project);
	}

	public void test0001GetServer() throws Exception {
		assertNull(props.getDefaultServer());
	}

	public void test0002SetServer() throws Exception {
		props.setDefaultServer(getServer(), null);
		assertEquals(props.getDefaultServer(), getServer());
	}

	public void test0003UnSetServer() throws Exception {
		props.setDefaultServer(null, null);
		assertNull(props.getDefaultServer());
	}

	public void test0004End() throws Exception {
		project.delete(true, true, null);
	}

	public void test0005Delegate() throws Exception {
		getServer().getAdapter(ServerDelegate.class);
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
		getServer().synchronousStart(ILaunchManager.DEBUG_MODE, null);
	}
	
	public void test0014CanStop() throws Exception {
		assertTrue(getServer().canStop().isOK());
	}
	
	public void test0015Stop() throws Exception {
		getServer().synchronousStop(false);
	}
	
	public void test0016GetServerPorts() throws Exception {
		IServerPort[] ports = getServer().getServerPorts();
		if (ports != null) {
			int size = ports.length;
			for (int i = 0; i < size; i++) {
				ports[i].getId();
				ports[i].getContentTypes();
				ports[i].getName();
				ports[i].getPort();
				ports[i].getProtocol();
				ports[i].isAdvanced();
			}
		}
	}

	public void test1001Delete() throws Exception {
		deleteServer(getServer());
		server = null;
	}
}