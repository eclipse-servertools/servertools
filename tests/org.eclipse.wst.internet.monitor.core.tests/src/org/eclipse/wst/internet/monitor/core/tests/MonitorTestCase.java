/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
import junit.framework.TestSuite;
/**
 * Note: use ports between 22100-22200 to ensure they are free on the build machine.
 */
public class MonitorTestCase extends TestCase {
	private static IMonitor monitor;

	public MonitorTestCase() {
		super();
	}

	protected IMonitor getMonitor() throws Exception {
		if (monitor == null) {
			IMonitorWorkingCopy wc = MonitorCore.createMonitor();
			int port = SocketUtil.findUnusedPort(22100, 22200);
			assertTrue("Could not find free local port", port != -1);
			wc.setLocalPort(port);
			wc.setRemoteHost("www.eclipse.org");
			wc.setRemotePort(80);
			monitor = wc.save();
		}
		return monitor;
	}

	public static void addOrderedTests(TestSuite suite) {
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "getMonitors"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "deleteMonitors"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "createMonitor"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "findMonitor"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "startMonitor"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "startMonitor2"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "stopMonitor"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "stopMonitor2"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "restartMonitor"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "deleteMonitor"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "deleteMonitor2"));
		suite.addTest(TestSuite.createTest(MonitorTestCase.class, "findMonitor2"));
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
		assertEquals(0, MonitorCore.getMonitors().length);
	}

	public void createMonitor() throws Exception {
		assertTrue(getMonitor() != null);
		assertEquals(1, MonitorCore.getMonitors().length);
		assertTrue(!getMonitor().isRunning());
		assertTrue(!getMonitor().isWorkingCopy());
	}

	public void findMonitor() throws Exception {
		int count = 0;
		IMonitor[] monitors = MonitorCore.getMonitors();
		for (IMonitor m : monitors) {
			if (getMonitor().equals(m))
				count++;
		}
		assertEquals(1, count);
	}
	
	public void startMonitor() throws Exception {
		assertTrue(!getMonitor().isRunning());
		getMonitor().start();
		assertTrue(getMonitor().isRunning());
	}

	public void startMonitor2() throws Exception {
		assertTrue(getMonitor().isRunning());
		getMonitor().start();
		assertTrue(getMonitor().isRunning());
	}
	
	public void testStartWorkingCopyMonitor() throws Exception {
		try {
			IMonitorWorkingCopy wc = MonitorCore.createMonitor();
			wc.start();
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testStartWorkingCopyMonitor2() throws Exception {
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

	public void stopMonitor() throws Exception {
		assertTrue(getMonitor().isRunning());
		getMonitor().stop();
		assertTrue(!getMonitor().isRunning());
	}

	public void stopMonitor2() throws Exception {
		assertTrue(!getMonitor().isRunning());
		getMonitor().stop();
		assertTrue(!getMonitor().isRunning());
	}

	public void restartMonitor() throws Exception {
		assertTrue(!getMonitor().isRunning());
		try {
			getMonitor().start();
		} catch (CoreException ce) {
			// wait 5 seconds and try again
			Thread.sleep(5000);
			getMonitor().start();
		}
		assertTrue(getMonitor().isRunning());
		getMonitor().stop();
		assertTrue(!getMonitor().isRunning());
	}

	public void testStopWorkingCopyMonitor() throws Exception {
		try {
			IMonitorWorkingCopy wc = MonitorCore.createMonitor();
			wc.stop();
			assertTrue("Should throw exception", false);
		} catch (Exception e) {
			// ignore
		}
	}

	public void testStopDeletedMonitor() throws Exception {
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

	public void testValidateMonitor() throws Exception {
		assertTrue(getMonitor().validate().isOK());
	}

	public void testModifyMonitor() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(1);
		wc.setRemoteHost("a");
		wc.setRemotePort(2);
		IMonitor monitor2 = wc.save();
		
		assertEquals(monitor2, getMonitor());
		assertEquals(1, getMonitor().getLocalPort());
		assertEquals("a", getMonitor().getRemoteHost());
		assertEquals(2, getMonitor().getRemotePort());
	}

	public void deleteMonitor() throws Exception {
		getMonitor().delete();
		assertEquals(0, MonitorCore.getMonitors().length);
	}

	public void deleteMonitor2() throws Exception {
		getMonitor().delete();
	}
	
	public void testDeleteWorkingCopyMonitor() throws Exception {
		IMonitorWorkingCopy wc = MonitorCore.createMonitor();
		wc.delete();
	}
	
	public void findMonitor2() throws Exception {
		int count = 0;
		IMonitor[] monitors = MonitorCore.getMonitors();
		for (IMonitor m : monitors) {
			if (getMonitor().equals(m))
				count++;
		}
		assertEquals(0, count);
	}

	public void testCreateMonitor() throws Exception {
		int num = MonitorCore.getMonitors().length;
		MonitorCore.createMonitor();
		assertEquals(num, MonitorCore.getMonitors().length);
	}
	
	public void testCreateMonitor2() {
		IMonitorWorkingCopy wc = MonitorCore.createMonitor();
		wc.setProtocol(null);
		assertNull(wc.getOriginal());
	}

	public void testValidateMonitorLocalPort() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(-1);
		wc.setRemoteHost("www.eclipse.org");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void testValidateMonitorRemotePort() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(80);
		wc.setRemoteHost("www.eclipse.org");
		wc.setRemotePort(-1);
		assertTrue(!wc.validate().isOK());
	}

	public void testValidateMonitorRemoteHost() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost(null);
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void testValidateMonitorRemoteHost2() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost("");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void testValidateMonitorRemoteHost3() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost("  ");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}
	
	public void testValidateMonitorRemoteHost4() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost("hi&bye");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void testValidateMonitorRemoteHost5() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(8080);
		wc.setRemoteHost("xyz:");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}

	public void testValidateMonitorRemoteHost6() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(80);
		wc.setRemoteHost("www.eclipse.org");
		wc.setRemotePort(80);
		assertTrue(wc.validate().isOK());
	}

	public void testValidateMonitorLocalHost() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(80);
		wc.setRemoteHost("localhost");
		wc.setRemotePort(80);
		assertTrue(!wc.validate().isOK());
	}
	
	public void testValidateMonitorStartLocalHost() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
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
	
	public void testGetId() throws Exception {
		getMonitor().getId();
	}

	public void testGetProtocol() throws Exception {
		assertNotNull(getMonitor().getProtocol());
	}
	
	public void testCheckListener() throws Exception {
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