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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.wst.internet.monitor.core.internal.MonitorManager;
import org.eclipse.wst.internet.monitor.core.internal.MonitorPlugin;
import org.eclipse.wst.internet.monitor.core.internal.Trace;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
/**
 * Base class for obtaining references to monitor models. This class also provide methods
 * to do operations on a monitor.
 * 
 * @since 1.0
 */
public class MonitorCore {
	/**
	 * Protocol adapter id (value "TCPIP") for TCP/IP. Provided here for convenience;
	 * other protocol adapters may be available.
	 * 
	 * @see #findProtocolAdapter(String)
	 */
	public static String TCPIP_PROTOCOL_ID = "TCPIP";

	/**
	 * Protocol adapter id (value "HTTP") for HTTP. Provided here for convenience;
	 * other protocol adapters may be available.
	 * 
	 * @see #findProtocolAdapter(String)
	 */
	public static String HTTP_PROTOCOL_ID = "HTTP";

	private static MonitorManager manager = MonitorManager.getInstance();
	
	private static final String lineSeparator = System.getProperty("line.separator");
	
	/**
	 * Returns an array of all known monitor instances. The list will not contain any
	 * working copies.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of monitor instances {@link IMonitor}
	 */
	public static IMonitor[] getMonitors() {
		List list = manager.getMonitors();
		IMonitor[] m = new IMonitor[list.size()];
		list.toArray(m);
		return m;
	}
	
	/**
	 * Create a new monitor. The monitor will not exist for use until
	 * the save() method has been called.
	 * 
	 * @return a working copy of the created monitor
	 */
	public static IMonitorWorkingCopy createMonitor() {
		return manager.createMonitor();
	}
	
	/**
	 * Start the given monitor listening on it's client port.
	 * The monitor must not be null.
	 * 
	 * @param monitor the monitor to be started
	 * @throws Exception thrown if the monitor fails to start because the port
	 *    is in use or another problem occurs
	 */
	public static void startMonitor(IMonitor monitor) throws Exception {
		if (monitor == null)
			throw new IllegalArgumentException();
		manager.startMonitor(monitor);
	}
	
	/**
	 * Stop the given monitor and removes all resources.
	 * The monitor must not be null.
	 * 
	 * @param monitor the monitor to be stopped
	 */
	public static void stopMonitor(IMonitor monitor) {
		if (monitor == null)
			throw new IllegalArgumentException();
		manager.stopMonitor(monitor);
	}
	
	/**
	 * Returns an array of all known protocol adapter instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of protocol adapter instances {@link IProtocolAdater}
	 */
	public static IProtocolAdapter[] getProtocolAdapters() {
		return MonitorPlugin.getInstance().getProtocolAdapters();
	}
	
	/**
	 * Returns the protocol adapter with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * protocol adapter ({@link #getProtocolAdapters()}) for the one with a
	 * matching id ({@link IProtocolAdater#getId()}). The id may not be null.
	 *
	 * @param the protocol adapter id
	 * @return the protocol adapter instance, or <code>null</code> if there
	 *   is no protocol adapter with the given id
	 */
	public static IProtocolAdapter findProtocolAdapter(String id) {
		return MonitorPlugin.getInstance().getProtocolAdapter(id);
	}
	
	/**
	 * Returns an array of all known content filters.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of content filter instances {@link IContentFilter}
	 */
	public static IContentFilter[] getContentFilters() {
		return MonitorPlugin.getInstance().getContentFilters();
	}
	
	/**
	 * Returns the content filter with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * content filters ({@link #getContentFilters()}) for the one with a
	 * matching id ({@link IContentFilter#getId()}). The id may not be null.
	 *
	 * @param the content filter id
	 * @return the content filter instance, or <code>null</code> if there
	 *   is no content filter with the given id
	 */
	public static IContentFilter findContentFilter(String id) {
		return MonitorPlugin.getInstance().getContentFilter(id);
	}
	
	/**
	 * Add a monitor listener.
	 * Once registered, a listener starts receiving notification of 
	 * changes to the monitors. The listener continues to receive
	 * notifications until it is removed.
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener the monitor listener
	 * @see #removeMonitorListener(IMonitorListener)
	 */
	public static void addMonitorListener(IMonitorListener listener) {
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
		manager.removeMonitorListener(listener);
	}

	/**
	 * Returns an array of all known request instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of request instances {@link IRequest}
	 */
	public static IRequest[] getRequests() {
		List list = manager.getRequests();
		IRequest[] r = new IRequest[list.size()];
		list.toArray(r);
		return r;
	}
	
	/**
	 * Remove all requests. This method clears all requests and their data
	 * from the buffer and notifies the request listeners.
	 */
	public static void removeAllRequests() {
		manager.removeAllRequests();
	}
	
	/**
	 * Add a request listener.
	 * Once registered, a listener starts receiving notification of 
	 * changes to the requests. The listener continues to receive
	 * notifications until it is removed.
	 * Has no effect if an identical listener is already registered.
	 *
	 * @param listener the request listener
	 * @see #removeRequestListener(IRequestListener)
	 */
	public static void addRequestListener(IRequestListener listener) {
		manager.addRequestListener(listener);
	}

	/**
	 * Removes the given request listener. Has no
	 * effect if the listener is not registered.
	 * 
	 * @param listener the listener
	 * @see #addRequestListener(IRequestListener)
	 */
	public static void removeRequestListener(IRequestListener listener) {
		manager.removeRequestListener(listener);
	}
	
	/**
	 * Convenience method to parse the given bytes into String form. The bytes
	 * are parsed into a line delimited string. The byte array must not be null.
	 * 
	 * @param b a byte array
	 * @return the string after the conversion
	 */
	public static String parse(byte[] b) {
		if (b == null)
			throw new IllegalArgumentException();

		ByteArrayInputStream bin = new ByteArrayInputStream(b);
		BufferedReader br = new BufferedReader(new InputStreamReader(bin));
		StringBuffer sb = new StringBuffer();
		try {
			String s = br.readLine();
			
			while (s != null) {
				sb.append(s);
				s = br.readLine();
				if (s != null)
					sb.append(lineSeparator);
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error parsing input", e);
		}
		
		return sb.toString();
	}

	/**
	 * Create and return an new IResendRequest from the specified request.
	 * The request may not be null.
	 * 
	 * @param request the request that is to be resent
	 * @return a new IResendRequest based on the specified request
	 */
	public static IResendRequest createResendRequest(IRequest request) {
		if (request == null)
			throw new IllegalArgumentException();
		return new ResendHTTPRequest(request);
	}
}