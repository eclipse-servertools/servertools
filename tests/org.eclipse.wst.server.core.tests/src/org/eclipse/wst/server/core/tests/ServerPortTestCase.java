/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests;

import org.eclipse.wst.server.core.ServerPort;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ServerPortTestCase extends TestCase {
	public static Test suite() {
		return new TestSuite(ServerPortTestCase.class, "ServerPortTestCase");
	}
	
	public void test00CreatePort() throws Exception {
		new ServerPort(null, null, 0, null);
	}

	public void test01CreatePort() throws Exception {
		new ServerPort(null, null, 0, null, false);
	}
	
	public void test02CreatePort() throws Exception {
		new ServerPort(null, null, 0, null, null, false);
	}
	
	public void test03CreatePort() throws Exception {
		new ServerPort(null, 0, null);
	}
}