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
 * to do operations on the monitor.
 * 
 * @since 1.0
 */
public class MonitorCore {
	// [issue: the protocols are stored in here. What if the clients of the API implements a new protocol?]
	public static String TCPIP_PROTOCOL_ID = "TCPIP";
	public static String HTTP_PROTOCOL_ID = "HTTP";

	private static MonitorManager manager = MonitorManager.getInstance();
	
	private static final String lineSeparator = System.getProperty("line.separator");
	
	/**
	 * Returns an array of all the existing monitors.
	 * 
	 * @return an array of monitors
	 */
	public static IMonitor[] getMonitors() {
		List list = manager.getMonitors();
		IMonitor[] m = new IMonitor[list.size()];
		list.toArray(m);
		return m;
	}
	
	/**
	 * Create a new monitor.
	 * 
	 * @return a working copy of the created monitor.
	 */
	public static IMonitorWorkingCopy createMonitor() {
		return manager.createMonitor();
	}
	
	/**
	 * Start the given monitor.
	 * 
	 * @param monitor the monitor to be started.
	 * @throws Exception if the monitor fail to start.
	 */
	public static void startMonitor(IMonitor monitor) throws Exception {
		manager.startMonitor(monitor);
	}
	
	/**
	 * Stop the given monitor.
	 * 
	 * @param monitor the monitor to be stopped.
	 */
	public static void stopMonitor(IMonitor monitor) {
		manager.stopMonitor(monitor);
	}
	
	/**
	 * Return the protocol adapters.
	 * 
	 * @return array of protocol adapters
	 */
	public static IProtocolAdapter[] getProtocolAdapters() {
		return MonitorPlugin.getInstance().getProtocolAdapters();
	}
	
	/**
	 * Return the protocol adapter with the given id.
	 * 
	 * @return protocol adapter
	 */
	public static IProtocolAdapter getProtocolAdapter(String id) {
		return MonitorPlugin.getInstance().getProtocolAdapter(id);
	}
	
	/**
	 * Return the content filters.
	 * 
	 * @return array of content filters
	 */
	public static IContentFilter[] getContentFilters() {
		return MonitorPlugin.getInstance().getContentFilters();
	}
	
	/**
	 * Return the content filter with the given id.
	 * 
	 * @return content filter
	 */
	public static IContentFilter getContentFilter(String id) {
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
	 * Return an array of all requests.
	 * 
	 * @return an array of all requests.
	 */
	public static IRequest[] getRequests() {
		List list = manager.getRequests();
		IRequest[] r = new IRequest[list.size()];
		list.toArray(r);
		return r;
	}
	
	/**
	 * Remove all requests.
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
	 * Parse the given bytes into String form.
	 * 
	 * @param b the input bytes
	 * @return the string after the conversion.
	 */
	public static String parse(byte[] b) {
		if (b == null)
			return "";

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
	 * 
	 * @param request The request that is to be resent.
	 * @return A new IResendRequest based on the specified request.
	 */
	public static IResendRequest createResendRequest(IRequest request) {
	  return new ResendHTTPRequest(request);
	}
}