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
/**
 * Listener for changes affecting the global list of outstanding
 * message traffic between monitored clients and servers.
 * <p>
 * [issue: When the global list of requests goes away, this
 * interface should be "re-purposed" to notify an interested
 * party that a request has just passed between the
 * monitor's client and server; e.g., message(IRequest).
 * ]
 * [issue: This is related to an issue flagged on 
 * MonitorCore.getRequests(). If a client creates a particular
 * monitor, expect them to be interested only in requests on that
 * monitor. Either pass the relevant monitor as a parameter
 * to these methods, or make it easy for a client to find this
 * info out from the IRequest itself.]
 * </p>
 * 
 * @see IMonitor#addRequestListener(IRequestListener)
 * @since 1.0
 */
public interface IRequestListener {
	/**
	 * Notification that the given request was created.
	 * 
	 * @param request the request that has been added
	 */
	public void requestAdded(IRequest request);

	/**
	 * Notification that the given request has been changed.
	 * <p>
	 * [issue: Requests can't change after they are created,
	 * can they?]
	 * </p>
	 * 
	 * @param request the request that has been changed
	 */
	public void requestChanged(IRequest request);
}