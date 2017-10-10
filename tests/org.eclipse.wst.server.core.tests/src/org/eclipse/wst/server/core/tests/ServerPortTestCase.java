/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
import junit.framework.TestCase;

public class ServerPortTestCase extends TestCase {
	public void test00CreatePort() throws Exception {
		new ServerPort(null, null, 0, null);
	}

	public void test02CreatePort() throws Exception {
		new ServerPort(null, null, 0, null, null, false);
	}
}