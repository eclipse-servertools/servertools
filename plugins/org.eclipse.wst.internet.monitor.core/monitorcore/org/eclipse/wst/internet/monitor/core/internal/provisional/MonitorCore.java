/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.provisional;

import java.util.List;

import org.eclipse.wst.internet.monitor.core.internal.MonitorManager;
/**
 * Main class for creating new monitors and locating existing ones. The methods on
 * this class are thread safe.
 * <p>
 * This class provides all functionality through static members. It is not intended
 * to be instantiated or subclassed.
 * </p>
 */
public final class MonitorCore {
	private static MonitorManager manager;

	/**
	 * Cannot create MonitorCore - use static methods.
	 */
	private MonitorCore() {
		// can't create
	}

	/**
	 * Returns a monitor manager instance.
	 * 
	 * @return the monitor manager
	 */
	private static MonitorManager getManager() {
		if (manager == null)
			manager = MonitorManager.getInstance();
		return manager;
	}

	/**
	 * Returns a list of all known monitor instances. The list will not contain any
	 * working copies and is persisted between workbench sessions.
	 * <p>
	 * A new array is returned on each call; clients may safely store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of monitor instances
	 */
	public static IMonitor[] getMonitors() {
		List<IMonitor> list = getManager().getMonitors();
		IMonitor[] m = new IMonitor[list.size()];
		list.toArray(m);
		return m;
	}

	/**
	 * Creates a new monitor working copy. After configuring parameters on
	 * the working copy, calling {@link IMonitorWorkingCopy#save()} brings
	 * the monitor into existence.
	 * <p>
	 * Note that the client is responsible for calling {@link IMonitor#delete()}
	 * to delete the monitor once it is no longer needed.
	 * </p>
	 * <p>
	 * When monitors are created, the local and remote port values default to
	 * <code>80</code>, but they do not have a protocol or remote host (values
	 * are <code>null</code>).
	 * </p> 
	 * 
	 * @return a monitor working copy
	 */
	public static IMonitorWorkingCopy createMonitor() {
		return getManager().createMonitor();
	}

	/**
	 * Adds a monitor listener.
	 * Once registered, a listener starts receiving notification of 
	 * changes to the monitors. The listener continues to receive
	 * notifications until it is removed.
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener the monitor listener
	 * @see #removeMonitorListener(IMonitorListener)
	 */
	public static void addMonitorListener(IMonitorListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		getManager().addMonitorListener(listener);
	}

	/**
	 * Removes the given monitor listener. Has no
	 * effect if the listener is not registered.
	 * 
	 * @param listener the listener
	 * @see #addMonitorListener(IMonitorListener)
	 */
	public static void removeMonitorListener(IMonitorListener listener) {
		if (listener == null)
			throw new IllegalArgumentException();
		getManager().removeMonitorListener(listener);
	}
}
