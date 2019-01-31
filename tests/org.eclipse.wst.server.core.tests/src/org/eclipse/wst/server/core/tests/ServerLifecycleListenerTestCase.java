/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;

import junit.framework.TestCase;

public class ServerLifecycleListenerTestCase extends TestCase {
	public void testListener() {
		IServerLifecycleListener listener = new IServerLifecycleListener() {
			public void serverAdded(IServer server) {
				// ignore
			}

			public void serverChanged(IServer server) {
				// ignore
			}

			public void serverRemoved(IServer server) {
				// ignore
			}
		};
		
		listener.serverAdded(null);
		listener.serverChanged(null);
		listener.serverRemoved(null);
	}
}