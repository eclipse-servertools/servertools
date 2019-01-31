/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.model.ServerDelegate;
import org.eclipse.wst.server.core.tests.impl.TestServerDelegate;

public class ServerDelegateTestCase extends TestCase {
	protected static ServerDelegate delegate;

	protected ServerDelegate getServerDelegate() {
		if (delegate == null) {
			delegate = new TestServerDelegate();
		}
		return delegate;
	}

	public void testGetServer() throws Exception {
		getServerDelegate().getServer();
	}

	public void testGetServerWorkingCopy() throws Exception {
		getServerDelegate().getServerWorkingCopy();
	}

	public void testDispose() throws Exception {
		getServerDelegate().dispose();
	}

	public void testSetDefaults() throws Exception {
		getServerDelegate().setDefaults(null);
	}

	public void testCanModifyModules() throws Exception {
		getServerDelegate().canModifyModules(null, null);
	}

	public void testGetChildModules() throws Exception {
		getServerDelegate().getChildModules(null);
	}

	public void testGetRootModules() throws Exception {
		getServerDelegate().getRootModules(null);
	}

	public void testGetServerPorts() throws Exception {
		getServerDelegate().getServerPorts();
	}

	public void testModifyModules() throws Exception {
		getServerDelegate().modifyModules(null, null, null);
	}

	public void testImportConfiguration() throws Exception {
		getServerDelegate().importRuntimeConfiguration(null, null);
	}

	public void testImportConfiguration2() throws Exception {
		getServerDelegate().saveConfiguration(null);
	}

	public void testConfigurationChanged() throws Exception {
		getServerDelegate().configurationChanged();
	}

	public void testTestProtected() {
		((TestServerDelegate)getServerDelegate()).testProtected();
	}
}