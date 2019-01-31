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
package org.eclipse.wst.server.core.tests.util;

import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;

import junit.framework.TestCase;

public class ServerLifecycleAdapterTestCase extends TestCase {
	public void testListener() {
		ServerLifecycleAdapter listener = new ServerLifecycleAdapter();
		
		listener.serverAdded(null);
		listener.serverChanged(null);
		listener.serverRemoved(null);
	}
}