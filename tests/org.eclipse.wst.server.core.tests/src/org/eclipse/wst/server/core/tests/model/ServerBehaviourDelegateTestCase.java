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

import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.tests.impl.TestServerBehaviourDelegate;

public class ServerBehaviourDelegateTestCase extends TestCase {
	protected static ServerBehaviourDelegate delegate;

	protected ServerBehaviourDelegate getServerBehaviourDelegate() {
		if (delegate == null) {
			delegate = new TestServerBehaviourDelegate();
		}
		return delegate;
	}

	public void testGetServer() throws Exception {
		getServerBehaviourDelegate().getServer();
	}
	
	public void testDispose() throws Exception {
		getServerBehaviourDelegate().dispose();
	}
	
	public void testSetupLaunchConfiguration() throws Exception {
		getServerBehaviourDelegate().setupLaunchConfiguration(null, null);
	}
	
	public void testRestart() throws Exception {
		try {
			getServerBehaviourDelegate().restart(null);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testStop() {
		getServerBehaviourDelegate().stop(false);
	}
	
	public void testTestProtected() {
		((TestServerBehaviourDelegate)delegate).testProtected();
	}
}