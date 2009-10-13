/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.internet.monitor.core.internal.provisional.*;
import junit.framework.TestCase;
/**
 * Note: use ports between 22100-22200 to ensure they are free on the build machine.
 */
public class MonitorTestCase extends TestCase {
	private static IMonitor monitor;

	public MonitorTestCase() {
		super();
	}

	public void test00GetMonitors() throws Exception {
		assertNotNull(MonitorCore.getMonitors());
		assertEquals(0, MonitorCore.getMonitors().length);
	}

	public void test01CreateMonitor() throws Exception {
		IMonitorWorkingCopy wc = MonitorCore.createMonitor();
		int port = SocketUtil.findUnusedPort(22100, 22200);
		assertTrue("Could not find free local port", port != -1);
		wc.setLocalPort(port);
		wc.setRemoteHost("www.eclipse.org");
		wc.setRemotePort(80);
		monitor = wc.save();
		
		assertTrue(monitor != null);
		assertEquals(1, MonitorCore.getMonitors().length);
		assertTrue(!monitor.isRunning());
		assertTrue(!monitor.isWorkingCopy());
	}

	public void test02GetMonitor() throws Exception {
		int count = 0;
		IMonitor[] monitors = MonitorCore.getMonitors();
		for (IMonitor m : monitors) {
			if (monitor.equals(m))
				count++;
		}
		assertEquals(1, count);
	}
	
	public void test03StartMonitor() throws Exception {
		assertTrue(!monitor.isRunning());
		monitor.start();
		assertTrue(monitor.isRunning());
	}

	public void test04StartMonitor() throws Exception {
		assertTrue(monitor.isRunning());
		monitor.start();
		assertTrue(monitor.isRunning());
	}
	
	public void test05StartMonitor() throws Exception {
		try {
			IMonitorWorkingCopy wc = MonitorCore.createMonitor();
			wc.start();
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test06StartMonitor() throws Exception {
		try {
			IMonitorWorkingCopy wc = MonitorCore.createMonitor();
			IMonitor m = wc.save();
			m.delete();
			m.start();
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test07StopMonitor() throws Exception {
		assertTrue(monitor.isRunning());
		monitor.stop();
		assertTrue(!monitor.isRunning());
	}

	public void test08StopMonitor() throws Exception {
		assertTrue(!monitor.isRunning());
		monitor.stop();
		assertTrue(!monitor.isRunning());
	}

	public void test09RestartMonitor() throws Exception {
		assertTrue(!monitor.isRunning());
		try {
			monitor.start();
		} catch (CoreException ce) {
			// wait 5 seconds and try again
			Thread.sleep(5000);
			monitor.start();
		}
		assertTrue(monitor.isRunning());
		monitor.stop();
		assertTrue(!monitor.isRunning());
	}

	public void test10StopMonitor() throws Exception {
		try {
			IMonitorWorkingCopy wc = MonitorCore.createMonitor();
			wc.stop();
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test11StopMonitor() throws Exception {
		try {
			IMonitorWorkingCopy wc = MonitorCore.createMonitor();
			IMonitor m = wc.save();
			m.delete();
			m.stop();
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}

	public void test12ValidateMonitor() throws Exception {
		assertTrue(monitor.validate().isOK());
	}

	public void test13ModifyMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(1);
		wc.setRemoteHost("a");
		wc.setRemotePort(2);
		IMonitor monitor2 = wc.save();
		
		assertEquals(monitor2, monitor);
		assertEquals(1, monitor.getLocalPort());
		assertEquals("a", monitor.getRemoteHost());
		assertEquals(2, monitor.getRemotePort());
	}

	public void test14DeleteMonitor() throws Exception {
		monitor.delete();
		assertEquals(0, MonitorCore.getMonitors().length);
	}

	public void test15DeleteMonitor() throws Exception {
		monitor.delete();
	}
	
	public void test16DeleteMonitor() throws Exception {
		IMonitorWorkingCopy wc = MonitorCore.createMonitor();
		wc.delete();
	}
	
	public void test17GetMonitor() throws Exception {
		int count = 0;
		IMonitor[] monitors = MonitorCore.getMonitors();
		for (IMonitor m : monitors) {
			if (monitor.equals(m))
				count++;
		}
		assertEquals(0, count);
	}

	public void test18CreateMonitor() throws Exception {
		int num = MonitorCore.getMonitors().length;
		MonitorCore.createMonitor();
		assertEquals(num, MonitorCore.getMonitors().length);
	}
	
	public void test19CreateMonitor() {
		IMonitorWorkingCopy wc = MonitorCore.createMonitor();
		wc.setProtocol(null);
		assertNull(wc.getOriginal());
	}

	public void test20ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(-1);
		wc.setRemoteHost("www.eclipse.org");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void test21ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(80);
		wc.setRemoteHost("www.eclipse.org");
		wc.setRemotePort(-1);
		assertTrue(!wc.validate().isOK());
	}

	public void test22ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost(null);
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void test23ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost("");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void test24ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost("  ");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}
	
	public void test25ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost("hi&bye");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void test26ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost("xyz:");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void test27ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(80);
		wc.setRemoteHost("www.eclipse.org");
		wc.setRemotePort(80);
		assertTrue(wc.validate().isOK());
	}

	public void test28ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(80);
		wc.setRemoteHost("localhost");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}
	
	public void test29ValidateMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.createWorkingCopy();
		wc.setLocalPort(80);
		wc.setRemoteHost("localhost");
		wc.setRemotePort(80);
		IMonitor monitor2 = wc.save();
		try {
			monitor2.start();
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
		monitor2.delete();
	}
	
	public void test30GetId() {
		monitor.getId();
	}

	public void test31GetProtocol() {
		assertNotNull(monitor.getProtocol());
	}
	
	public void test32CheckListener() throws Exception {
		IRequestListener listener2 = new IRequestListener() {
			public void requestAdded(IMonitor monitor2, Request request) {
				// ignore
			}

			public void requestChanged(IMonitor monitor2, Request request) {
				// ignore
			}
		};
		
		listener2.requestAdded(null, null);
		listener2.requestChanged(null, null);
	}
}