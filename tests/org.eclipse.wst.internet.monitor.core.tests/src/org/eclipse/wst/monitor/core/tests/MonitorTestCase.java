/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.monitor.core.tests;

import org.eclipse.wst.monitor.core.IMonitor;
import org.eclipse.wst.monitor.core.IMonitorListener;
import org.eclipse.wst.monitor.core.IMonitorWorkingCopy;
import org.eclipse.wst.monitor.core.MonitorCore;
import junit.framework.Test;
import junit.framework.TestCase;

public class MonitorTestCase extends TestCase {
	private static IMonitor monitor;
	
	protected static IMonitor addEvent;
	protected static IMonitor changeEvent;
	protected static IMonitor removeEvent;

	protected static IMonitorListener listener = new IMonitorListener() {
		public void monitorAdded(IMonitor monitor2) {
			addEvent = monitor2;
		}

		public void monitorChanged(IMonitor monitor2) {
			changeEvent = monitor2;
		}

		public void monitorRemoved(IMonitor monitor2) {
			removeEvent = monitor2;
		}
	};

	public MonitorTestCase() {
		super();
	}

	public static Test suite() {
		return new OrderedTestSuite(MonitorTestCase.class, "MonitorTestCase");
	}

	public void test00AddMonitorListener() throws Exception {
		MonitorCore.addMonitorListener(listener);
	}

	public void test01CreateMonitor() throws Exception {
		IMonitorWorkingCopy wc = MonitorCore.createMonitor();
		wc.setLocalPort(7781);
		wc.setRemoteHost("www.eclipse.org");
		wc.setRemotePort(80);
		monitor = wc.save();
		
		assertTrue(monitor != null);
		assertTrue(MonitorCore.getMonitors().length == 1);
		assertTrue(!monitor.isRunning());
		assertTrue(!monitor.isWorkingCopy());
	}
	
	public void test02CheckListener() throws Exception {
		assertTrue(addEvent == monitor);
		assertTrue(changeEvent == null);
		assertTrue(removeEvent == null);
		addEvent = null;
	}

	public void test03StartMonitor() throws Exception {
		assertTrue(!monitor.isRunning());
		MonitorCore.startMonitor(monitor);
		assertTrue(monitor.isRunning());
	}

	public void test04StopMonitor() throws Exception {
		assertTrue(monitor.isRunning());
		MonitorCore.stopMonitor(monitor);
		assertTrue(!monitor.isRunning());
	}
	
	public void test05ModifyMonitor() throws Exception {
		IMonitorWorkingCopy wc = monitor.getWorkingCopy();
		wc.setLocalPort(1);
		wc.setRemoteHost("a");
		wc.setRemotePort(2);
		IMonitor monitor2 = wc.save();
		
		assertTrue(monitor2 == monitor);
		assertTrue(monitor.getLocalPort() == 1);
		assertTrue(monitor.getRemoteHost().equals("a"));
		assertTrue(monitor.getRemotePort() == 2);
	}
	
	public void test06CheckListener() throws Exception {
		assertTrue(addEvent == null);
		assertTrue(changeEvent == monitor);
		assertTrue(removeEvent == null);
		changeEvent = null;
	}

	public void test07DeleteMonitor() throws Exception {
		monitor.delete();
		assertTrue(MonitorCore.getMonitors().length == 0);
	}

	public void test08CheckListener() throws Exception {
		assertTrue(addEvent == null);
		assertTrue(changeEvent == null);
		assertTrue(removeEvent == monitor);
		removeEvent = null;
	}

	public void test09CreateMonitor() throws Exception {
		int num = MonitorCore.getMonitors().length;
		MonitorCore.createMonitor();
		assertTrue(MonitorCore.getMonitors().length == num);
	}
	
	public void test10RemoveMonitorListener() throws Exception {
		MonitorCore.removeMonitorListener(listener);
	}
}