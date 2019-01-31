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

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.internal.provisional.ServerLocatorDelegate;
import org.eclipse.wst.server.core.tests.impl.TestServerLocatorDelegate;

public class ServerLocatorDelegateTestCase extends TestCase {
	protected static ServerLocatorDelegate delegate;

	protected ServerLocatorDelegate getServerLocatorDelegate() {
		if (delegate == null) {
			delegate = new TestServerLocatorDelegate();
		}
		return delegate;
	}

	public void testSearch() throws Exception {
		getServerLocatorDelegate().searchForServers("host", null, null);
	}
	
	public void testListener() {
		ServerLocatorDelegate.IServerSearchListener listener = new ServerLocatorDelegate.IServerSearchListener() {
			public void serverFound(IServerWorkingCopy server) {
				// ignore
			}
		};
		
		listener.serverFound(null);
	}
}