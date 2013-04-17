/*******************************************************************************
 * Copyright (c) 2005, 2013 IBM Corporation and others.
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
import junit.framework.TestSuite;

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

	// This test suite ensures the test methods are run in order
	public TestSuite getOrderedTests(Class<? extends TestCase> testClass) {
		TestSuite mySuite = new TestSuite();
		mySuite.addTest(TestSuite.createTest(testClass, "testGetProperties"));
		mySuite.addTest(TestSuite.createTest(testClass, "testServerGetDelegate"));
		mySuite.addTest(TestSuite.createTest(testClass, "testServerAttributesGetDelegate"));
		mySuite.addTest(TestSuite.createTest(testClass, "testServerAttributesLoadDelegate"));
		mySuite.addTest(TestSuite.createTest(testClass, "testServerGetBehaviourDelegate"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetServerPorts"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetServerState"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetServerPublishState"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetServerRestartState"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetModuleState"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetModulePublishState"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetModuleRestartState"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetMode"));
		mySuite.addTest(TestSuite.createTest(testClass, "testCanPublish"));
		mySuite.addTest(TestSuite.createTest(testClass, "testCanRestart"));
		mySuite.addTest(TestSuite.createTest(testClass, "testCanControlModule"));
		mySuite.addTest(TestSuite.createTest(testClass, "testAddServerListener"));
		mySuite.addTest(TestSuite.createTest(testClass, "testAddServerListener2"));
		mySuite.addTest(TestSuite.createTest(testClass, "testRemoveServerListener"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetName"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetId"));
		mySuite.addTest(TestSuite.createTest(testClass, "testIsReadOnly"));
		mySuite.addTest(TestSuite.createTest(testClass, "testIsWorkingCopy"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetHost"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetRuntime"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetServerType"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetServerConfiguration"));
		mySuite.addTest(TestSuite.createTest(testClass, "testCreateWorkingCopy"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetModules"));
		mySuite.addTest(TestSuite.createTest(testClass, "testCanModifyModules"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetModules"));
		mySuite.addTest(TestSuite.createTest(testClass, "testCanModifyModules"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetChildModules"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetRootModules"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetServerPorts2"));
		mySuite.addTest(TestSuite.createTest(testClass, "testServerAttributesDelete"));
		mySuite.addTest(TestSuite.createTest(testClass, "testIsDirty"));
		mySuite.addTest(TestSuite.createTest(testClass, "testSetName"));
		mySuite.addTest(TestSuite.createTest(testClass, "testSetName"));
		mySuite.addTest(TestSuite.createTest(testClass, "testSetHost"));
		mySuite.addTest(TestSuite.createTest(testClass, "testSetReadOnly"));
		mySuite.addTest(TestSuite.createTest(testClass, "testServerWCIsDirty"));
		mySuite.addTest(TestSuite.createTest(testClass, "testPropertyChangeListener"));
		mySuite.addTest(TestSuite.createTest(testClass, "testGetOriginal"));
		mySuite.addTest(TestSuite.createTest(testClass, "testSetServerConfiguration"));
		mySuite.addTest(TestSuite.createTest(testClass, "testModifyModules"));
		return mySuite;
	}	
	
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

	protected IServerAttributes getServerAttributes() throws Exception {
		if (serverAttr == null) {
			serverAttr = getServer();
		}
		return serverAttr;
	}

	protected IServerWorkingCopy getServerWorkingCopy() throws Exception {
		if (serverWC == null) {
			serverWC = getServer().createWorkingCopy();
		}
		return serverWC;
	}

	public abstract IServer createServer() throws Exception;
	
	public abstract void deleteServer(IServer server2) throws Exception;

	public static void addOrderedTests(Class<? extends TestCase> testClass, TestSuite suite) {
		suite.addTest(TestSuite.createTest(testClass, "serverPublish"));
		suite.addTest(TestSuite.createTest(testClass, "serverCanRun"));
		suite.addTest(TestSuite.createTest(testClass, "serverRun"));
		suite.addTest(TestSuite.createTest(testClass, "serverCanStop"));
		suite.addTest(TestSuite.createTest(testClass, "serverStop"));
		suite.addTest(TestSuite.createTest(testClass, "serverCanDebug"));
		suite.addTest(TestSuite.createTest(testClass, "serverDebug"));
		suite.addTest(TestSuite.createTest(testClass, "serverCanStop2"));
		suite.addTest(TestSuite.createTest(testClass, "serverStop2"));
		suite.addTest(TestSuite.createTest(testClass, "deleteProject"));
	}

	public static void addFinalTests(Class<? extends TestCase> testClass,TestSuite suite) {
		suite.addTest(TestSuite.createTest(testClass, "clearWorkingCopy"));
		suite.addTest(TestSuite.createTest(testClass, "deleteServer"));
	}

	public void testGetProperties() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		if (project != null && !project.exists()) {
			project.create(null);
			project.open(null);
		}
		props = ServerPlugin.getProjectProperties(project);
	}

	public void deleteProject() throws Exception {
		if (project != null) {
			project.delete(true, true, null);
		}
	}

	public void testServerGetDelegate() throws Exception {
		getServer().getAdapter(ServerDelegate.class);
	}

	public void testServerLoadDelegate() throws Exception {
		getServer().loadAdapter(ServerDelegate.class, null);
	}

	public void testServerAttributesGetDelegate() throws Exception {
		getServerAttributes().getAdapter(ServerDelegate.class);
	}

	public void testServerAttributesLoadDelegate() throws Exception {
		getServerAttributes().loadAdapter(ServerDelegate.class, null);
	}

	public void testServerGetBehaviourDelegate() throws Exception {
		getServer().getAdapter(ServerBehaviourDelegate.class);
	}

	public void serverPublish() throws Exception {
		getServer().publish(IServer.PUBLISH_FULL, null);
	}

	public void serverCanRun() throws Exception {
		assertTrue(getServer().canStart(ILaunchManager.RUN_MODE).isOK());
	}

	public void serverRun() throws Exception {
		getServer().synchronousStart(ILaunchManager.RUN_MODE, null);
	}

	public void serverCanStop() throws Exception {
		assertTrue(getServer().canStop().isOK());
	}

	public void serverStop() throws Exception {
		getServer().synchronousStop(false);
	}

	public void serverCanDebug() throws Exception {
		assertTrue(getServer().canStart(ILaunchManager.DEBUG_MODE).isOK());
	}

	public void serverDebug() throws Exception {
		getServer().synchronousStart(ILaunchManager.DEBUG_MODE, null);
	}

	public void serverCanStop2() throws Exception {
		assertTrue(getServer().canStop().isOK());
	}

	public void serverStop2() throws Exception {
		getServer().synchronousStop(false);
	}

	public void testGetServerPorts() {
		ServerPort[] ports = server.getServerPorts(null);
		if (ports != null) {
			for (ServerPort port : ports) {
				port.getId();
				port.getContentTypes();
				port.getName();
				port.getPort();
				port.getProtocol();
				port.isAdvanced();
				port.toString();
				port.equals(null);
				port.hashCode();
			}
		}
	}

	public void testGetServerState() throws Exception {
		getServer().getServerState();
	}

	public void testGetServerPublishState() throws Exception {
		getServer().getServerPublishState();
	}

	public void testGetServerRestartState() throws Exception {
		getServer().getServerRestartState();
	}

	public void testGetModuleState() {
		try {
			getServer().getModuleState(null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testGetModulePublishState() {
		try {
			getServer().getModulePublishState(null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testGetModuleRestartState() {
		try {
			getServer().getModuleRestartState(null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testGetMode() throws Exception {
		getServer().getMode();
	}

	public void testCanPublish() throws Exception {
		getServer().canPublish();
	}
	
	public void testCanRestart() throws Exception {
		getServer().canRestart("run");
	}
	
	public void testCanControlModule() {
		try {
			getServer().canControlModule(null, null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testAddServerListener() throws Exception {
		getServer().addServerListener(sl);
	}

	public void testAddServerListener2() throws Exception {
		getServer().addServerListener(sl, 0);
	}

	public void testRemoveServerListener() throws Exception {
		getServer().removeServerListener(sl);
	}

	public void testGetName() throws Exception {
		getServerAttributes().getName();
	}
	
	public void testGetId() throws Exception {
		getServerAttributes().getId();
	}
	
	public void testIsReadOnly() throws Exception {
		getServerAttributes().isReadOnly();
	}
	
	public void testIsWorkingCopy() throws Exception {
		getServerAttributes().isWorkingCopy();
	}
	
	public void testGetHost() throws Exception {
		getServerAttributes().getHost();
	}
	
	public void testGetRuntime() throws Exception {
		getServerAttributes().getRuntime();
	}
	
	public void testGetServerType() throws Exception {
		getServerAttributes().getServerType();
	}

	public void testGetServerConfiguration() throws Exception {
		getServerAttributes().getServerConfiguration();
	}

	public void testCreateWorkingCopy() throws Exception {
		getServerAttributes().createWorkingCopy();
	}

	public void testGetModules() throws Exception {
		getServerAttributes().getModules();
	}

	public void testCanModifyModules() {
		try {
			getServerAttributes().canModifyModules(null, null, null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testGetChildModules() {
		try {
			getServerAttributes().getChildModules(null, null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testGetRootModules() {
		try {
			getServerAttributes().getRootModules(null, null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void testGetServerPorts2() throws Exception {
		getServerAttributes().getServerPorts(null);
	}
	
	public void testServerAttributesDelete() {
		try {
			IServerAttributes sa = getServerAttributes().createWorkingCopy();
			sa.delete();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void testIsDirty() throws Exception {
		assertFalse(getServerWorkingCopy().isDirty());
	}
	
	public void testSetName() throws Exception {
		getServerWorkingCopy().setName("test");
	}
	
	public void testSetHost() throws Exception {
		getServerWorkingCopy().setHost("www.eclipse.org");
	}
	
	public void testSetReadOnly() throws Exception {
		getServerWorkingCopy().setReadOnly(true);
	}
	
	public void testServerWCIsDirty() throws Exception {
		assertTrue(getServerWorkingCopy().isDirty());
	}
	
	public void testPropertyChangeListener() throws Exception {
		getServerWorkingCopy().addPropertyChangeListener(pcl);

		getServerWorkingCopy().removePropertyChangeListener(pcl);
	}
	
	public void testGetOriginal() throws Exception{
		getServerWorkingCopy().getOriginal();
	}

	public void testSetServerConfiguration() throws Exception {
		getServerWorkingCopy().setServerConfiguration(null);
	}
	
	public void testModifyModules() {
		try {
			getServerWorkingCopy().modifyModules(null, null, null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void clearWorkingCopy() {
		serverAttr = null;
		serverWC = null;
	}

	public void deleteServer() throws Exception {
		deleteServer(getServer());
		server = null;
	}
}