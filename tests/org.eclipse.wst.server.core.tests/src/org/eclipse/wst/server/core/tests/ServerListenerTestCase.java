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
package org.eclipse.wst.server.core.tests;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;

import junit.framework.TestCase;

public class ServerListenerTestCase extends TestCase {
	public void testListener() {
		IServerListener listener = new IServerListener() {
			public void restartStateChange(IServer server) {
				// ignore
			}

			public void serverStateChange(IServer server) {
				// ignore
			}

			public void modulesChanged(IServer server) {
				// ignore
			}

			public void moduleStateChange(IServer server, IModule[] module) {
				// ignore
			}
		};
		
		listener.restartStateChange(null);
		listener.serverStateChange(null);
		listener.modulesChanged(null);
		listener.moduleStateChange(null, null);
	}
}