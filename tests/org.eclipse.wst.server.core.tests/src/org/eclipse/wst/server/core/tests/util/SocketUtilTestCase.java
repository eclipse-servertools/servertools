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
package org.eclipse.wst.server.core.tests.util;

import org.eclipse.wst.server.core.util.SocketUtil;

import junit.framework.TestCase;

public class SocketUtilTestCase extends TestCase {
	public void testFindUnusedPort() {
		int i = SocketUtil.findUnusedPort(22000, 22050);
		assertTrue(i != -1);
	}
	
	public void testIsLocalhost() {
		assertTrue(SocketUtil.isLocalhost("localhost"));
	}
	
	public void testIsLocalhost2() {
		assertTrue(SocketUtil.isLocalhost("127.0.0.1"));
	}
	
	public void testIsLocalhost3() {
		assertFalse(SocketUtil.isLocalhost(null));
	}
	
	public void testIsLocalhost4() {
		assertFalse(SocketUtil.isLocalhost("www.not-eclipse.com"));
	}
	
	public void testIsPortInUse() {
		assertFalse(SocketUtil.isPortInUse(22054));
	}
	
	public void testIsPortInUse2() {
		assertFalse(SocketUtil.isPortInUse(22054, 5));
	}
}