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

	public void test03GetServer() throws Exception {
		delegate.getServer();
	}
	
	public void test04Dispose() throws Exception {
		delegate.dispose();
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

	public void test21Stop() {
		delegate.stop(false);
	}
	
	public void test27TestProtected() {
		((TestServerBehaviourDelegate)delegate).testProtected();
	}
}