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

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.internal.provisional.ServerLocatorDelegate;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestServerLocatorDelegate;

public class ServerLocatorDelegateTestCase extends TestCase {
	protected static ServerLocatorDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(ServerLocatorDelegateTestCase.class, "ServerLocatorDelegateTestCase");
	}

	public void test00CreateDelegate() throws Exception {
		delegate = new TestServerLocatorDelegate();
	}
	
	public void test01Search() throws Exception {
		delegate.searchForServers("host", null, null);
	}
	
	public void test02Listener() {
		ServerLocatorDelegate.IServerSearchListener listener = new ServerLocatorDelegate.IServerSearchListener() {
			public void serverFound(IServerWorkingCopy server) {
				// ignore
			}
		};
		
		listener.serverFound(null);
	}
}