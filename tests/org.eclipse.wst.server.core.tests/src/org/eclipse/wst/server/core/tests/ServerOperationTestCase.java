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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import junit.framework.Test;
import junit.framework.TestCase;

public class ServerOperationTestCase extends TestCase {
	protected static IServer.IOperationListener listener;

	public static Test suite() {
		return new OrderedTestSuite(ServerOperationTestCase.class, "ServerOperationTestCase");
	}

	public void test00Create() throws Exception {
		listener = new IServer.IOperationListener() {
			public void done(IStatus result) {
				// ignore
			}
		};
	}

	public void test01Done() throws Exception {
		listener.done(null);
	}
}