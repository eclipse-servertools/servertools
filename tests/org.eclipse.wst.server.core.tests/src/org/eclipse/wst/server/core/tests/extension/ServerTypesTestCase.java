/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;

public class ServerTypesTestCase extends TestCase {
	public void testServerTypesExtension() throws Exception {
		IServerType[] serverTypes = ServerCore.getServerTypes();
		if (serverTypes != null) {
			for (IServerType serverType : serverTypes) {
				serverType.getId();
				serverType.getName();
				serverType.getDescription();
				serverType.getRuntimeType();
				serverType.hasRuntime();
				serverType.hasServerConfiguration();
				serverType.supportsLaunchMode("run");
				serverType.supportsRemoteHosts();
				try {
					serverType.createServer(null, null, null);
					serverType.createServer(null, null, null, null);
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}
}