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
package org.eclipse.wst.internet.monitor.core.tests;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.eclipse.wst.internet.monitor.core.*;
import junit.framework.Test;
import junit.framework.TestCase;
/**
 * Note: use ports between 22100-22200 to ensure they are free on the build machine.
 */
public class RequestTestCase extends TestCase {
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

	public static Test suite() {
		return new OrderedTestSuite(RequestTestCase.class, "RequestTestCase");
	}
	
	public void test00GetMonitors() throws Exception {
		assertNotNull(MonitorCore.getMonitors());
	}

	public void test01CreateMonitor() throws Exception {
		IMonitorWorkingCopy wc = MonitorCore.createMonitor();
		wc.setLocalPort(22152);
		wc.setRemoteHost("www.eclipse.org");
		wc.setRemotePort(80);
		monitor = wc.save();
		
		assertTrue(monitor != null);
		assertTrue(MonitorCore.getMonitors().length == 1);
		assertTrue(!monitor.isRunning());
		assertTrue(!monitor.isWorkingCopy());
	}
	
	public void test03AddListener() throws Exception {
		monitor.addRequestListener(listener);
	}

	public void test04AddListener() throws Exception {
		monitor.addRequestListener(listener);
	}

	public void test05StartMonitor() throws Exception {
		assertTrue(!monitor.isRunning());
		monitor.start();
		assertTrue(monitor.isRunning());
	}
	
	public void test06Ping() throws Exception {
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
	}

	public void test07CheckListener() throws Exception {
		assertEquals(addCount, 1);
		assertEquals(monitorEvent, monitor);
		assertNotNull(requestEvent);
	}
	
	public void test08VerifyMonitor() throws Exception {
		assertEquals(requestEvent.getMonitor(), monitor);
	}
	
	public void test09VerifyProtocol() throws Exception {
		assertEquals(requestEvent.getProtocol(), "HTTP");
	}

	public void test10VerifyTime() throws Exception {
		// within a minute
		assertTrue(Math.abs(requestEvent.getDate().getTime() - System.currentTimeMillis()) < 1000 * 60);
	}
	
	public void test11VerifyLocalPort() throws Exception {
		assertEquals(requestEvent.getLocalPort(), 22152);
	}
	
	public void test12VerifyRemoteHost() throws Exception {
		assertEquals(requestEvent.getRemoteHost(), "www.eclipse.org");
	}
	
	public void test13VerifyRemotePort() throws Exception {
		assertEquals(requestEvent.getRemotePort(), 80);
	}
	
	public void test14VerifyRequest() throws Exception {
		assertNotNull(requestEvent.getRequest(Request.ALL));
	}
	
	public void test15VerifyResponse() throws Exception {
		assertNotNull(requestEvent.getResponse(Request.ALL));
	}
	
	public void test16VerifyResponseTime() throws Exception {
		assertTrue(requestEvent.getResponseTime() > 0);
	}
	
	public void test17CheckRequest() throws Exception {
		assertNotNull(requestEvent.getName());
	}
	
	public void test18CheckRequest() throws Exception {
		assertNull(requestEvent.getProperty("test"));
	}

	/*public void test19CheckRequest() throws Exception {
		assert(requestEvent.getProperty(""));
	}*/
	
	public void test19AddToRequest() throws Exception {
		requestEvent.addToRequest(new byte[0]);
	}
	
	public void test20AddToResponse() throws Exception {
		requestEvent.addToResponse(new byte[0]);
	}
	
	public void test21SetProperty() throws Exception {
		requestEvent.setProperty("test", null);
	}
	
	public void test22GetAdapter() throws Exception {
		assertNull(requestEvent.getAdapter(String.class));
	}

	public void test23StopMonitor() throws Exception {
		assertTrue(monitor.isRunning());
		monitor.stop();
		assertTrue(!monitor.isRunning());
	}
	
	public void test24RemoveListener() throws Exception {
		monitor.removeRequestListener(listener);
	}
	
	public void test25Create() {
		new Request(null, null, 0, null, 0);
	}
	
	public void test26TestProtectedMethods() {
		Request mr = new Request(null, null, 0, null, 0) {			
			public Object getAdapter(Class c) {
				setName("test");
				setRequest(null);
				setResponse(null);
				fireChangedEvent();
				return super.getAdapter(c);
			}
		};
		mr.getAdapter(null);
	}
}