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
package org.eclipse.wst.internet.monitor.core;

import java.util.List;

import org.eclipse.wst.internet.monitor.core.internal.MonitorManager;
import org.eclipse.wst.internet.monitor.core.internal.MonitorPlugin;
import org.eclipse.wst.internet.monitor.core.internal.Request;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
/**
 * Main class for creating new monitors and locating existing ones. The methods on
 * this class are thread safe.
 * <p>
 * This class provides all functionality through static members. It is not intended
 * to be instantiated or subclassed.
 * </p>
 * 
 * @since 1.0
 */
public final class MonitorCore {
	private static MonitorManager manager = MonitorManager.getInstance();

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
		List list = manager.getMonitors();
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
		return manager.createMonitor();
	}

	/**
	 * Returns an array of all known content filters.
	 * <p>
	 * Content filters are registered via the <code>contentFilters</code>
	 * extension point in the <code>org.eclipse.wst.internet.monitor.core</code>
	 * plug-in.
	 * </p>
	 * <p>
	 * A new array is returned on each call; clients may safely store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of content filter instances
	 */
	public static IContentFilter[] getContentFilters() {
		return MonitorPlugin.getInstance().getContentFilters();
	}

	/**
	 * Returns the content filter with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * content filters ({@link #getContentFilters()}) for the one with a
	 * matching id ({@link IContentFilter#getId()})
	 *
	 * @param the content filter id; must not be <code>null</code>
	 * @return the content filter instance, or <code>null</code> if there
	 *   is no content filter with the given id
	 */
	public static IContentFilter findContentFilter(String id) {
		if (id == null)
			throw new IllegalArgumentException();
		return MonitorPlugin.getInstance().findContentFilter(id);
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
		manager.addMonitorListener(listener);
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
		manager.removeMonitorListener(listener);
	}

	/**
	 * Creates a new resend request from the given request.
	 * <p>
	 * [issue: This method seems to be HTTP-specific. It would be hard to
	 * specify what it would mean for other protocols. It also violates the
	 * premise that the monitor merely monitors traffic between client and
	 * server. This method should be deleted.]
	 * </p>
	 * 
	 * @param request the request that is to be resent; may not be <code>null</code>
	 * @return a new resend request
	 */
	public static IResendRequest createResendRequest(IRequest request) {
		if (request == null)
			throw new IllegalArgumentException();
		return new ResendHTTPRequest(((Request)request).getMonitor(), request);
	}
}