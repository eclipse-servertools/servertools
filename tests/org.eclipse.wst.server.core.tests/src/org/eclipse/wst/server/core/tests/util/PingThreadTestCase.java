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

import org.eclipse.wst.server.core.tests.OrderedTestSuite;
import org.eclipse.wst.server.core.util.PingThread;
import junit.framework.Test;
import junit.framework.TestCase;

public class PingThreadTestCase extends TestCase {
	protected static PingThread ping;

	public static Test suite() {
		return new OrderedTestSuite(PingThreadTestCase.class, "PingThreadTestCase");
	}

	public void test00Create() {
		ping = new PingThread(null, null, null, 10);
	}

	public void test01StopPinging() {
		ping.stopPinging();
	}
	
	public void test02TestProtected() {
		class MyPingThread extends PingThread {
			public MyPingThread() {
				super(null, null, null, 0);
			}
			public void testProtected() {
				try {
					ping();
				} catch (Exception e) {
					// ignore
				}
			}
		}
		MyPingThread mpt = new MyPingThread();
		mpt.testProtected();
	}
}