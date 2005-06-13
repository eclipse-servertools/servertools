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

import org.eclipse.wst.server.core.model.ServerDelegate;
import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.tests.impl.TestServerDelegate;

public class ServerDelegateTestCase extends TestCase {
	protected static ServerDelegate delegate;

	public static Test suite() {
		return new OrderedTestSuite(ServerDelegateTestCase.class, "ServerDelegateTestCase");
	}

	public void test00CreateDelegate() throws Exception {
		delegate = new TestServerDelegate();
	}

	public void test03GetServer() throws Exception {
		delegate.getServer();
	}
	
	public void test04GetServerWorkingCopy() throws Exception {
		delegate.getServerWorkingCopy();
	}
	
	public void test11Dispose() throws Exception {
		delegate.dispose();
	}
	
	public void test12SetDefaults() throws Exception {
		delegate.setDefaults(null);
	}
	
	public void test18CanModifyModules() throws Exception {
		delegate.canModifyModules(null, null);
	}
	
	public void test19GetChildModules() throws Exception {
		delegate.getChildModules(null);
	}
	
	public void test20GetRootModules() throws Exception {
		delegate.getRootModules(null);
	}
	
	public void test21GetServerPorts() throws Exception {
		delegate.getServerPorts();
	}
	
	public void test22ModifyModules() throws Exception {
		delegate.modifyModules(null, null, null);
	}
	
	public void test23ImportConfiguration() throws Exception {
		delegate.importConfiguration(null, null);
	}
	
	public void test24ImportConfiguration() throws Exception {
		delegate.saveConfiguration(null);
	}
	
	public void test25ConfigurationChanged() throws Exception {
		delegate.configurationChanged();
	}
	
	public void test26TestProtected() {
		((TestServerDelegate)delegate).testProtected();
	}
}