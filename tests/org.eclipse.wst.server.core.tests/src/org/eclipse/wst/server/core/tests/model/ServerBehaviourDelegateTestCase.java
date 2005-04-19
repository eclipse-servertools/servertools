/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.model;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestServerBehaviourDelegate;

public class ServerBehaviourDelegateTestCase extends TestCase {
	protected static ServerBehaviourDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(ServerBehaviourDelegateTestCase.class, "ServerBehaviourDelegateTestCase");
	}

	public void test00CreateDelegate() throws Exception {
		delegate = new TestServerBehaviourDelegate();
	}
	
	public void test01Initialize() throws Exception {
		delegate.initialize();
	}
	
	public void test02Initialize() throws Exception {
		delegate.initialize(new ServerWorkingCopy(new Server(null)));
	}

	public void test03GetServer() throws Exception {
		delegate.getServer();
	}
	
	public void test04Dispose() throws Exception {
		delegate.dispose();
	}
	
	public void test05SetMode() {
		delegate.setMode(null);
	}
	
	public void test06SetServerState() {
		delegate.setServerState(0);
	}
	
	public void test07SetServerPublishState() {
		delegate.setServerPublishState(0);
	}
	
	public void test08SetServerRestartState() {
		delegate.setServerRestartState(false);
	}
	
	public void test09SetModuleState() {
		delegate.setModuleState(null, 0);
	}
	
	public void test10SetModulePublishState() {
		delegate.setModulePublishState(null, 0);
	}
	
	public void test11SetModuleRestartState() {
		delegate.setModuleRestartState(null, false);
	}
	
	public void test12SetModules() {
		delegate.setModules(null);
	}
	
	public void test13PublishStart() throws Exception {
		delegate.publishStart(null);
	}
	
	public void test14PublishServer() throws Exception {
		delegate.publishServer(0, null);
	}
	
	public void test15PublishModule() throws Exception {
		delegate.publishModule(0, 0, null, null);
	}
	
	public void test16PublishFinish() throws Exception {
		delegate.publishFinish(null);
	}
	
	public void test17SetupLaunchConfiguration() throws Exception {
		delegate.setupLaunchConfiguration(null, null);
	}
	
	public void test18Restart() throws Exception {
		try {
			delegate.restart(null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test19RestartModule() throws Exception {
		delegate.restartModule(null, null);
	}
	
	public void test20CanRestartModule() {
		delegate.canRestartModule(null);
	}
	
	public void test21Stop() {
		delegate.stop(false);
	}
	
	public void test22GetPublishedResourceDelta() {
		delegate.getPublishedResourceDelta(null);
	}
	
	public void test23GetPublishedResources() {
		delegate.getPublishedResources(null);
	}
	
	public void test24GetTempDirectory() {
		delegate.getTempDirectory();
	}
	
	public void test25SetServerStatus() {
		delegate.setServerStatus(null);
	}

	public void test26SetModuleStatus() {
		delegate.setModuleStatus(null, null);
	}
}