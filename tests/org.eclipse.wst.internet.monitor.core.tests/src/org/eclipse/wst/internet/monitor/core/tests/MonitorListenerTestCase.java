/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.internet.monitor.core.internal.provisional.*;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MonitorListenerTestCase extends TestCase {
	private static IMonitor monitor;
	
	protected static IMonitor addEvent;
	protected static IMonitor changeEvent;
	protected static IMonitor removeEvent;
	protected static int count;
	
	protected static IMonitorListener listener = new IMonitorListener() {
		public void monitorAdded(IMonitor monitor2) {
			addEvent = monitor2;
			count++;
		}

		public void monitorChanged(IMonitor monitor2) {
			changeEvent = monitor2;
			count++;
		}

		public void monitorRemoved(IMonitor monitor2) {
			removeEvent = monitor2;
			count++;
		}
	};

	public MonitorListenerTestCase() {
		super();
	}

	protected IMonitor getMonitor () throws CoreException {
		if (monitor == null) {
			IMonitorWorkingCopy wc = MonitorCore.createMonitor();
			monitor = wc.save();
		}
		return monitor;
	}

	public static void addOrderedTests(TestSuite suite) {
		suite.addTest(TestSuite.createTest(MonitorListenerTestCase.class, "addListener"));
		suite.addTest(TestSuite.createTest(MonitorListenerTestCase.class, "addListener2"));
		suite.addTest(TestSuite.createTest(MonitorListenerTestCase.class, "listenerCreateMonitor"));
		suite.addTest(TestSuite.createTest(MonitorListenerTestCase.class, "listenerChangeMonitor"));
		suite.addTest(TestSuite.createTest(MonitorListenerTestCase.class, "listenerDeleteMonitor"));
		suite.addTest(TestSuite.createTest(MonitorListenerTestCase.class, "removeListener"));
		suite.addTest(TestSuite.createTest(MonitorListenerTestCase.class, "removeListener2"));
	}

	public void addListener() throws Exception {
		MonitorCore.addMonitorListener(listener);
	}
	
	public void addListener2() throws Exception {
		MonitorCore.addMonitorListener(listener);
	}
	
	public void listenerCreateMonitor() throws Exception {
		getMonitor();
		
		assertTrue(addEvent == monitor);
		assertTrue(changeEvent == null);
		assertTrue(removeEvent == null);
		assertTrue(count == 1);
		addEvent = null;
		count = 0;
	}

	public void listenerChangeMonitor() throws Exception {
		IMonitorWorkingCopy wc = getMonitor().createWorkingCopy();
		wc.setLocalPort(1);
		monitor = wc.save();

		assertTrue(addEvent == null);
		assertTrue(changeEvent == monitor);
		assertTrue(removeEvent == null);
		assertTrue(count == 1);
		changeEvent = null;
		count = 0;
	}

	public void listenerDeleteMonitor() throws Exception {
		getMonitor().delete();
		
		assertTrue(addEvent == null);
		assertTrue(changeEvent == null);
		assertTrue(removeEvent == monitor);
		assertTrue(count == 1);
		removeEvent = null;
		count = 0;
	}
	
	public void removeListener() throws Exception {
		MonitorCore.removeMonitorListener(listener);
	}
	
	public void removeListener2() throws Exception {
		MonitorCore.removeMonitorListener(listener);
	}
	
	public void testCheckListener() throws Exception {
		IMonitorListener listener2 = new IMonitorListener() {
			public void monitorAdded(IMonitor monitor2) {
				// ignore
			}

			public void monitorChanged(IMonitor monitor2) {
				// ignore
			}

			public void monitorRemoved(IMonitor monitor2) {
				// ignore
			}
		};
		
		listener2.monitorAdded(null);
		listener2.monitorChanged(null);
		listener2.monitorRemoved(null);
	}
}
