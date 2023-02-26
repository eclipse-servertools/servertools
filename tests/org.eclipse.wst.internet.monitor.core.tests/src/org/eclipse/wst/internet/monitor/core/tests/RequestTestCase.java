/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
package org.eclipse.wst.internet.monitor.core.tests;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.internet.monitor.core.internal.provisional.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Note: use ports between 22100-22200 to ensure they are free on the build machine.
 */
public class RequestTestCase extends TestCase {
	private static final String CONNECT_TIMEOUT = "sun.net.client.defaultConnectTimeout";
	private static final String READ_TIMEOUT = "sun.net.client.defaultReadTimeout";

	private static IMonitor monitor;

	protected static IMonitor monitorEvent;
	protected static Request requestEvent;
	protected static int addCount;
	protected static int changeCount;

	protected static IRequestListener listener = new IRequestListener() {
		public void requestAdded(IMonitor monitor2, Request request2) {
			monitorEvent = monitor2;
			requestEvent = request2;
			addCount++;
		}

		public void requestChanged(IMonitor monitor2, Request request2) {
			monitorEvent = monitor2;
			requestEvent = request2;
			changeCount++;
		}
	};

	public RequestTestCase() {
		super();
	}

	protected IMonitor getMonitor() throws CoreException {
		if (monitor == null) {
			IMonitorWorkingCopy wc = MonitorCore.createMonitor();
			wc.setLocalPort(22152);
			wc.setRemoteHost("www.eclipse.org");
			wc.setRemotePort(80);
			monitor = wc.save();
		}
		return monitor;
	}

	public static void addOrderedTests(TestSuite suite) {
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "deleteMonitors"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "getMonitors"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "createMonitor"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "addListener"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "addListener2"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "startMonitor"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "pingMonitor"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "checkListener"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "verifyMonitor"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "verifyProtocol"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "verifyTime"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "verifyLocalPort"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "verifyRemoteHost"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "verifyRemotePort"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "verifyRequest"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "verifyResponse"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "verifyResponseTime"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "checkRequest"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "checkRequest2"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "addToRequest"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "addToResponse"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "setProperty"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "getAdapter"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "stopMonitor"));
		suite.addTest(TestSuite.createTest(RequestTestCase.class, "removeListener"));
	}

	public void deleteMonitors() {
		IMonitor [] monitors = MonitorCore.getMonitors();
		for (int i = 0; i < monitors.length; i++) {
			monitors[i].delete();
		}
		monitor = null;
	}

	public void getMonitors() throws Exception {
		assertNotNull(MonitorCore.getMonitors());
	}

	public void createMonitor() throws Exception {
		getMonitor();

		assertTrue(monitor != null);
		assertTrue(MonitorCore.getMonitors().length == 1);
		assertTrue(!monitor.isRunning());
		assertTrue(!monitor.isWorkingCopy());
	}

	public void addListener() throws Exception {
		monitor.addRequestListener(listener);
	}

	public void addListener2() throws Exception {
		monitor.addRequestListener(listener);
	}

	public void startMonitor() throws Exception {
		assertTrue(!monitor.isRunning());
		monitor.start();
		assertTrue(monitor.isRunning());
	}

	public void pingMonitor() throws Exception {
		String connectTimeout = System.getProperty(CONNECT_TIMEOUT);
		String readTimeout = System.getProperty(READ_TIMEOUT);

		System.setProperty(CONNECT_TIMEOUT, "10000"); // 10000ms = 10s
		System.setProperty(READ_TIMEOUT, "10000");

		URL url = new URL("http://localhost:22152/");
		//URL url = new URL("http://www.eclipse.org/");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();

		// read the server's response
		System.out.println("Response from www.eclipse.org ----------------------------------");
		InputStream in = conn.getInputStream();
		byte[] b = new byte[256];
		int n = in.read(b);
		System.out.println(new String(b));
		while (n >= 0) {
			n = in.read(b);
			System.out.println(new String(b));
		}
		in.close();
		System.out.println("End of response from www.eclipse.org ---------------------------");

		try {
			System.setProperty(CONNECT_TIMEOUT, connectTimeout);
			System.setProperty(READ_TIMEOUT, readTimeout);
		} catch (Exception e) {
			// ignore - JDK bug on some systems doesn't allow null
		}
	}

	public void checkListener() throws Exception {
		assertEquals(1, addCount);
		assertEquals(monitorEvent, monitor);
		assertNotNull(requestEvent);
	}

	public void verifyMonitor() throws Exception {
		assertEquals(requestEvent.getMonitor(), monitor);
	}

	public void verifyProtocol() throws Exception {
		assertEquals(requestEvent.getProtocol(), "HTTP");
	}

	public void verifyTime() throws Exception {
		// within a minute
		assertTrue(Math.abs(requestEvent.getDate().getTime() - System.currentTimeMillis()) < 1000 * 60);
	}

	public void verifyLocalPort() throws Exception {
		assertEquals(requestEvent.getLocalPort(), 22152);
	}

	public void verifyRemoteHost() throws Exception {
		assertEquals(requestEvent.getRemoteHost(), "www.eclipse.org");
	}

	public void verifyRemotePort() throws Exception {
		assertEquals(requestEvent.getRemotePort(), 80);
	}

	public void verifyRequest() throws Exception {
		assertNotNull(requestEvent.getRequest(Request.ALL));
	}

	public void verifyResponse() throws Exception {
		assertNotNull(requestEvent.getResponse(Request.ALL));
	}

	public void verifyResponseTime() throws Exception {
		assertTrue("ResponseTime was " + requestEvent.getResponseTime(), requestEvent.getResponseTime() > 0);
	}

	public void checkRequest() throws Exception {
		assertNotNull(requestEvent.getName());
	}

	public void checkRequest2() throws Exception {
		assertNull(requestEvent.getProperty("test"));
	}

	/*public void test19CheckRequest() throws Exception {
		assert(requestEvent.getProperty(""));
	}*/

	public void addToRequest() throws Exception {
		requestEvent.addToRequest(new byte[0]);
	}

	public void addToResponse() throws Exception {
		requestEvent.addToResponse(new byte[0]);
	}

	public void setProperty() throws Exception {
		requestEvent.setProperty("test", null);
	}

	public void getAdapter() throws Exception {
		assertNull(requestEvent.getAdapter(String.class));
	}

	public void stopMonitor() throws Exception {
		assertTrue(monitor.isRunning());
		monitor.stop();
		assertTrue(!monitor.isRunning());
	}

	public void removeListener() throws Exception {
		monitor.removeRequestListener(listener);
	}

	public void testCreateRequest() {
		new Request(null, null, 0, null, 0);
	}

	public void testTestProtectedMethods() {
		Request mr = new Request(null, null, 0, null, 0) {
			public Object getAdapter(Class c) {
				setName("test");
				setRequest(null);
				setResponse(null);
				fireChangedEvent();
				return null;
			}
		};
		mr.getAdapter(null);
	}
}