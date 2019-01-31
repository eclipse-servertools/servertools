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
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.extension;

import junit.framework.TestCase;

import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.core.internal.ServerPlugin;

public class ClientsTestCase extends TestCase {
	public void testClientsExtension() throws Exception {
		IClient[] clients = ServerPlugin.getClients();
		if (clients != null) {
			for (IClient cl : clients)
				System.out.println(cl.getId() + " - " + cl.getName());
		}
	}
}