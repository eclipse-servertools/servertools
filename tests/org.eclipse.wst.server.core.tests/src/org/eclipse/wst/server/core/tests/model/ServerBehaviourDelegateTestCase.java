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
}